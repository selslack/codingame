package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.Waypoint;

import java.util.IntSummaryStatistics;
import java.util.LinkedList;
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

    static public int distance(Waypoint w1, Waypoint w2) {
        return distance(w1.x, w1.y, w2.x, w2.y);
    }

    static public List<Waypoint> clusterize(List<Waypoint> waypoints, int distance) {
        List<Waypoint> result = new LinkedList<>();
        List<Waypoint> heap = new LinkedList<>(waypoints);

        for (Waypoint point : waypoints) {
            if (!heap.contains(point)) {
                continue;
            }

            if (heap.stream().filter(v -> Utils.distance(v, point) <= distance).count() <= 3) {
                Waypoint copy = new Waypoint(point.x, point.y);
                copy.marker = 1;

                result.add(copy);
                continue;
            }

            Waypoint center = point;
            int lastPointsAround = 0;

            while (true) {
                IntSummaryStatistics x = new IntSummaryStatistics();
                IntSummaryStatistics y = new IntSummaryStatistics();

                final Waypoint finalCenter = center;

                heap.stream().filter(v -> Utils.distance(v, finalCenter) <= distance).forEach(v -> {
                    x.accept(v.x);
                    y.accept(v.y);
                });

                center = new Waypoint(
                    (int) Math.floor(x.getAverage()),
                    (int) Math.floor(y.getAverage())
                );

                final Waypoint finalCenter1 = center;

                int pointsAround = (int) heap.stream()
                    .filter(v -> Utils.distance(v, finalCenter1) <= distance)
                    .count();

                center.marker = Game.KILL_MOD[pointsAround];

                if (pointsAround == lastPointsAround) {
                    break;
                }

                lastPointsAround = pointsAround;
            }

            final Waypoint finalCenter2 = center;

            heap.removeIf(v -> Utils.distance(v, finalCenter2) <= distance);
            result.add(center);
        }

        return result;
    }
}
