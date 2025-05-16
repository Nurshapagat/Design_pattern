package src.factory;

import src.entity.*;
import src.main.GamePanel;

public class EntityFactory {

    public static Player createPlayer(GamePanel gp) {
        return new Player(gp);
    }

    public static King createKing(GamePanel gp, int col, int row) {
        return new King(gp, col, row);
    }

    public static Dragon createDragon(GamePanel gp, int col, int row) {
        return new Dragon(gp, col, row);
    }

    public static skeleton createSkeleton(GamePanel gp, int col, int row) {
        return new skeleton(gp, col, row);
    }

    public static Goblin createGoblin(GamePanel gp, int col, int row) {
        return new Goblin(gp, col, row);
    }

}
