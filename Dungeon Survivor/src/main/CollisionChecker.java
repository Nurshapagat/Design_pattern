package src.main;

import src.entity.Entity;
import src.entity.Mine; // <--- ДОБАВЬТЕ ЭТОТ ИМПОРТ
import src.tile.TileManager;

public class CollisionChecker {

	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}

	public void checkTile() {
		if (gp == null || gp.player == null || gp.tileM == null || gp.tileM.mapTileNum == null || gp.tileM.tiles == null) {
			return;
		}

		gp.player.collisionOn = false;

		int playerCol = gp.player.getX() / gp.tileSize; // Используем геттер
		int playerRow = gp.player.getY() / gp.tileSize; // Используем геттер

		int tileNum;
		int targetCol, targetRow;

		switch(gp.player.direction) {
			case Entity.UP:
				targetRow = playerRow - 1;
				if (targetRow < 0) { gp.player.collisionOn = true; return; }
				tileNum = gp.tileM.mapTileNum[playerCol][targetRow];
				if(tileNum >= 0 && tileNum < gp.tileM.tiles.length && gp.tileM.tiles[tileNum] != null && gp.tileM.tiles[tileNum].collision)
					gp.player.collisionOn = true;
				break;
			case Entity.DOWN:
				targetRow = playerRow + 1;
				if (targetRow >= gp.maxScreenRow) { gp.player.collisionOn = true; return; }
				tileNum = gp.tileM.mapTileNum[playerCol][targetRow];
				if(tileNum >= 0 && tileNum < gp.tileM.tiles.length && gp.tileM.tiles[tileNum] != null && gp.tileM.tiles[tileNum].collision)
					gp.player.collisionOn = true;
				break;
			case Entity.RIGHT:
				targetCol = playerCol + 1;
				if (targetCol >= gp.maxScreenCol) { gp.player.collisionOn = true; return; }
				tileNum = gp.tileM.mapTileNum[targetCol][playerRow];
				if(tileNum >= 0 && tileNum < gp.tileM.tiles.length && gp.tileM.tiles[tileNum] != null && gp.tileM.tiles[tileNum].collision)
					gp.player.collisionOn = true;
				break;
			case Entity.LEFT:
				targetCol = playerCol - 1;
				if (targetCol < 0) { gp.player.collisionOn = true; return; }
				tileNum = gp.tileM.mapTileNum[targetCol][playerRow];
				if(tileNum >= 0 && tileNum < gp.tileM.tiles.length && gp.tileM.tiles[tileNum] != null && gp.tileM.tiles[tileNum].collision)
					gp.player.collisionOn = true;
				break;
		}
	}

	public void checkVillages() {
		if (gp == null || gp.player == null || gp.tileM == null || gp.tileM.mapTileNum == null || gp.tileM.tiles == null) return;

		int playerCol = gp.player.getX() / gp.tileSize; // Используем геттер
		int playerRow = gp.player.getY() / gp.tileSize; // Используем геттер

		if (playerCol < 0 || playerCol >= gp.maxScreenCol || playerRow < 0 || playerRow >= gp.maxScreenRow) return;

		int tileNum = gp.tileM.mapTileNum[playerCol][playerRow];

		if(tileNum >= 0 && tileNum < gp.tileM.tiles.length && gp.tileM.tiles[tileNum] != null){
			switch(gp.tileM.tiles[tileNum].symbol) {
				case TileManager.village_a:
					if (gp.player.hit_point < gp.player.max_hit_point) { // Лечим, только если не полное ХП
						gp.player.hit_point += 200;
						if(gp.player.hit_point > gp.player.max_hit_point) gp.player.hit_point = gp.player.max_hit_point;
						gp.tileM.tiles[tileNum].symbol = ' ';
						System.out.println("Player visited Village A, healed up to 200 HP.");
						// if(gp != null) gp.playSE(INDEX_SOUND_VILLAGE_HEAL);
					}
					break;
				case TileManager.village_b:
					if (gp.player.hit_point < gp.player.max_hit_point) {
						gp.player.hit_point += 150;
						if(gp.player.hit_point > gp.player.max_hit_point) gp.player.hit_point = gp.player.max_hit_point;
						gp.tileM.tiles[tileNum].symbol = ' ';
						System.out.println("Player visited Village B, healed up to 150 HP.");
					}
					break;
				case TileManager.village_c:
					if (gp.player.hit_point < gp.player.max_hit_point) {
						gp.player.hit_point += 100;
						if(gp.player.hit_point > gp.player.max_hit_point) gp.player.hit_point = gp.player.max_hit_point;
						gp.tileM.tiles[tileNum].symbol = ' ';
						System.out.println("Player visited Village C, healed up to 100 HP.");
					}
					break;
			}
		}
	}

	public void checkMines() {
		if (gp == null || gp.player == null || gp.minesM == null || gp.minesM.mines == null) return;

		for(int i = 0; i < gp.minesM.mines.length; i++) {
			Mine currentMine = gp.minesM.mines[i]; // Для читаемости
			if(currentMine != null &&
					gp.player.getX() == currentMine.getX() && // Используем геттеры для игрока и мины
					gp.player.getY() == currentMine.getY()) {

				gp.minesM.setExplosion(currentMine); // Передаем объект мины
				gp.player.hit_point -= currentMine.getDamage(); // Получаем урон из мины
				System.out.println("Player hit a mine! -" + currentMine.getDamage() + " HP.");
				gp.minesM.mines[i] = null; // Удаляем мину после взрыва

				if (gp.player.hit_point <= 0) {
					gp.player.hit_point = 0;
					// Решение о конце игры (gp.panelState = gp.END) лучше принимать в GamePanel.update()
					// или в MinesManager.explode(), чтобы не было смешения логики.
					// Здесь мы просто обновляем HP игрока.
				}
			}
		}
	}
}