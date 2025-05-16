package src.main;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.entity.Entity;

public class UI {

	GamePanel gp;
	Font font;
	Font smallFont;
	Font largeFont;
	BufferedImage[] redDice;
	BufferedImage[] blueDice;

	public UI(GamePanel gp) {
		this.gp = gp;
		this.font = new Font("Arial", Font.BOLD | Font.ITALIC, 28);
		this.smallFont = new Font("Arial", Font.BOLD, 20);
		this.largeFont = new Font("Arial", Font.BOLD | Font.ITALIC, 80);
		getDiceImage();
	}

	public void getDiceImage() {
		redDice = new BufferedImage[6];
		blueDice = new BufferedImage[6];
		try {
			for (int i = 0; i < 6; i++) {
				redDice[i] = ImageIO.read(getClass().getResourceAsStream("/res/dice/r1.jpg"));
				blueDice[i] = ImageIO.read(getClass().getResourceAsStream("/res/dice/r2.jpg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Dice images not found! Check paths /res/dice/");
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {
		if (gp == null || gp.player == null || gp.monstersM == null || gp.battleM == null || redDice == null || blueDice == null) {
			return;
		}

		int yPos = 40;
		drawGlowingText(g2, "HP: " + gp.player.hit_point + "/" + gp.player.max_hit_point, 20, yPos);
		yPos += 45;
		drawGlowingText(g2, "Lvl: " + gp.player.level, 20, yPos);

		if (gp.player.gameState == gp.player.BATTLE && gp.battleM.monsterIndex >= 0
				&& gp.battleM.monsterIndex < gp.monstersM.monsters.length
				&& gp.monstersM.monsters[gp.battleM.monsterIndex] != null) {

			Entity currentMonster = gp.monstersM.monsters[gp.battleM.monsterIndex];
			String monsterHP = currentMonster.symbol + " HP: " + currentMonster.hit_point + "/" + currentMonster.max_hit_point;

			int xPosMonsterHP = gp.screenWidth - getXoffsetForRightAlignedText(monsterHP, g2, font) - 20;
			int yPosMonsterHP = 40;
			drawGlowingText(g2, monsterHP, xPosMonsterHP, yPosMonsterHP);
		}

		if (gp.player.gameState == gp.player.BATTLE && gp.battleM.monsterIndex >= 0
				&& gp.battleM.monsterIndex < gp.monstersM.monsters.length
				&& gp.monstersM.monsters[gp.battleM.monsterIndex] != null) {

			Entity currentMonster = gp.monstersM.monsters[gp.battleM.monsterIndex];
			int currentBattleState = gp.battleM.battleState;

			g2.setFont(smallFont);

			String playerActionMsg = gp.battleM.getLastPlayerActionInfo();
			if (!playerActionMsg.isEmpty()) {
				drawGlowingText(g2, playerActionMsg, gp.screenWidth / 2 - getCenteredTextXOffset(playerActionMsg, g2, smallFont), gp.screenHeight - gp.tileSize - 70, new Color(255, 80, 80), new Color(200, 0, 0));
			}

			String monsterAttackMsg = gp.battleM.getLastMonsterAttackInfo();
			if (!monsterAttackMsg.isEmpty()) {
				drawGlowingText(g2, monsterAttackMsg, gp.screenWidth / 2 - getCenteredTextXOffset(monsterAttackMsg, g2, smallFont), gp.screenHeight - gp.tileSize * 2 - 95, new Color(255, 100, 100), new Color(180, 0, 0));
			}

			g2.setFont(font);
			Color promptColor = new Color(255, 220, 100);
			String battlePrompt = "";
			if (currentBattleState == 0) battlePrompt = "Roll Initiative! (R)";
			else if (currentBattleState == 1) battlePrompt = "ACTION: (R)Attack ";
			else if (currentBattleState == 2 && !gp.battleM.rolling) battlePrompt = currentMonster.symbol + "'s Turn...";
			else if (currentBattleState == 3) battlePrompt = "Simultaneous Roll! (R)";
			else if (currentBattleState == 4 && !gp.battleM.rolling && (gp.player.dice[0] == 0 && gp.player.dice[1] == 0))
				battlePrompt = "Roll to Heal HP! (R)";
			else if (gp.battleM.rolling && currentBattleState != 2 && battleStateToString(currentBattleState).contains("Player"))
				battlePrompt = "Player Rolling...";
			else if (gp.battleM.rolling && currentBattleState == 2) battlePrompt = currentMonster.symbol + " Rolling...";
			else if (gp.battleM.rolling && currentBattleState == 3) battlePrompt = "Both Rolling...";

			if (!battlePrompt.isEmpty()) {
				drawGlowingText(g2, battlePrompt, gp.screenWidth / 2 - getCenteredTextXOffset(battlePrompt, g2, font), gp.screenHeight - 30, promptColor, new Color(150, 100, 0));
			}
		}
	}

	private void drawGlowingText(Graphics2D g2, String text, int x, int y) {
		drawGlowingText(g2, text, x, y, new Color(255, 230, 100), new Color(255, 140, 0));
	}

	private void drawGlowingText(Graphics2D g2, String text, int x, int y, Color mainColor, Color glowColor) {
		g2.setFont(font);

		for (int i = 6; i >= 1; i--) {
			float alpha = 0.05f * i;
			g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), (int) (alpha * 255)));
			g2.drawString(text, x + i, y + i);
		}

		GradientPaint gradient = new GradientPaint(x, y - 20, mainColor, x, y + 10, glowColor, false);
		g2.setPaint(gradient);
		g2.drawString(text, x, y);

		g2.setColor(Color.BLACK);
		Stroke originalStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(2f));
		g2.draw(new TextLayout(text, font, g2.getFontRenderContext()).getOutline(AffineTransform.getTranslateInstance(x, y)));
		g2.setStroke(originalStroke);
	}

	private String battleStateToString(int state) {
		switch (state) {
			case 0:
				return "Initiative";
			case 1:
				return "Player Action Select";
			case 11:
				return "Player Dice Attack";
			case 13:
				return "Player Strong Attack";
			case 2:
				return "Monster Turn";
			case 3:
				return "Simultaneous";
			case 4:
				return "Player Heal Post-Battle";
			default:
				return "Unknown State";
		}
	}

	private int getCenteredTextXOffset(String text, Graphics2D g2, Font currentFont) {
		if (text == null || g2 == null || currentFont == null) return 0;
		Font oldFont = g2.getFont();
		g2.setFont(currentFont);
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont);
		return length / 2;
	}

	private int getXoffsetForRightAlignedText(String text, Graphics2D g2, Font currentFont) {
		if (text == null || g2 == null || currentFont == null) return 0;
		Font oldFont = g2.getFont();
		g2.setFont(currentFont);
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont);
		return length;
	}

	public void drawTransitionMessage(Graphics2D g2, String message) {
		if (message == null || g2 == null || largeFont == null || gp == null) return;
		g2.setFont(largeFont);
		g2.setColor(new Color(0, 0, 0, 200));
		g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
		g2.setColor(Color.YELLOW);
		int x = getXforCenteredText(message, g2, largeFont);
		int y = gp.screenHeight / 2;
		g2.drawString(message, x, y);
	}

	public int getXforCenteredText(String text, Graphics2D g2, Font customFont) {
		if (text == null || g2 == null || customFont == null || gp == null) return 0;
		Font oldFont = g2.getFont();
		g2.setFont(customFont);
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont);
		int x = (gp.screenWidth - length) / 2;
		return x;
	}
}
