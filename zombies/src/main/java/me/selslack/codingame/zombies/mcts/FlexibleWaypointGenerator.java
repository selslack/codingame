package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.LinkedList;
import java.util.List;

public class FlexibleWaypointGenerator implements WaypointGenerator {
    final private int range;
    final private int count;
    final private double angle;
    final private Waypoint[] modifiers;

    public FlexibleWaypointGenerator(final int range, final int count) {
        this.range = range;
        this.count = count;
        this.angle = 2 * Math.PI / count;
        this.modifiers = new Waypoint[count];

        for (int i = 0; i < count; i++) {
            int x = (int) Math.floor(Math.nextUp(range * Math.cos(i * angle)));
            int y = (int) Math.floor(Math.nextUp(range * Math.sin(i * angle)));

            modifiers[i] = new Waypoint(x, y);
        }
    }

    @Override
    public List<Waypoint> generate(GameState state) {
        List<Waypoint> result = new LinkedList<>();
        Waypoint base = new Waypoint(state.getAsh());

        for (Waypoint modifier : modifiers) {
            int x = base.x + modifier.x;
            int y = base.y + modifier.y;

            if (x >= 0 && x < 16000 && y >= 0 && y < 9000) {
                result.add(new Waypoint(x, y));
            }
        }

        return result;
    }
}
