package src.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import src.main.GamePanel; // Убедитесь, что GamePanel импортирован

public class Mine extends Entity {

    GamePanel gp;

    public static final int number = 5;
    private final int damage = 100;

    private int initialCol, initialRow;

    public Mine(GamePanel gp, int col, int row){
        this.gp = gp;
        this.initialCol = col;
        this.initialRow = row;
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues();

        this.x = initialCol * gp.tileSize;
        this.y = initialRow * gp.tileSize;
        this.symbol = MINE;
        this.visable = false;
        this.max_hit_point = 1;
        this.hit_point = 1;
    }
    @Override
    public void draw(Graphics2D g2) {
    }
    public int getDamage() {
        return damage;
    }
}