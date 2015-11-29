package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Utils;

import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;

public class ClusteringWaypointGenerator implements WaypointGenerator {
    final private WaypointGenerator generator;
    final private int distance;

    public ClusteringWaypointGenerator(WaypointGenerator generator, int distance) {
        this.generator = generator;
        this.distance = distance;
    }

    @Override
    public List<Waypoint> generate(GameState state) {
        List<Waypoint> points = generator.generate(state);
        List<Waypoint> result = new LinkedList<>(points);

        for (Waypoint point : points) {
            if (!result.contains(point)) {
                continue;
            }

            for (int i = 0; i < 5; i++) {
                point = massCentre(result, point, distance);
            }

            // Wtf, Java?
            final Waypoint finalPoint = point;

            result.removeIf(v -> Utils.distance(v.x, v.y, finalPoint.x, finalPoint.y) <= distance);
            result.add(point);
        }

        return result;
    }

    private Waypoint massCentre(final List<Waypoint> waypoints, Waypoint centre, final int distance) {
        IntSummaryStatistics x = new IntSummaryStatistics();
        IntSummaryStatistics y = new IntSummaryStatistics();

        waypoints.stream()
            .filter(v -> Utils.distance(v.x, v.y, centre.x, centre.y) <= distance)
            .forEach(v -> {
                x.accept(v.x);
                y.accept(v.y);
            });

        Waypoint result = new Waypoint(
            (int) Math.floor(x.getAverage()),
            (int) Math.floor(y.getAverage())
        );

        if (result.x == centre.x && result.y == centre.y) {
            return centre;
        }
        else {
            return result;
        }
    }
}
