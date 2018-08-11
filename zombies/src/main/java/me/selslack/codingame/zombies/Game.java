package me.selslack.codingame.zombies;

import java.util.List;
import java.util.ListIterator;

public class Game {
    static final public int KILL_DISTANCE_SQ = 2000 * 2000;
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
        final int humansAlive = state.getHumans().size();
        final ListIterator<Human> zombies = state.getZombies().listIterator();
        final ListIterator<Human> humans = state.getHumans().listIterator();

        // 0) Combo counter
        int kills = 0;

        // 1) Zombie's move towards their targets
        for (Human zombie : state.getZombies()) {
            humanMovement(zombie, calculateYummyBrains(zombie, state.getAsh(), state.getHumans()));
        }

        // 2) Ash moves to the given position
        humanMovement(state.getAsh(), x, y);

        // 3) Ash kills zombie in his range
        while (zombies.hasNext()) {
            final Human zombie = zombies.next();

            if (distanceSq(state.getAsh(), zombie) <= KILL_DISTANCE_SQ) {
                // Remove dead zombie from the state
                zombies.remove();

                state.score += humansAlive * humansAlive * 10 * KILL_MULTIPLIER[kills++];
            }
        }

        // 4) Zombie eat humans
        while (humans.hasNext()) {
            final Human human = humans.next();

            for (Human zombie : state.getZombies()) {
                if (human.x == zombie.x && human.y == zombie.y) {
                    humans.remove();
                }
            }
        }

        if (state.isLose()) {
            state.score = 0;
        }

        state.tick++;

        return state;
    }

    static protected int distanceSq(Human human, Human target) {
        return Utils.distanceSq(human.x, human.y, target.x, target.y);
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
        final int distanceToAsh = distanceSq(ash, zombie);

        Human target = ash;
        int closest = Integer.MAX_VALUE;

        // Zombies will attack the human with the smallest id if they are equidistant and they prefer Ash to scared humans.
        // Source: https://forum.codingame.com/t/code-vs-zombies-questions/1083/2
        for (Human human : humans) {
            final int distanceToZombie = distanceSq(human, zombie);

            if (distanceToZombie < distanceToAsh && distanceToZombie < closest) {
                target = human;
                closest = distanceToZombie;
            }
        }

        return target;
    }
}
