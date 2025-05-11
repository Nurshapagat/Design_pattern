package src.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import src.entity.Entity;

public class UI{

	GamePanel gp;
	Font font;
	Font smallFont;
	Font largeFont;
	BufferedImage[] redDice;
	BufferedImage[] blueDice;

	public UI(GamePanel gp) {
		this.gp = gp;
		this.font = new Font("Arial", Font.PLAIN, 18);
		this.smallFont = new Font("Arial", Font.BOLD, 14);
		this.largeFont = new Font("Arial", Font.BOLD, 60);
		getDiceImage();
	}

	public void getDiceImage() {
		redDice = new BufferedImage[6];
		blueDice = new BufferedImage[6];
		try {
			for(int i=0; i<6; i++) {
				redDice[i] = ImageIO.read(getClass().getResourceAsStream("/res/dice/r1.jpg"));
				blueDice[i] = ImageIO.read(getClass().getResourceAsStream("/res/dice/r2.jpg"));
			}
		}catch(IOException e) { e.printStackTrace(); }
		catch(IllegalArgumentException e) {System.err.println("Dice images not found! Check paths /res/dice/"); e.printStackTrace();}
	}

	public void draw(Graphics2D g2) {
		if (gp == null || gp.player == null || gp.monstersM == null || gp.battleM == null || redDice == null || blueDice == null) {
			// System.err.println("UI Draw: Critical component is null"); // Для отладки
			return;
		}

		g2.setFont(font);
		g2.setColor(Color.white);

		int yPos = 20;
		g2.drawString("HP: " + gp.player.hit_point + "/" + gp.player.max_hit_point, 20, yPos);
		yPos += 20;
		g2.drawString("MP: " + gp.player.mana + "/" + gp.player.maxMana, 20, yPos);
		yPos += 20;
		g2.drawString("Lvl: " + gp.player.level, 20, yPos);
		yPos += 20;
		g2.drawString("XP: " + gp.player.experience + "/" + gp.player.experienceToNextLevel, 20, yPos);

		yPos = 20;
		String stepsText = "Steps: " + gp.player.steps;
		g2.drawString(stepsText, gp.screenWidth - getXoffsetForRightAlignedText(stepsText, g2, font) - 20, yPos);
		yPos += 20;
		String monstersText = "Monsters: " + gp.monstersM.monster_remaning;
		g2.drawString(monstersText, gp.screenWidth - getXoffsetForRightAlignedText(monstersText, g2, font) - 20, yPos);
		yPos += 20;
		String mapLvlText = "Map Lvl: " + gp.currentLevel;
		g2.drawString(mapLvlText, gp.screenWidth - getXoffsetForRightAlignedText(mapLvlText, g2, font) - 20, yPos);


		if(gp.player.gameState == gp.player.BATTLE && gp.battleM.monsterIndex >= 0 && gp.battleM.monsterIndex < gp.monstersM.monsters.length && gp.monstersM.monsters[gp.battleM.monsterIndex] != null) {
			Entity currentMonster = gp.monstersM.monsters[gp.battleM.monsterIndex];
			int currentBattleState = gp.battleM.battleState;

			// Кости игрока
			if (currentBattleState == 0 || currentBattleState == 11 || currentBattleState == 13 || currentBattleState == 3 || currentBattleState == 4 ) {
				if (currentBattleState == 0) {
					if(gp.player.dice[0] > 0 && gp.player.dice[0] <= redDice.length) g2.drawImage(redDice[gp.player.dice[0]-1], gp.screenWidth - gp.tileSize*2 - 15 , gp.screenHeight - gp.tileSize*2 - 40, gp.tileSize, gp.tileSize, null);
				} else {
					if(gp.player.dice[0] > 0 && gp.player.dice[0] <= redDice.length) g2.drawImage(redDice[gp.player.dice[0]-1], gp.screenWidth - gp.tileSize*2 - 15 , gp.screenHeight - gp.tileSize*2 - 40, gp.tileSize, gp.tileSize, null);
					if(gp.player.dice[1] > 0 && gp.player.dice[1] <= blueDice.length) g2.drawImage(blueDice[gp.player.dice[1]-1], gp.screenWidth - gp.tileSize - 10, gp.screenHeight - gp.tileSize*2 - 40, gp.tileSize, gp.tileSize, null);
				}
			}

			String monsterHPText = currentMonster.symbol + " HP: " + currentMonster.hit_point + "/" + currentMonster.max_hit_point;
			g2.drawString(monsterHPText, 20, gp.screenHeight - 60);

			// Кости монстра
			if (currentBattleState == 0 || currentBattleState == 2 || currentBattleState == 3) {
				if (currentBattleState == 0) {
					if(currentMonster.dice[0]>0 && currentMonster.dice[0] <= redDice.length) g2.drawImage(redDice[currentMonster.dice[0]-1], 20, gp.screenHeight - gp.tileSize*2 - 75, gp.tileSize, gp.tileSize, null);
				} else {
					if(currentMonster.dice[0]>0 && currentMonster.dice[0] <= redDice.length) g2.drawImage(redDice[currentMonster.dice[0]-1], 20, gp.screenHeight - gp.tileSize*2 - 75, gp.tileSize, gp.tileSize, null);
					if(currentMonster.dice[1]>0 && currentMonster.dice[1] <= blueDice.length) g2.drawImage(blueDice[currentMonster.dice[1]-1], 20 + gp.tileSize + 5, gp.screenHeight - gp.tileSize*2 - 75, gp.tileSize, gp.tileSize, null);
				}
			}

			g2.setFont(smallFont);
			String playerActionMsg = gp.battleM.getLastPlayerActionInfo();
			if (!playerActionMsg.isEmpty()) {
				g2.drawString(playerActionMsg, gp.screenWidth/2 - getCenteredTextXOffset(playerActionMsg, g2, smallFont), gp.screenHeight - gp.tileSize - 70); // Центрируем сообщение игрока
			}
			String monsterAttackMsg = gp.battleM.getLastMonsterAttackInfo();
			if (!monsterAttackMsg.isEmpty()) {
				g2.drawString(monsterAttackMsg, gp.screenWidth/2 - getCenteredTextXOffset(monsterAttackMsg, g2, smallFont), gp.screenHeight - gp.tileSize*2 - 95); // Центрируем сообщение монстра
			}

			g2.setFont(font);
			String battlePrompt = "";
			if (currentBattleState == 0) battlePrompt = "Roll Initiative! (R)";
			else if (currentBattleState == 1) battlePrompt = "ACTION: (R)Attack | (1)Heal:"+BattleManager.HEAL_MANA_COST+"MP | (2)S.Atk:"+BattleManager.STRONG_ATTACK_MANA_COST+"MP";
			else if (currentBattleState == 2 && !gp.battleM.rolling) battlePrompt = currentMonster.symbol + "'s Turn...";
			else if (currentBattleState == 3) battlePrompt = "Simultaneous Roll! (R)";
			else if (currentBattleState == 4 && !gp.battleM.rolling && (gp.player.dice[0] == 0 && gp.player.dice[1] == 0) ) battlePrompt = "Roll to Heal HP! (R)";
			else if (gp.battleM.rolling && currentBattleState != 2 && battleStateToString(currentBattleState).contains("Player") ) battlePrompt = "Player Rolling...";
			else if (gp.battleM.rolling && currentBattleState == 2) battlePrompt = currentMonster.symbol + " Rolling...";
			else if (gp.battleM.rolling && currentBattleState == 3) battlePrompt = "Both Rolling...";


			if (!battlePrompt.isEmpty()){
				g2.drawString(battlePrompt, gp.screenWidth/2 - getCenteredTextXOffset(battlePrompt, g2, font), gp.screenHeight - 30);
			}

		} else if (gp.player.gameState == gp.player.MOVE) {
			// g2.drawString("Explore the dungeon!", gp.screenWidth/2 - getCenteredTextXOffset("Explore the dungeon!", g2, font), gp.screenHeight - 30);
		}
	}
	// Вспомогательный метод для отладки состояний (можно удалить)
	private String battleStateToString(int state) {
		switch(state) {
			case 0: return "Initiative";
			case 1: return "Player Action Select";
			case 11: return "Player Dice Attack";
			case 13: return "Player Strong Attack";
			case 2: return "Monster Turn";
			case 3: return "Simultaneous";
			case 4: return "Player Heal Post-Battle";
			default: return "Unknown State";
		}
	}

