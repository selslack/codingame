package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Human;

import java.util.LinkedList;
import java.util.List;

public class ZombiesWaypointGenerator implements WaypointGenerator {
    @Override
    public List<Waypoint> generate(final GameState state) {
        LinkedList<Waypoint> result = new LinkedList<>();

        for (Human zombie : state.getZombies()) {
            if (zombie.isAlive) {
                result.add(new Waypoint(zombie));
            }
        }

        return result;
    }
}
