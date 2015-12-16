package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ZombiesMoveGenerator implements MoveGenerator {
    @Override
    public Strategy getStrategy() {
        return Strategy.EXTINGUISH;
    }

    @Override
    public List<Waypoint> generate(final GameState state) {
        return state.getZombies().stream()
            .map(Waypoint::new)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
