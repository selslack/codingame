package me.selslack.codingame.zombies;

import java.util.Comparator;

public class Game {
    static final public int KILL_DISTANCE = 2000;
    static final public int[] KILL_MOD = new int[] {1};

    static public void process(GameState state, int x, int y) {
        Human presentAsh = state.getAsh();
        Human futureAsh = presentAsh.clone();

        humanMovement(futureAsh, x, y);

        for (Human zombie : state.getZombies()) {
            int distanceToPresentAsh = Utils.distance(presentAsh, zombie);

            Human zombieTarget = state.getHumans().stream()
                .filter(v -> v.isAlive && Utils.distance(v, zombie) <= distanceToPresentAsh)
                .sorted(Comparator.comparingInt(v -> Utils.distance(v, zombie)))
                .findFirst()
                .orElse(presentAsh);

            humanMovement(zombie, zombieTarget);

            if (Utils.distance(futureAsh, zombie) <= KILL_DISTANCE) {
                zombie.isAlive = false;
            }

            if (zombieTarget != presentAsh && zombie.isAlive && Utils.distance(zombie, zombieTarget) < 1) {
                zombieTarget.isAlive = false;
            }
        }

        presentAsh.x = futureAsh.x;
        presentAsh.y = futureAsh.y;
    }

    static private void humanMovement(Human object, Human target) {
        humanMovement(object, target.x, target.y);
    }

    static private void humanMovement(Human human, int x, int y) {
        if (human.type.getSpeed() <= 0) {
            throw new IllegalArgumentException("This human is unmovable: " + human);
        }

        if (Utils.distance(human.x, human.y, x, y) <= human.type.getSpeed()) {
            human.x = x;
            human.y = y;
        }
        else {
            double angle = Math.atan2(y - human.y, x - human.x);

            human.x += Math.floor(Math.nextUp(human.type.getSpeed() * Math.cos(angle)));
            human.y += Math.floor(Math.nextUp(human.type.getSpeed() * Math.sin(angle)));
        }
    }
}
