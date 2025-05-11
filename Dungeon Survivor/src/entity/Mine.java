package src.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import src.main.GamePanel; // Убедитесь, что GamePanel импортирован

public class Mine extends Entity {

    GamePanel gp;

    // --- ВОССТАНОВЛЕННОЕ ПОЛЕ ---
    public static final int number = 5; // Количество мин, которые нужно создать на уровне
    // --- КОНЕЦ ВОССТАНОВЛЕНИЯ ---

    private final int damage = 100; // Урон от этой мины

    private int initialCol, initialRow; // Для установки координат в setDefaultValues

    public Mine(GamePanel gp, int col, int row){
        // Сохраняем GamePanel и начальные координаты
        this.gp = gp;
        this.initialCol = col;
        this.initialRow = row;
        // Вызываем стандартизированный метод инициализации
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        // Вызываем общий метод сброса из родительского класса Entity
        super.setDefaultValues();

        // Устанавливаем специфичные для мины значения
        this.x = initialCol * gp.tileSize; // Устанавливаем координату X
        this.y = initialRow * gp.tileSize; // Устанавливаем координату Y
        this.symbol = MINE;                // Устанавливаем символ сущности
        this.visable = false;              // Мины по умолчанию невидимы
        this.max_hit_point = 1;            // Мина уничтожается при срабатывании
        this.hit_point = 1;                // Начальное "здоровье" мины
        // Остальные общие поля (direction, moving и т.д.) установлены в super.setDefaultValues()
    }

    /**
     * Метод отрисовки мины. По умолчанию мины невидимы.
     * Логика отрисовки взрыва находится в MinesManager.
     * Можно раскомментировать для отладки или если мины должны быть видны.
     */
    @Override
    public void draw(Graphics2D g2) {
        /*
        if (visable && gp != null) {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(getX(), getY(), gp.tileSize, gp.tileSize);
        }
        */
    }

    /**
     * Возвращает количество урона, наносимого этой миной.
     * @return Урон мины.
     */
    public int getDamage() {
        return damage;
    }
}