package src.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.main.GamePanel;

public class skeleton extends Entity{

    GamePanel gp;
    public static final int number = 5;

    private int initialCol, initialRow;

    public skeleton(GamePanel gp, int col, int row) {
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

        max_hit_point = 150;
        hit_point = max_hit_point;
        visable = false;
        direction = LEFT;
        symbol = GARGOYLE;

        experienceDropped = 50;
        critChance = 0.06;
        critDamageMultiplier = 1.5;
    }

    public void getImage() {
        try {
            standingUp = new BufferedImage[2];
            standingUp[0] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));
            standingUp[1] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));

            standingDown = new BufferedImage[2];
            standingDown[0] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));
            standingDown[1] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));

            standingLeft = new BufferedImage[2];
            standingLeft[0] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));
            standingLeft[1] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));

            standingRight = new BufferedImage[2];
            standingRight[0] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));
            standingRight[1] = ImageIO.read(getClass().getResourceAsStream("/res/skeleton.png"));

        }catch(IOException e) {
            e.printStackTrace();
        }catch(IllegalArgumentException e) {
            System.err.println("Error loading gargoyle images: " + e.getMessage());
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
}