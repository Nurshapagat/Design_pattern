package src.main;

import java.awt.Graphics2D;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import src.entity.Dragon;
import src.entity.Entity;
import src.entity.skeleton;
import src.entity.Goblin;
import src.entity.King;
import src.tile.TileManager;

public class MonstersManager {

    GamePanel gp;
    public Entity[] monsters;
    public int monster_remaning;

    public MonstersManager(GamePanel gp) {
        this.gp = gp;
        setMonsters();
    }

    public void setMonsters() {
        if (gp == null || gp.tileM == null) {
            System.err.println("MonstersManager: GamePanel or TileManager is null, cannot set monsters.");
            monsters = new Entity[0]; // Создаем пустой массив, чтобы избежать NullPointerException дальше
            monster_remaning = 0;
            return;
        }

        int bossCol = -1;
        int bossRow = -1;
        List<int[]> bossLocations = new ArrayList<>();

        if (gp.tileM.mapTileNum != null && gp.tileM.tiles != null) {
            for (int col = 0; col < gp.maxScreenCol; col++) {
                for (int row = 0; row < gp.maxScreenRow; row++) {
                    int tileNumOnMap = gp.tileM.mapTileNum[col][row];
                    if (tileNumOnMap >= 0 && tileNumOnMap < gp.tileM.tiles.length &&
                            gp.tileM.tiles[tileNumOnMap] != null &&
                            gp.tileM.tiles[tileNumOnMap].symbol == TileManager.boss_tile) {
                        bossLocations.add(new int[]{col, row});
                    }
                }
            }
        }

        if (!bossLocations.isEmpty()) {
            int[] chosenBossLocation = bossLocations.get(0);
            bossCol = chosenBossLocation[0];
            bossRow = chosenBossLocation[1];
            System.out.println("Boss (Zork) found at map tile: (" + bossCol + ", " + bossRow + ")");
        } else {
            System.err.println("Boss tile (symbol '" + TileManager.boss_tile + "') not found! Placing Zork at default (1,10).");
            bossCol = 1;
            bossRow = 10;
        }

        monster_remaning = Dragon.number + skeleton.number + Goblin.number;
        int dragonNum = Dragon.number;
        int gargoyleNum = skeleton.number;
        int goblinNum = Goblin.number;
        int total_num_other_monsters = monster_remaning;

        monsters = new Entity[total_num_other_monsters + 1];

        monsters[0] = new King(gp, bossCol, bossRow);
        // monsters[0].visable = true; // Раскомментировать, если босс виден сразу

        Random random = new Random();
        int currentMonsterArrayIndex = 1;

        int placementAttempts = 0; // Счетчик попыток, чтобы избежать бесконечного цикла
        final int MAX_PLACEMENT_ATTEMPTS = total_num_other_monsters * 100; // Ограничение попыток

        while(currentMonsterArrayIndex <= total_num_other_monsters && placementAttempts < MAX_PLACEMENT_ATTEMPTS) {
            placementAttempts++;
            int tryCol = random.nextInt(gp.maxScreenCol);
            int tryRow = random.nextInt(gp.maxScreenRow);
            int tileNumOnMap = gp.tileM.mapTileNum[tryCol][tryRow];

            boolean isBossTile = (tryCol == bossCol && tryRow == bossRow);
            boolean isPlayerStartZone = (
                    (tryCol == 17 && tryRow == 1) || (tryCol == 16 && tryRow == 1) ||
                            (tryCol == 17 && tryRow == 2) || (tryCol == 16 && tryRow == 2)
            );
            boolean isTooCloseToBoss = (Math.abs(tryCol - bossCol) <= 1 && Math.abs(tryRow - bossRow) <= 1) && !isBossTile;

            if (isBossTile || isPlayerStartZone || isTooCloseToBoss) {
                continue;
            }

            boolean allowedPlacement = true;
            // --- ИСПРАВЛЕНИЕ ЗДЕСЬ ---
            for(int i = 0; i < currentMonsterArrayIndex; i++) {
                if(monsters[i] != null) {
                    if(monsters[i].getX() == tryCol*gp.tileSize && monsters[i].getY() == tryRow*gp.tileSize) { // Используем геттеры
                        allowedPlacement = false;
                        break;
                    }
                }
            }
            // --- КОНЕЦ ИСПРАВЛЕНИЯ ---

            if(allowedPlacement && tileNumOnMap >= 0 && tileNumOnMap < gp.tileM.tiles.length && // Добавил проверку границ для tileNumOnMap
                    gp.tileM.tiles[tileNumOnMap] != null && gp.tileM.tiles[tileNumOnMap].symbol == TileManager.map_tile) {

                boolean monsterPlacedThisIteration = false;
                // Пытаемся разместить монстра, начиная с самого "редкого" или по порядку
                if (dragonNum > 0) {
                    monsters[currentMonsterArrayIndex] = new Dragon(gp, tryCol, tryRow);
                    dragonNum--;
                    monsterPlacedThisIteration = true;
                } else if (gargoyleNum > 0) {
                    monsters[currentMonsterArrayIndex] = new skeleton(gp, tryCol, tryRow);
                    gargoyleNum--;
                    monsterPlacedThisIteration = true;
                } else if (goblinNum > 0) {
                    monsters[currentMonsterArrayIndex] = new Goblin(gp, tryCol, tryRow);
                    goblinNum--;
                    monsterPlacedThisIteration = true;
                }

                if (monsterPlacedThisIteration) {
                    currentMonsterArrayIndex++;
                    placementAttempts = 0; // Сбросить счетчик попыток после успешного размещения
                }
            }
        }
        if (placementAttempts >= MAX_PLACEMENT_ATTEMPTS) {
            System.err.println("MonstersManager: Could not place all monsters. Check map space or placement logic. Placed: " + (currentMonsterArrayIndex-1) + "/" + total_num_other_monsters);
            // Обнуляем счетчик оставшихся монстров, чтобы не было несоответствия
            monster_remaning = currentMonsterArrayIndex - 1;
        }
    }

    public void draw(Graphics2D g2) {
        if (monsters == null) return; // Защита
        for(Entity e : monsters) {
            if(e != null) {
                e.draw(g2);
            }
        }
    }
}