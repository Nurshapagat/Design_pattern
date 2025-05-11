package src.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.Color;
import java.util.List; // Импорт для List

import javax.imageio.ImageIO;

import src.main.GamePanel;

public class TileManager {

	public static final char map_tile  = 'm';
	public static final char boss_tile = 'B';
	public static final char village_a = 'a';
	public static final char village_b = 'b';
	public static final char village_c = 'c';

	GamePanel gp;
	public Tile[] tiles;
	public int mapTileNum[][];

	public TileManager(GamePanel gp, String mapFilePath) {
		this.gp = gp;
		tiles = new Tile[10];

		mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];

		getTileImage();
		loadMap(mapFilePath);
	}

	public void getTileImage() {
		try {
			tiles[0] = new Tile();
			tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/000.png"));
			tiles[0].collision = true;
			tiles[0].symbol = '0';

			tiles[1] = new Tile();
			tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile.jpg"));
			tiles[1].collision = true;
			tiles[1].symbol = '1';

			tiles[2] = new Tile();
			tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile2.jpg"));
			tiles[2].collision = false;
			tiles[2].symbol = map_tile;

			tiles[3] = new Tile();
			tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile3.jpg"));
			tiles[3].collision = true;
			tiles[3].symbol = 'M';

			tiles[4] = new Tile();
			tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile5.jpg"));
			tiles[4].collision = false;
			tiles[4].symbol = village_a;

			tiles[5] = new Tile();
			tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile6.jpg"));
			tiles[5].collision = false;
			tiles[5].symbol = village_b;

			tiles[6] = new Tile();
			tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile7.jpg"));
			tiles[6].collision = false;
			tiles[6].symbol = village_c;

			tiles[7] = new Tile();
			tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/tile4.jpg"));
			tiles[7].collision = false;
			tiles[7].symbol = boss_tile;

		} catch (IOException e) {
			System.err.println("Error loading tile images: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Error: One or more tile image resources not found. Check paths in getTileImage(). " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void loadMap(String path) {
		InputStream	is = getClass().getResourceAsStream(path);
		if (is == null) {
			System.err.println("Cannot find map file: " + path + ". Attempting to load default map.");
			List<String> gamePanelMapFiles = gp.getMapFiles(); // Используем геттер
			if (gamePanelMapFiles != null && !gamePanelMapFiles.isEmpty()) {
				is = getClass().getResourceAsStream(gamePanelMapFiles.get(0));
				if (is == null) {
					System.err.println("CRITICAL ERROR: Default map also not found: " + gamePanelMapFiles.get(0));

					for (int r = 0; r < gp.maxScreenRow; r++) {
						for (int c = 0; c < gp.maxScreenCol; c++) {
							mapTileNum[c][r] = 0; // Например, тайл 0 (стена)
						}
					}
					return;
				}
				System.out.println("Loading default map instead: " + gamePanelMapFiles.get(0));
			} else {
				System.err.println("CRITICAL ERROR: No map files configured in GamePanel, cannot load any map.");
				for (int r = 0; r < gp.maxScreenRow; r++) {
					for (int c = 0; c < gp.maxScreenCol; c++) {
						mapTileNum[c][r] = 0;
					}
				}
				return;
			}
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		int col = 0;
		int row = 0;

		try {
			while(row < gp.maxScreenRow) {
				String line = br.readLine();
				if (line == null) {
					for (int r = row; r < gp.maxScreenRow; r++) {
						for (int c = 0; c < gp.maxScreenCol; c++) {
							mapTileNum[c][r] = 0;
						}
					}
					break;
				}

				String[] numbers = line.trim().split("\\s+");
				col = 0;
				for(; col < gp.maxScreenCol; col++) {
					if (col < numbers.length && !numbers[col].isEmpty()) {
						try {
							int num = Integer.parseInt(numbers[col]);
							if (num >= 0 && num < tiles.length) {
								mapTileNum[col][row] = num;
							} else {
								System.err.println("Invalid tile number " + num + " in map " + path + " at " + (row+1) + "," + (col+1) + ". Using default tile 0.");
								mapTileNum[col][row] = 0;
							}
						} catch (NumberFormatException e) {
							System.err.println("Error parsing number in map file " + path + " at " + (row+1) + "," + (col+1) + ": '" + numbers[col] + "'. Using default tile 0.");
							mapTileNum[col][row] = 0;
						}
					} else {
						mapTileNum[col][row] = 0;
					}
				}
				row++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();

			for (int r = row; r < gp.maxScreenRow; r++) {
				for (int c = (r == row ? col : 0) ; c < gp.maxScreenCol; c++) { // если ошибка на середине строки, продолжаем с этой строки
					mapTileNum[c][r] = 0;
				}
			}
		}
	}

	public void draw(Graphics2D g2) {
		int col = 0;
		int row = 0;
		int x = 0;
		int y = 0;

		while(row < gp.maxScreenRow) {
			col = 0;
			x = 0;
			while (col < gp.maxScreenCol) {
				int tileNum = mapTileNum[col][row];
				if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null && tiles[tileNum].image != null) {
					g2.drawImage(tiles[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
				} else {
					g2.setColor(Color.BLACK); // Или другой цвет для "пустого" или ошибочного тайла
					g2.fillRect(x, y, gp.tileSize, gp.tileSize);

				}
				col++;
				x += gp.tileSize;
			}
			row++;
			y += gp.tileSize;
		}
	}
}