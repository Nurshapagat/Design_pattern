package src.screens;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.main.GamePanel;

public class StartScreen {

    GamePanel gp;
    BufferedImage titleImage;
    Font buttonsFont;
    public String[] menuTexts = {"PLAY", "LEVEL", "QUIT"};
    public int commandNum = 0;

    public StartScreen(GamePanel gp) {
        this.gp = gp;
        buttonsFont = new Font("Segoe UI", Font.BOLD, 48);
        try {
            titleImage = ImageIO.read(getClass().getResourceAsStream("/res/phone.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        if (titleImage != null) {
            g2.drawImage(titleImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            GradientPaint gpBackground = new GradientPaint(
                    0, 0, new Color(10, 10, 20),
                    0, gp.screenHeight, new Color(40, 40, 60)
            );
            g2.setPaint(gpBackground);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }

        g2.setFont(buttonsFont);
        int yStart = gp.screenHeight / 2;
        int buttonHeight = gp.tileSize * 2;
        int buttonWidth = gp.tileSize * 6;

        for (int i = 0; i < menuTexts.length; i++) {
            int x = (gp.screenWidth - buttonWidth) / 2;
            int y = yStart + i * (buttonHeight + 20);

            if (commandNum == i) {
                g2.setColor(new Color(255, 165, 0, 180)); // Полупрозрачный оранжевый
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 30, 30);
                g2.setColor(Color.white);
            } else {
                g2.setColor(new Color(50, 50, 50, 180));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 30, 30);
                g2.setColor(new Color(200, 200, 200));
            }

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(menuTexts[i]);
            int textX = x + (buttonWidth - textWidth) / 2;
            int textY = y + ((buttonHeight - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(menuTexts[i], textX, textY);
        }
    }

}
