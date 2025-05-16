package src.main;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import src.entity.Player;
import src.screens.EndScreen;
import src.screens.LevelScreen;
import src.screens.StartScreen;
import src.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

	final int originalTileSize = 20;
	final int scale = 3;

	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 24;
	public final int maxScreenRow = 15;
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

	private List<String> mapFiles;
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
		player.hit_point = player.max_hit_point;
		panelState = START;

		sound.loadBackgroundMusic();
		sound.playBackgroundMusic();

		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1_000_000_000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;

		while(gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
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

	// Методы звука
	public void playSE(int i) {
		if(sound_off) return;
		sound.setFile(i);
		sound.play();
	}

	public void toggle_sound() {
		sound_off = !sound_off;
		if(sound_off) {
			sound.stopBackgroundMusic();
		} else {
			sound.playBackgroundMusic();
		}
	}

	public void stopBackgroundMusic() {
		sound.stopBackgroundMusic();
	}

	public void restart() {
	}

	public void handleZorkDefeat(int monsterIndex) {
		System.out.println("Boss defeated!");

		if (currentLevel < MAX_LEVELS) {
			currentLevel++;
			System.out.println("Loading level " + currentLevel);

			setupNewGameComponents(mapFiles.get(currentLevel - 1));
			player.setDefaultValues();
			player.hit_point = player.max_hit_point;

			if(monstersM != null) {
				monstersM.setMonsters();
			}

			panelState = GAME;

			transitionMessage = "Level " + currentLevel;
			transitionMessageTimer = 120;
		} else {
			panelState = END;
			es.setTitleTexts("Поздравляем! Вы выиграли все уровни!");
		}
	}
}
