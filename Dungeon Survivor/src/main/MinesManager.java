package src.main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import src.entity.Mine; // Убедитесь, что Mine импортирован
import src.entity.Entity; // Для проверки instanceof Entity при проверке монстров
import src.tile.TileManager; // Для проверки символа тайла

public class MinesManager {

    GamePanel gp;
    public Mine[] mines;
    int total_num; // Общее количество мин для текущего уровня

    // Поля для анимации взрыва
    boolean explosion;
    int explosionX;
    int explosionY;
    int explosionCounter;
    BufferedImage[] images;

    public MinesManager(GamePanel gp) {
        this.gp = gp;
        explosionCounter = 0;
        explosion = false; // Изначально взрыва нет
        loadExplosionImages(); // Загружаем картинки взрыва
        setMines(); // Размещаем мины
    }

    /**
     * Загружает изображения для анимации взрыва.
     */
    void loadExplosionImages() {
        images = new BufferedImage[13]; // 13 кадров анимации
        try {
            // Предполагается, что изображения находятся в /res/explosion/1.png, /res/explosion/2.png, ...
            for (int i = 0; i < 13; i++) {
                String path = "/res/explosion/" + (i + 1) + ".png";
                images[i] = ImageIO.read(getClass().getResourceAsStream(path));
                if (images[i] == null) { // Проверка, если ресурс не найден
                    System.err.println("Could not load explosion image: " + path);
                }
            }
        } catch (IOException | IllegalArgumentException e) { // Ловим возможные исключения
            System.err.println("Error loading explosion images: " + e.getMessage());
            e.printStackTrace();
            images = null; // Устанавливаем в null, чтобы избежать NullPointerException при отрисовке
        }
    }

    /**
     * Создает и размещает мины на карте.
     */
    void setMines() {
        if (gp == null || gp.tileM == null || gp.monstersM == null) {
            System.err.println("MinesManager: Cannot set mines, critical components missing (gp, tileM, or monstersM).");
            mines = new Mine[0];
            total_num = 0;
            return;
        }

        total_num = Mine.number; // Используем статическое поле из класса Mine
        mines = new Mine[total_num];
        if (total_num == 0) {
            System.out.println("MinesManager: Number of mines is set to 0.");
            return; // Если мин 0, ничего не делаем
        }

        Random random = new Random();
        int placedCount = 0;
        int attempts = 0;
        final int MAX_ATTEMPTS_PER_MINE = 100; // Макс. попыток найти место для ОДНОЙ мины

        while(placedCount < total_num && attempts < MAX_ATTEMPTS_PER_MINE * total_num) {
            attempts++;
            int col = random.nextInt(gp.maxScreenCol);
            int row = random.nextInt(gp.maxScreenRow);

            // Проверка валидности координат и тайла
            if (gp.tileM.mapTileNum == null || col < 0 || col >= gp.maxScreenCol || row < 0 || row >= gp.maxScreenRow) continue;
            int tileNumOnMap = gp.tileM.mapTileNum[col][row];
            if (tileNumOnMap < 0 || tileNumOnMap >= gp.tileM.tiles.length || gp.tileM.tiles[tileNumOnMap] == null) continue; // Невалидный тайл

            // 1. Проверка типа тайла (должен быть проходимый 'map_tile')
            if (gp.tileM.tiles[tileNumOnMap].symbol != TileManager.map_tile) {
                continue;
            }

            // 2. Проверка запрещенных зон (старт игрока, зона босса)
            boolean isForbiddenZone = checkForbiddenZones(col, row);
            if (isForbiddenZone) continue;

            // 3. Проверка наложения на монстров
            boolean overlapsMonster = checkMonsterOverlap(col, row);
            if (overlapsMonster) continue;

            // 4. Проверка наложения на уже размещенные мины
            boolean overlapsMine = checkMineOverlap(col, row, placedCount);
            if (overlapsMine) continue;

            // Если все проверки пройдены - размещаем мину
            mines[placedCount] = new Mine(gp, col, row);
            // System.out.println("Placed mine " + (placedCount + 1) + " at (" + col + "," + row + ")"); // Для отладки
            placedCount++;
            attempts = 0; // Сбросить счетчик общих попыток при успехе
        }

        if (placedCount < total_num) {
            System.err.println("MinesManager: Could not place all " + total_num + " mines. Placed: " + placedCount);
            // Обрезаем массив или обрабатываем ситуацию иначе
            Mine[] actualMines = new Mine[placedCount];
            System.arraycopy(mines, 0, actualMines, 0, placedCount);
            mines = actualMines;
            total_num = placedCount;
        } else {
            // System.out.println("MinesManager: Successfully placed " + placedCount + " mines."); // Для отладки
        }
    }

