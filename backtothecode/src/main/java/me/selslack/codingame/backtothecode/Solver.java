package me.selslack.codingame.backtothecode;

import me.selslack.codingame.backtothecode.heuristics.*;

import java.util.*;

public class Solver {
    public int[] solve(GameState state, byte playerId) {
        Map<Game.Direction, Double> map = new HashMap<>();
        Evaluator evaluator = new Evaluator();

        evaluator.addHeuristic(new AvoidInvalidMoveHeuristic(), 1.0);
        evaluator.addHeuristic(new AreaControlHeuristic(), 0.0015);
        evaluator.addHeuristic(new FillCellHeuristic(), 20.0);
        evaluator.addHeuristic(new EarlyGameFillHeuristic(), 1.0);

        for (Game.Direction direction : Game.Direction.directions()) {
            map.put(direction, evaluator.evaluate(state, playerId, direction));
        }

        return map.entrySet().stream()
            .sorted(Comparator.comparingDouble(v -> -v.getValue()))
            .map(v -> state.getField().sum(state.getPlayer(playerId).getPosition(), v.getKey().getDimensions()).orElse(new int[] { -1, -1 }))
            .findFirst()
            .orElse(new int[] { -1, -1 });
    }
}
