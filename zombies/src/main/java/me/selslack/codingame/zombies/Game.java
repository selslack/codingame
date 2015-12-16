package me.selslack.codingame.zombies;

import java.util.Comparator;
import java.util.List;

public class Game {
    static final public int KILL_DISTANCE = 2000;
    static final public int[] KILL_MULTIPLIER = new int[] {
        1, 2, 3, 5, 8,
        13, 21, 34, 55, 89,
        144, 233, 377, 610, 987,
        1597, 2584, 4181, 6765, 10946,
        17711, 28657, 46368, 75025, 121393,
        196418, 317811
    };

    static public GameState process(GameState state, Waypoint waypoint) {
        return process(state, waypoint.x, waypoint.y);
    }

    static public GameState process(GameState state, int x, int y) {
        int humansAlive = state.getHumans().size();
        int kills = 0;

        // 1) Zombie's move towards their targets
        state.getZombies()
            .forEach(v -> humanMovement(v, calculateYummyBrains(v, state.getAsh(), state.getHumans())));

        // 2) Ash moves to the given position
        humanMovement(state.getAsh(), x, y);


        for (Human zombie : state.getZombies()) {
            // 3) Ash kills zombie in his range
            if (zombie.isAlive && distance(state.getAsh(), zombie) <= KILL_DISTANCE) {
                zombie.isAlive = false;
                state.score += humansAlive * humansAlive * 10 * KILL_MULTIPLIER[kills++];
            }

            // 4) Zombie eat humans
            if (zombie.isAlive) {
                // noinspection Convert2streamapi
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

        return state;
    }

    static public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.ceil(Utils.distance(x1, y1, x2, y2));
    }

    static public int distance(Human human, Human target) {
        return distance(human.x, human.y, target.x, target.y);
    }


    static protected void humanMovement(Human object, Waypoint target) {
        humanMovement(object, target.x, target.y);
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
            double[] segment = Utils.projectionFast(human.x, human.y, x, y, human.type.getSpeed());

            double roundedX = Math.round(segment[0]);
            double roundedY = Math.round(segment[1]);

            if (Math.abs(segment[0] - roundedX) < Utils.EPSILON) {
                segment[0] = roundedX;
            }

            if (Math.abs(segment[1] - roundedY) < Utils.EPSILON) {
                segment[1] = roundedY;
            }

            human.x += Math.floor(segment[0]);
            human.y += Math.floor(segment[1]);
        }
    }

    static public Human calculateYummyBrains(final Human zombie, final Human ash, final List<Human> humans) {
        int distanceToAsh = distance(ash, zombie);

        // Zombies will attack the human with the smallest id if they are equidistant and they prefer Ash to scared humans.
        // Source: https://forum.codingame.com/t/code-vs-zombies-questions/1083/2
        return humans
            .stream()
            .filter(v -> v.isAlive && distance(v, zombie) < distanceToAsh)
            .sorted(Comparator.comparingInt((Human v) -> distance(v, zombie)).thenComparingInt(v -> v.id))
            .findFirst()
            .orElse(ash);
    }
}
