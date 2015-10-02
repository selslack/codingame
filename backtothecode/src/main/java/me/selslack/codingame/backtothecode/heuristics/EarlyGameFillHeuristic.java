package me.selslack.codingame.backtothecode.heuristics;

import me.selslack.codingame.backtothecode.Game;
import me.selslack.codingame.backtothecode.GameState;

import java.util.Arrays;

public class EarlyGameFillHeuristic implements Heuristic {
    static private int[][][] metaRegions = new int[][][] {
        new int[][] { new int[] { 0, 0 },   new int[] { 10, 9  } },
        new int[][] { new int[] { 11, 0 },  new int[] { 22, 9  } },
        new int[][] { new int[] { 23, 0 },  new int[] { 34, 9  } },
        new int[][] { new int[] { 0, 10 },  new int[] { 10, 19 } },
        new int[][] { new int[] { 11, 10 }, new int[] { 22, 19 } },
        new int[][] { new int[] { 23, 10 }, new int[] { 34, 19 } },
    };

    @Override
    public double score(GameState state, byte playerId, Game.Direction direction) {
        Game.move(state, playerId, direction);

        if (state.getField().count(v -> v < 0) < 250) {
            return 0;
        }

        double score = 0;

        for (int[][] region : metaRegions) {
            score += valueOfRegion(state, playerId, region);
        }

        return score;
    }

    private double valueOfRegion(GameState state, byte playerId, int[][] region) {
        for (int x = region[0][0]; x <= region[1][0]; x++) {
            for (int y = region[0][1]; y <= region[1][1]; y++) {
                if (state.getField().get(x, y) != -1 && state.getField().get(x, y) != playerId) {
                    return 0;
                }
            }
        }

        GameState.Player player = state.getPlayer(playerId);
        int filled = 0;
        int distance = Integer.MAX_VALUE;
        boolean isFilled = true;

        for (int x = region[0][0]; x <= region[1][0]; x++) {
            for (int y : new int[] { region[0][1], region[1][1] }) {
                int localDistance = Math.abs(player.getPosition()[0] - x) + Math.abs(player.getPosition()[1] - y);

                if (localDistance < distance) {
                    distance = localDistance;
                }
            }
        }

        for (int y = region[0][1]; y <= region[1][1]; y++) {
            for (int x : new int[] { region[0][0], region[1][0] }) {
                int localDistance = Math.abs(player.getPosition()[0] - x) + Math.abs(player.getPosition()[1] - y);

                if (localDistance < distance) {
                    distance = localDistance;
                }
            }
        }

        for (int x = region[0][0]; x <= region[1][0]; x++) {
            for (int y = region[0][1]; y <= region[1][1]; y++) {
                isFilled = isFilled && state.getField().get(x, y) == playerId;
            }
        }

        if (distance > 0 && !isFilled) {
            return (1.0 / distance) * 30.0;
        }

        for (int x = region[0][0]; x <= region[1][0]; x++) {
            filled += state.getField().get(x, region[0][1]) == playerId ? 1 : 0;
            filled += state.getField().get(x, region[1][1]) == playerId ? 1 : 0;
        }

        for (int y = region[0][1]; y <= region[1][1]; y++) {
            filled += state.getField().get(region[0][0], y) == playerId ? 1 : 0;
            filled += state.getField().get(region[1][0], y) == playerId ? 1 : 0;
        }

        return 100 + filled * 30;
    }
}
