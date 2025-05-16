package src.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

	public boolean upPressed, downPressed, leftPressed, rightPressed, rPressed;
	public boolean spell1Pressed;
	public boolean spell2Pressed;
	GamePanel gp;

	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();

		if(code == KeyEvent.VK_ESCAPE) {
			if (gp != null) gp.gameThread = null;
		}

		if(code == KeyEvent.VK_M) {
			if (gp != null) gp.toggle_sound();
		}

		if(gp.panelState == gp.START) {
			if(code == KeyEvent.VK_W) {
				if(gp.ss.commandNum == 0) gp.ss.commandNum = 2; else gp.ss.commandNum--;
			}
			else if(code == KeyEvent.VK_S) {
				if(gp.ss.commandNum == 2) gp.ss.commandNum = 0; else gp.ss.commandNum++;
			}
			if(code == KeyEvent.VK_ENTER) {
				switch(gp.ss.commandNum) {
					case 0: gp.panelState = gp.GAME; break;
					case 1: gp.panelState = gp.LEVEL; break;
					case 2: if (gp != null) gp.gameThread = null; break;
				}
			}
		}
		else if(gp.panelState == gp.GAME) {
			if(code == KeyEvent.VK_W) upPressed = true;
			else if(code == KeyEvent.VK_S) downPressed = true;
			else if(code == KeyEvent.VK_D) rightPressed = true;
			else if(code == KeyEvent.VK_A) leftPressed = true;
			else if(code == KeyEvent.VK_R) {
				if (!rPressed && gp.player.gameState == gp.player.BATTLE) rPressed = true;
			}
			else if (code == KeyEvent.VK_1) {
				if (!spell1Pressed && gp.player.gameState == gp.player.BATTLE) spell1Pressed = true;
			}
			else if (code == KeyEvent.VK_2) {
				if (!spell2Pressed && gp.player.gameState == gp.player.BATTLE) spell2Pressed = true;
			}
		}
		else if(gp.panelState == gp.LEVEL) {
			if(code == KeyEvent.VK_A && gp.ls.commandNum > 0) { // commandNum 0,1,2
				gp.ls.commandNum--; gp.ls.level = 400 - gp.ls.commandNum * 100; // Easy 400, Med 300, Hard 200
			} else if (code == KeyEvent.VK_A && gp.ls.commandNum == 0){
				gp.ls.commandNum = 2; gp.ls.level = 400 - gp.ls.commandNum * 100;
			}
			if(code == KeyEvent.VK_D && gp.ls.commandNum < 2) {
				gp.ls.commandNum++; gp.ls.level = 400 - gp.ls.commandNum * 100;
			} else if (code == KeyEvent.VK_D && gp.ls.commandNum == 2){
				gp.ls.commandNum = 0; gp.ls.level = 400 - gp.ls.commandNum * 100;
			}
			if(code == KeyEvent.VK_ENTER) {
				// Начальное HP игрока устанавливается в Player.setDefaultValues() на основе vitality.
				// Здесь можно изменить базовую vitality, если LevelScreen должен на это влиять.
				// Например:
				// if (gp.ls.commandNum == 0) gp.player.vitality = 95; // Easy
				// else if (gp.ls.commandNum == 1) gp.player.vitality = 75; // Medium
				// else if (gp.ls.commandNum == 2) gp.player.vitality = 55; // Hard
				// gp.player.setDefaultValues(); // Пересчитать HP на основе новой vitality
				gp.panelState = gp.START;
			}
		}
		else if(gp.panelState == gp.END) {
			if(code == KeyEvent.VK_W) {
				if(gp.es.commandNum == 0) gp.es.commandNum = 1; else gp.es.commandNum--;
			}
			else if(code == KeyEvent.VK_S) {
				if(gp.es.commandNum == 1) gp.es.commandNum = 0; else gp.es.commandNum++;
			}
			if(code == KeyEvent.VK_ENTER) {
				switch(gp.es.commandNum) {
					case 0: gp.panelState = gp.START; if (gp != null) gp.restart(); break;
					case 1: if (gp != null) gp.gameThread = null; break;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) upPressed = false;
		else if(code == KeyEvent.VK_S) downPressed = false;
		else if(code == KeyEvent.VK_D) rightPressed = false;
		else if(code == KeyEvent.VK_A) leftPressed = false;
		else if(code == KeyEvent.VK_R) rPressed = false;
		else if (code == KeyEvent.VK_1) spell1Pressed = false;
		else if (code == KeyEvent.VK_2) spell2Pressed = false;
	}
}