    /** Вспомогательный метод проверки запрещенных зон */
    private boolean checkForbiddenZones(int col, int row) {
        // Player Start zone
        if ((col == 17 && row == 1) || (col == 16 && row == 1) ||
                (col == 17 && row == 2) || (col == 16 && row == 2)) {
            return true;
        }
        // Zork zone (если босс существует)
        Entity boss = (gp.monstersM != null && gp.monstersM.monsters != null && gp.monstersM.monsters.length > 0) ? gp.monstersM.monsters[0] : null;
        if (boss != null && boss.symbol == Entity.ZORK) {
            int bossCol = boss.getX() / gp.tileSize;
            int bossRow = boss.getY() / gp.tileSize;
            // Запретить ставить на босса и в радиусе 1 клетки вокруг него
            if (Math.abs(col - bossCol) <= 1 && Math.abs(row - bossRow) <= 1) {
                return true;
            }
        }
        return false;
    }

    /** Вспомогательный метод проверки наложения на монстров */
    private boolean checkMonsterOverlap(int col, int row) {
        if (gp.monstersM != null && gp.monstersM.monsters != null) {
            for(Entity monster : gp.monstersM.monsters) {
                if(monster != null) {
                    if(monster.getX() == col*gp.tileSize && monster.getY() == row*gp.tileSize) {
                        return true; // Найдено наложение
                    }
                }
            }
        }
        return false; // Наложений не найдено
    }

    /** Вспомогательный метод проверки наложения на другие мины */
    private boolean checkMineOverlap(int col, int row, int currentPlacedCount) {
        if (mines != null) {
            for(int i = 0; i < currentPlacedCount; i++) {
                if(mines[i] != null) {
                    if(mines[i].getX() == col*gp.tileSize && mines[i].getY() == row*gp.tileSize) {
                        return true; // Найдено наложение
                    }
                }
            }
        }
        return false; // Наложений не найдено
    }

    /**
     * Активирует флаг и координаты для анимации взрыва.
     * @param mine Мина, которая взорвалась.
     */
    public void setExplosion(Mine mine) {
        if (mine == null) return;
        explosion = true;
        explosionX = mine.getX(); // Используем геттеры
        explosionY = mine.getY();
        explosionCounter = 0; // Сбросить счетчик анимации
        if(gp != null) gp.playSE(3); // Звук взрыва (убедитесь, что индекс 3 верный)
    }

    /**
     * Рисует анимацию взрыва, если он активен.
     * @param g2 Графический контекст для отрисовки.
     */
    public void explode(Graphics2D g2) {
        if (!explosion || images == null || images.length == 0 || gp == null) { // Если нет взрыва или картинки не загружены
            return;
        }

        // Проверка, загружены ли изображения (хотя бы первое)
        if (images[0] == null) {
            System.err.println("Explosion images not loaded, cannot draw explosion.");
            explosion = false; // Отключаем взрыв, чтобы не спамить ошибками
            return;
        }

        int frameIndex = explosionCounter % images.length; // Получаем индекс текущего кадра

        // Проверка, что конкретный кадр не null (на случай частичной загрузки)
        if (images[frameIndex] != null) {
            g2.drawImage(images[frameIndex], explosionX, explosionY, gp.tileSize, gp.tileSize, null);
        } else {
            // Можно нарисовать заглушку, если кадр не загружен
            // g2.setColor(Color.ORANGE);
            // g2.fillRect(explosionX, explosionY, gp.tileSize, gp.tileSize);
        }

        explosionCounter++;

        // Завершение анимации взрыва (например, после 3 полных циклов + немного)
        // images.length * 3 = 39 кадров (3 цикла)
        // Увеличим до 40-50 для небольшой задержки после последнего кадра
        if(explosionCounter >= images.length * 3 + 5) { // Примерно 3.5 секунды при 1 кадре/тик
            explosionCounter = 0;
            explosion = false; // Завершаем взрыв
            // Проверка смерти игрока ПОСЛЕ анимации взрыва (если урон нанесен в CollisionChecker)
            if (gp.player != null && gp.player.hit_point <= 0) {
                if (gp.panelState != gp.END) { // Чтобы не перезаписывать сообщение о смерти в бою
                    gp.player.hit_point = 0; // Убедимся, что не отрицательное
                    gp.es.setTitleTexts("You Died on a Mine! :(\nSteps: " + gp.player.steps + " Lvl: " + gp.player.level);
                    gp.panelState = gp.END;
                }
            }
        }
    }
}