package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

public class AvoidInvalidMoveHeuristic implements Heuristic {
    @Override
    public double score(GameState state, int playerId, int[] point) {
        Game.move(state, playerId, point);

        return state.getPlayer(playerId).isActive() ? 0 : Long.MIN_VALUE;
    }
}
