package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameState;
import me.selslack.codingame.backtothecode.MovementUtils;

import java.util.*;
import java.util.function.IntPredicate;

public class SimpleStrategySupplier implements StrategySupplier {
    @Override
    public List<AbstractStrategy> get(GameState state, int playerId) {
        IntPredicate distanceCheck = v -> v > 0;
        Random rnd = new Random();
        LinkedList<AbstractStrategy> result = new LinkedList<>();
        Map<int[], Integer> distances = new HashMap<>();

        if (state.round < 30 || state.field.count(v -> v < 0) >= 450) {
            distanceCheck = v -> v > 12;
        }
        else if (state.round < 100 || state.field.count(v -> v < 0) >= 350) {
            distanceCheck = v -> v > 8;
        }

        for (int x = 0; x < GameState.X; x++) {
            for (int y = 0; y < GameState.Y; y++) {
                int[] point = new int[] {x, y};
                int distance = state.field.manhattan(point, state.getPlayer(playerId).getPosition());

                if (state.field.get(point) >= 0 || !distanceCheck.test(distance)) {
                    continue;
                }

                distances.put(point, distance);
            }
        }

        distances.entrySet().stream()
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .limit(20)
            .map(Map.Entry::getKey)
            .map(v -> MovementUtils.moveToPoint(state.getPlayer(playerId).getPosition(), v, false, rnd.nextBoolean()))
            .forEach(v -> result.add(new MovementSequenceStrategy(state, playerId, v)));

        return result;
    }
}
