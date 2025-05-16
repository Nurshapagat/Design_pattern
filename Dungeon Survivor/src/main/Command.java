package src.main;
import src.entity.Player;

public interface Command {
    void execute();


    public class MoveUpCommand implements Command {
        private Player player;

        public MoveUpCommand(Player player) {
            this.player = player;
        }

        @Override
        public void execute() {
            player.moveUp();
        }
    }
}


