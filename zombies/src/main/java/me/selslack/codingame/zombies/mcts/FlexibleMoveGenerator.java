package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FlexibleMoveGenerator implements MoveGenerator {
    final private List<Waypoint> modifiers = new LinkedList<>();

    public FlexibleMoveGenerator(final int range, final int count) {
        double angle = 2 * Math.PI / count;

        for (int i = 0; i < count; i++) {
            int x = (int) Math.floor(Math.nextUp(range * Math.cos(i * angle)));
            int y = (int) Math.floor(Math.nextUp(range * Math.sin(i * angle)));

            modifiers.add(new Waypoint(x, y));
        }
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.RANDOM;
    }

    @Override
    public List<Waypoint> generate(GameState state) {
        Waypoint base = new Waypoint(state.getAsh());

        return modifiers.stream()
            .map(v -> new Waypoint(base.x + v.x, base.y + v.y))
            .filter(v -> v.x >= 0 && v.x < 16000 && v.y >= 0 && v.y < 9000)
            .collect(Collectors.toList());
    }
}
