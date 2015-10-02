package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

abstract public class FutureStateHeuristic implements Heuristic {
    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        return 0;
    }
}
