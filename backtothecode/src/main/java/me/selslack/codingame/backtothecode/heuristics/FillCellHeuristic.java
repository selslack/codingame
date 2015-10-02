package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

public class FillCellHeuristic implements Heuristic {
    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        Game.move(state, playerId, direction);

        return state.field.count(v -> v == playerId);
    }
}
