package src.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import src.entity.Player;
import src.screens.EndScreen;
import src.screens.LevelScreen;
import src.screens.StartScreen;
import src.tile.TileManager;

public class GamePanel extends JPanel implements Runnable{

	final int originalTileSize = 16;
	final int scale = 3;

	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 19;
	public final int maxScreenRow = 16;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;

	public int panelState;
	public final int START = 0;
	public final int GAME = 1;
	public final int LEVEL = 2;
	public final int END = 3;

	final int FPS = 60;

	private boolean sound_off = false;

	public KeyHandler keyH = new KeyHandler(this);
	public UI ui = new UI(this);
	public StartScreen ss = new StartScreen(this);
	public EndScreen es = new EndScreen(this);
	public LevelScreen ls = new LevelScreen(this);
	public TileManager tileM;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public BattleManager battleM = new BattleManager(this);
	public MonstersManager monstersM;
	public MinesManager minesM;
	public Player player = new Player(this);
	public Sound sound = new Sound();
	Thread gameThread;

	public int currentLevel = 1;
	public final int MAX_LEVELS = 3;
	private String transitionMessage = null;
	private int transitionMessageTimer = 0;

	private List<String> mapFiles; // Сделали private
	private Random randomMapSelector;
	private String currentMapPath;


	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);

		mapFiles = new ArrayList<>();
		mapFiles.add("/res/maps/map.txt");
		mapFiles.add("/res/maps/map2.txt");
		mapFiles.add("/res/maps/map3.txt");
		randomMapSelector = new Random();

		if (!mapFiles.isEmpty()) {
			setupNewGameComponents(mapFiles.get(0));
		} else {
			System.err.println("CRITICAL ERROR: Map files list is empty at GamePanel construction!");

		}
	}

	private void setupNewGameComponents(String mapPath) {
		this.currentMapPath = mapPath;
		this.tileM = new TileManager(this, currentMapPath);
		this.monstersM = new MonstersManager(this);
		this.minesM = new MinesManager(this);
	}

	public List<String> getMapFiles() {
		return mapFiles;
	}


	public void startGameThread() {
		gameThread = new Thread(this);
		currentLevel = 1;
		if (!mapFiles.isEmpty()) {
			setupNewGameComponents(mapFiles.get(0));
		} else {
			System.err.println("CRITICAL ERROR: Map files list is empty! Cannot start game.");
			return;
		}
		player.setDefaultValues();
		player.hit_point = ls.level;
		panelState = START;
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1_000_000_000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;

		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime)/drawInterval;
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}

			if(timer >= 1_000_000_000) {
				timer = 0;
			}
		}
		System.exit(0);
	}

	public void update() {
		if (transitionMessageTimer > 0) {
			transitionMessageTimer--;
			if (transitionMessageTimer == 0) {
				transitionMessage = null;
				if (panelState != END && panelState != START) {
					panelState = GAME;
				}
			}
			return;
		}

		if(panelState != GAME) {
			return;
		}
		player.update();
		battleM.update();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;

		if(panelState == START) {
			ss.draw(g2);
		}
		else if(panelState == GAME) {
			tileM.draw(g2);
			monstersM.draw(g2);
			player.draw(g2);
			minesM.explode(g2);
			ui.draw(g2);
		}
		else if(panelState == LEVEL) {
			ls.draw(g2);
		}
		else if(panelState == END) {
			es.draw(g2);
		}

		if (transitionMessage != null) {
			ui.drawTransitionMessage(g2, transitionMessage);
		}
		g2.dispose();
	}

	public void playMusic(int i) {
		sound.setFile(i);
		sound.play();
		sound.loop();
	}

	public void stopMusic() {
		sound.stop();
	}

	public void playSE(int i) {
		if(sound_off) return;
		sound.setFile(i);
		sound.play();
	}

	public void toggle_sound() {
		sound_off = !sound_off;
	}

	public void prepareNextLevel() {
		currentLevel++;
		if (currentLevel > MAX_LEVELS) {
			panelState = END;
			es.setTitleTexts("CONGRATULATIONS!\nYou Beat All Levels!\nTotal Steps: " + player.steps);
		} else {
			showTransitionMessage("Level " + currentLevel, 120);

			String nextMapPath = currentMapPath;
			List<String> availableMaps = getMapFiles(); // Используем геттер

			if (availableMaps != null && !availableMaps.isEmpty()) {
				if (currentLevel -1 < availableMaps.size()) {
					nextMapPath = availableMaps.get(currentLevel - 1);
				} else {
					nextMapPath = availableMaps.get(availableMaps.size() - 1);
				}
			} else {
				System.err.println("Map files list is null or empty in prepareNextLevel!");
			}

			setupNewGameComponents(nextMapPath);

			player.setDefaultValues();
			player.hit_point = ls.level + ((currentLevel - 1) * 50);

			player.gameState = player.MOVE;
			player.movingState = 0;
			battleM.rolling = false;
			battleM.diceCounter = 0;
			battleM.battleState = 0;
			if (battleM.monsterIndex != -1 && monstersM != null && battleM.monsterIndex < monstersM.monsters.length) { // Добавил проверку monstersM != null
				if(monstersM.monsters[battleM.monsterIndex] != null) {
					monstersM.monsters[battleM.monsterIndex].visable = false;
				}
			}
			battleM.monsterIndex = -1;
		}
	}

	public void handleZorkDefeat(int defeatedZorkIndex) {
		if (monstersM != null && defeatedZorkIndex >= 0 && defeatedZorkIndex < monstersM.monsters.length && monstersM.monsters[defeatedZorkIndex] != null) {
			// --- ОПЫТ ЗА ЗОРКА ---
			player.gainExperience(monstersM.monsters[defeatedZorkIndex].experienceDropped);
			// --- КОНЕЦ ОПЫТА ЗА ЗОРКА ---
			monstersM.monster_remaning--; // Уменьшаем только если это не Зорк, или если Зорк входит в этот счетчик
			monstersM.monsters[defeatedZorkIndex] = null;
		}



		player.gameState = player.MOVE;
		player.movingState = 0;
		battleM.rolling = false;
		battleM.diceCounter = 0;
		battleM.battleState = 0;
		prepareNextLevel();
	}


	public void showTransitionMessage(String message, int durationFrames) {
		this.transitionMessage = message;
		this.transitionMessageTimer = durationFrames;
	}

	void restart() {
		currentLevel = 1;
		List<String> availableMaps = getMapFiles();
		if (availableMaps != null && !availableMaps.isEmpty()) {
			setupNewGameComponents(availableMaps.get(0));
		} else {
			System.err.println("CRITICAL ERROR: Map files list is null or empty! Cannot restart properly.");
			return;
		}


		player.setDefaultValues(); // ВАЖНО: Сбрасываем игрока к начальным параметрам 1-го уровня
		player.hit_point = player.max_hit_point;

		battleM = new BattleManager(this);
		cChecker = new CollisionChecker(this);

		player.gameState = player.MOVE;
		player.movingState = 0;
		showTransitionMessage("Level " + currentLevel, 120);
	}

	public String getCurrentMapPath() {
		return currentMapPath;
	}
}