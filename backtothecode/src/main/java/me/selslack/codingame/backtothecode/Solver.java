package me.selslack.codingame.backtothecode;

import me.selslack.codingame.backtothecode.heuristics.*;
import me.selslack.codingame.backtothecode.strategy.FillNearestZoneStrategy;

import java.util.*;

public class Solver {
    public int[] solve(GameState state, int playerId) {
        Map<int[], Double> map = new HashMap<>();
        Evaluator evaluator = new Evaluator();

        evaluator.addHeuristic(new AreaControlHeuristic(), 0.0015);
        evaluator.addHeuristic(new FillCellHeuristic(), 1.0);
        evaluator.addHeuristic(new StrategyHeuristic(), 1.0);

        for (Game.Direction direction : Game.Direction.directions()) {
            Optional<int[]> point = state.field.sum(state.getPlayer(playerId).getPosition(), direction.getDimensions());

            if (!point.isPresent()) {
                continue;
            }

            map.put(point.get(), evaluator.evaluate(state, playerId, point.get()));
        }

        return map.entrySet().stream()
            .sorted(Comparator.comparingDouble(v -> -v.getValue()))
            .map(v -> v.getKey())
            .findFirst()
            .orElse(new int[]{-1, -1});
    }
}