	private int getCenteredTextXOffset(String text, Graphics2D g2, Font currentFont) {
		if (text == null || g2 == null || currentFont == null) return 0;
		Font oldFont = g2.getFont(); g2.setFont(currentFont);
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont); return length / 2;
	}
	private int getXoffsetForRightAlignedText(String text, Graphics2D g2, Font currentFont) {
		if (text == null || g2 == null || currentFont == null) return 0;
		Font oldFont = g2.getFont(); g2.setFont(currentFont);
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont); return length;
	}
	public void drawTransitionMessage(Graphics2D g2, String message) {
		if (message == null || g2 == null || largeFont == null || gp == null) return;
		g2.setFont(largeFont); g2.setColor(new Color(0,0,0,150));
		g2.fillRect(0,0, gp.screenWidth, gp.screenHeight);
		g2.setColor(Color.WHITE);
		int x = getXforCenteredText(message, g2, largeFont); int y = gp.screenHeight / 2;
		g2.drawString(message, x, y);
	}
	public int getXforCenteredText(String text, Graphics2D g2, Font customFont) {
		if (text == null || g2 == null || customFont == null || gp == null) return 0;
		Font oldFont = g2.getFont(); g2.setFont(customFont);
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		g2.setFont(oldFont); int x = (gp.screenWidth - length) / 2; return x;
	}
}