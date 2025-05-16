package src.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.main.GamePanel;

public class King extends Entity{
    GamePanel gp;


    private int initialCol, initialRow;

    public King(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.initialCol = col;
        this.initialRow = row;
        setDefaultValues();
        getImage();
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues();

        this.x = 22 * gp.tileSize;
        this.y = 1 * gp.tileSize;

        max_hit_point = 500;
        hit_point = max_hit_point;
        visable = false;

        symbol = KING;


        experienceDropped = 500;
        critChance = 0.15;
        critDamageMultiplier = 1.75;

    }

    public void getImage() {
        try {
            standingUp = new BufferedImage[2];
            standingUp[0] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));
            standingUp[1] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));

            standingDown = new BufferedImage[2];
            standingDown[0] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));
            standingDown[1] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));

            standingLeft = new BufferedImage[2];
            standingLeft[0] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));
            standingLeft[1] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));

            standingRight = new BufferedImage[2];
            standingRight[0] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));
            standingRight[1] = ImageIO.read(getClass().getResourceAsStream("/res/king.png"));

        }catch(IOException e) {
            e.printStackTrace();
        }catch(IllegalArgumentException e) {
            System.err.println("Error loading king images: " + e.getMessage());
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