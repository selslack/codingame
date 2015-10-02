package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

public interface Heuristic {
    double score(GameState state, byte playerId, Game.Direction direction);
}
