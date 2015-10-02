package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameField;
import me.selslack.codingame.backtothecode.GameState;

public class AreaControlHeuristic implements Heuristic {
    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        Game.move(state, playerId, direction);

        double result = 0.0;

        for (int x = 0; x < GameState.X; x++) {
            for (int y = 0; y < GameState.Y; y++) {
                result += Game.influence(state, playerId, x, y);
            }
        }

        return result;
    }
}
