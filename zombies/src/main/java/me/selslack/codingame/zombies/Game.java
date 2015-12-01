package me.selslack.codingame.zombies;

import java.util.Comparator;
import java.util.List;

public class Game {
    static final public int KILL_DISTANCE = 2000;
    static final public int[] KILL_MOD = new int[] {1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811};

    static public void process(GameState state) {
        process(state, state.getAsh().x, state.getAsh().y);
    }

    static public void process(GameState state, int x, int y) {
        int humansAlive = state.getHumans().size();
        int kills = 0;

        for (Human zombie : state.getZombies()) {
            humanMovement(zombie, calculateYummyBrains(zombie, state.getAsh(), state.getHumans()));
        }

        humanMovement(state.getAsh(), x, y);

        for (Human zombie : state.getZombies()) {
            if (zombie.isAlive && Utils.distance(state.getAsh(), zombie) <= KILL_DISTANCE) {
                zombie.isAlive = false;
                state.score += humansAlive * humansAlive * 10 * KILL_MOD[kills++];
            }

            if (zombie.isAlive) {
                for (Human human : state.getHumans()) {
                    if (human.x == zombie.x && human.y == zombie.y) {
                        human.isAlive = false;
                    }
                }
            }
        }

        state.getHumans().removeIf(v -> !v.isAlive);
        state.getZombies().removeIf(v -> !v.isAlive);

        if (state.isLose()) {
            state.score = 0;
        }

        state.tick++;
    }

    static protected void humanMovement(Human object, Human target) {
        humanMovement(object, target.x, target.y);
    }

    static protected void humanMovement(Human human, int x, int y) {
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

    static public Human calculateYummyBrains(final Human zombie, final Human ash, final List<Human> humans) {
        int distanceToAsh = Utils.distance(ash, zombie);

        // Zombies will attack the human with the smallest id if they are equidistant and they prefer Ash to scared humans.
        // Source: https://forum.codingame.com/t/code-vs-zombies-questions/1083/2
        return humans
            .stream()
            .filter(v -> v.isAlive && Utils.distance(v, zombie) < distanceToAsh)
            .sorted(Comparator.comparingInt((Human v) -> Utils.distance(v, zombie)).thenComparingInt(v -> v.id))
            .findFirst()
            .orElse(ash);
    }
}
