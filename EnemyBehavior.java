package src.behavior;

import src.entity.Goblin;

public interface EnemyBehavior {
    void move();
    void attack();
}


class AggressiveBehavior implements EnemyBehavior {
    private Goblin goblin;

    public AggressiveBehavior(Goblin goblin) {
        this.goblin = goblin;
    }

    @Override
    public void move() {
        goblin.moveTowardPlayer();
    }

    @Override
    public void attack() {
        goblin.attackPlayer();
    }

    private EnemyBehavior behavior;

    public void setBehavior(EnemyBehavior behavior) {
        this.behavior = behavior;
    }

    public void update() {
        if (behavior != null) {
            behavior.move();
            behavior.attack();
        }
    }
}

