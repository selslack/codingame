package me.selslack.codingame.zombies;

import java.util.Comparator;
import java.util.List;

public class Utils {
    static public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.floor(
            Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0))
        );
    }

    static public int distance(Human h1, Human h2) {
        return distance(h1.x, h1.y, h2.x, h2.y);
    }

    static public int plyDistance(Human object, Human target, double speed) {
        return (int) Math.ceil(distance(object, target) / speed);
    }

    static public boolean isHumanWorthSaving(Human ash, Human human, List<Human> zombies) {
        if (human == ash) {
            return false;
        }

        for (Human zombie : zombies) {
            if (plyDistance(zombie, human, 400) < plyDistance(ash, human, 1000)) {
                return false;
            }
        }

        return true;
    }

    static public int threat(Human ash, Human zombie, List<Human> zombies, List<Human> humans) {
        Human zombieTarget = humans.stream()
            .filter(v -> v.isAlive && distance(zombie, v) < distance(zombie, ash))
            .sorted(Comparator.comparingDouble(v -> Utils.distance(v, zombie)))
            .findFirst()
            .orElse(ash);

        if (zombieTarget == ash) {
            return Integer.MAX_VALUE / 6 / distance(ash, zombie);
        }
        else if (!isHumanWorthSaving(ash, zombieTarget, zombies)) {
            return Integer.MAX_VALUE / 3 / distance(ash, zombie);
        }
        else {
            return Integer.MAX_VALUE / distance(ash, zombie);
        }
    }
}
