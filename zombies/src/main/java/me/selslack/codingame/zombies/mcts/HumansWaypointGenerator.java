package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;
import me.selslack.codingame.zombies.Waypoint;

import java.util.LinkedList;
import java.util.List;

public class HumansWaypointGenerator implements WaypointGenerator {
    @Override
    public List<Waypoint> generate(final GameState state) {
        LinkedList<Waypoint> result = new LinkedList<>();

        for (Human human : state.getHumans()) {
            if (human.isAlive) {
                result.add(new Waypoint(human));
            }
        }

        return result;
    }
}
