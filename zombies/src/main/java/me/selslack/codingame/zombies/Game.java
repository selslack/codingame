package me.selslack.codingame.zombies;

import java.util.Comparator;

public class Game {
    static final public int KILL_DISTANCE = 2000;
    static final public int[] KILL_MOD = new int[] {1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946};

    static public void process(GameState state, int x, int y, boolean debug) {
        int humansAlive = (int) state.getHumans().stream().filter(v -> v.isAlive).count();
        int kills = 0;

        for (Human zombie : state.getZombies()) {
            int distanceToPresentAsh = Utils.distance(state.getAsh(), zombie);

            Human zombieTarget = state.getHumans().stream()
                .filter(v -> v.isAlive && Utils.distance(v, zombie) <= distanceToPresentAsh)
                .sorted(Comparator.comparingInt(v -> Utils.distance(v, zombie)))
                .findFirst()
                .orElse(state.getAsh());

            humanMovement(zombie, zombieTarget);
        }

        humanMovement(state.getAsh(), x, y);

        for (Human zombie : state.getZombies()) {
            if (zombie.isAlive && Utils.distance(state.getAsh(), zombie) <= KILL_DISTANCE) {
                zombie.isAlive = false;
                state.score += humansAlive * humansAlive * 10 * KILL_MOD[kills++];
            }

            for (Human human : state.getHumans()) {
                if (zombie.isAlive && human.x == zombie.x && human.y == zombie.y) {
                    human.isAlive = false;
                }
            }
        }
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
