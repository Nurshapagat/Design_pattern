package src.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.main.GamePanel;

public class Goblin extends Entity{

    GamePanel gp;
    public static final int number = 10;

    private int initialCol, initialRow;

    public Goblin(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.initialCol = col;
        this.initialRow = row;
        setDefaultValues();
        getImage();
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues();
        this.x = initialCol * gp.tileSize;
        this.y = initialRow * gp.tileSize;

        max_hit_point = 100;
        hit_point = max_hit_point;
        visable = false;
        direction = UP;
        symbol = GOBLIN;

        experienceDropped = 25;
        critChance = 0.05;
        critDamageMultiplier = 1.5;

    }

    public void getImage() {
        try {
            standingUp = new BufferedImage[2];
            standingUp[0] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));
            standingUp[1] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));

            standingDown = new BufferedImage[2];
            standingDown[0] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));
            standingDown[1] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));

            standingLeft = new BufferedImage[2];
            standingLeft[0] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));
            standingLeft[1] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));

            standingRight = new BufferedImage[2];
            standingRight[0] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));
            standingRight[1] = ImageIO.read(getClass().getResourceAsStream("/res/goblin.png"));

        }catch(IOException e) {
            e.printStackTrace();
        }catch(IllegalArgumentException e) {
            System.err.println("Error loading goblin images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if(!visable)
            return;

        spriteCounter++;
        if(spriteCounter > 12) {
            spriteNum = (spriteNum + 1) % 2;
            spriteCounter = 0;
        }

        BufferedImage image = null;
        switch(direction) {
            case UP:    image = standingUp[spriteNum];    break;
            case DOWN:  image = standingDown[spriteNum];  break;
            case LEFT:  image = standingLeft[spriteNum];  break;
            case RIGHT: image = standingRight[spriteNum]; break;
            default:    image = standingDown[spriteNum];  break;
        }

        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }

    public void attackPlayer() {

    }

    public void moveTowardPlayer() {

    }
}