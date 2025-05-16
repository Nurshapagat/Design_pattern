package src.main;

public class GameManager {
    private static GameManager instance;
    private GamePanel gamePanel;

    private GameManager() {
        gamePanel = new GamePanel();
    }

    public static GameManager getInstance() {
        if(instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

}

