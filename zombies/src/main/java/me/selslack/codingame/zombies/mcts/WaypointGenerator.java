package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;

import java.util.List;

public interface WaypointGenerator {
    List<Waypoint> generate(final GameState state);
}
