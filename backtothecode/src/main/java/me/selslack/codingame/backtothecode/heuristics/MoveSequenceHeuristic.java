package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

import java.util.stream.Stream;

public class MoveSequenceHeuristic implements Heuristic {
    private final Stream<int[][][]> stream;

    public MoveSequenceHeuristic(Stream<int[][][]> stream) {
        this.stream = stream;
    }

    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        return 0;
    }
}
