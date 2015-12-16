package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.List;

public interface MoveGenerator {
    default Strategy getStrategy() {
        return Strategy.RANDOM;
    }

    List<Waypoint> generate(final GameState state);
}
