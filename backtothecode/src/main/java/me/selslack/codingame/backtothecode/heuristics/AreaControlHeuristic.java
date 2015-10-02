package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameField;
import me.selslack.codingame.backtothecode.GameState;

public class AreaControlHeuristic implements Heuristic {
    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        Game.move(state, playerId, direction);

        GameField mask = state.field.clone();

        for (int x = 0; x < GameState.X; x++) {
            for (int y = 0; y < GameState.Y; y++) {
                int minDistance = Integer.MAX_VALUE;

                if (state.field.get(x, y) >= 0) {
                    continue;
                }

                for (GameState.Player player : state.getPlayers()) {
                    int distance = Math.abs(player.getPosition()[0] - x) + Math.abs(player.getPosition()[1] - y);

                    if (player.getId() == playerId) {
                        distance += 1;
                    }

                    if (distance > minDistance) {
                        continue;
                    }
                    else if (distance == minDistance) {
                        mask.set((byte) -2, x, y);
                    }
                    else {
                        minDistance = distance;
                        mask.set(player.getId(), x, y);
                    }
                }
            }
        }

        return mask.count(v -> v == playerId) - state.field.count(v -> v == playerId);
    }
}
