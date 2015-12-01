package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.Waypoint;

import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    static public int distance(int x1, int y1, int x2, int y2) {
        return (int) Math.ceil(
            Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0))
        );
    }

    static public int distance(Human h1, Human h2) {
        return distance(h1.x, h1.y, h2.x, h2.y);
    }

    static public int distance(Waypoint w1, Waypoint w2) {
        return distance(w1.x, w1.y, w2.x, w2.y);
    }
}
