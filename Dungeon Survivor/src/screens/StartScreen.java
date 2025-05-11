package src.screens;

import java.awt.*;
import src.main.GamePanel;

public class StartScreen {

    GamePanel gp;
    Font buttonsFont;
    Font titleFont;
    String titleText;
    String playText;
    String levelText;
    String quitText;
    public int commandNum = 0;

    public StartScreen(GamePanel gp) {
        this.gp = gp;
        this.buttonsFont = new Font("Segoe UI", Font.BOLD, 48);
        this.titleFont = new Font("Georgia", Font.BOLD, 90);
        titleText = "Dungeon Survivor";
        playText = "PLAY";
        levelText = "LEVEL";
        quitText = "QUIT";
    }

    public void draw(Graphics2D g2) {
        // Градиентный фон
        GradientPaint gpBackground = new GradientPaint(
                0, 0, new Color(30, 30, 30),
                0, gp.screenHeight, new Color(70, 70, 70)
        );
        g2.setPaint(gpBackground);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Заголовок с тенью
        g2.setFont(titleFont);
        int x = getXforCenteredText(titleText, g2);
        int y = gp.tileSize * 3;

        g2.setColor(Color.darkGray);
        g2.drawString(titleText, x + 5, y + 5);
        g2.setColor(new Color(255, 215, 0)); // Золотистый
        g2.drawString(titleText, x, y);

        // Линия под заголовком
        g2.setColor(Color.white);
        g2.fillRect(gp.screenWidth / 4, y + 10, gp.screenWidth / 2, 5);

        // Кнопки
        g2.setFont(buttonsFont);
        y += gp.tileSize * 3;

        drawButton(g2, playText, y, 0);
        y += gp.tileSize * 2;

        drawButton(g2, levelText, y, 1);
        y += gp.tileSize * 2;

        drawButton(g2, quitText, y, 2);
    }

    private void drawButton(Graphics2D g2, String text, int y, int index) {
        int x = getXforCenteredText(text, g2);
        boolean selected = (commandNum == index);

        if (selected) {
            // Закруглённый фон кнопки
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillRoundRect(x - 40, y - 45, gp.tileSize * 5, 60, 20, 20);

            // Стрелка выбора
            g2.setColor(Color.orange);
            g2.drawString(">", x - gp.tileSize, y);
        }

        // Текст кнопки
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
    }

    public int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return (gp.screenWidth - length) / 2;
    }
}
