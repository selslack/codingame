package me.selslack.codingame.backtothecode;

import me.selslack.codingame.backtothecode.strategy.AbstractStrategy;
import me.selslack.codingame.backtothecode.strategy.NoOpStrategy;
import me.selslack.codingame.backtothecode.strategy.StrategySupplier;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private List<StrategySupplier> suppliers;
    private AbstractStrategy activeStrategy;
    private Map<Integer, AbstractStrategy> history;
    private int roundNumber;

    public Solver() {
        this.suppliers = new LinkedList<>();
        this.history = new HashMap<>();

    }

    public void addStrategySupplier(StrategySupplier supplier) {
        suppliers.add(supplier);
    }

    public String solve(GameState state, int playerId) {
        long startTime = System.nanoTime();

        if (roundNumber >= state.round) {
            activeStrategy = history.get(state.round);
        }

        roundNumber = state.round;
        activeStrategy = pickBestStrategy(activeStrategy, state, playerId);

        history.put(state.round, activeStrategy);

        return Arrays
            .stream(activeStrategy.getMove(roundNumber))
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(" ")) + " " + ((System.nanoTime() - startTime) / 1000000.0);
    }

    private double evaluateStrategy(AbstractStrategy strategy, int roundNumber) {
        return strategy.getPredictedScore() / (1 + 0.01 * Math.exp(strategy.getEffectiveRemainingMoves(roundNumber) * 0.1));
    }

    public double test(int score, int rounds) {
        return score / (1 + 0.01 * Math.exp(rounds * 0.1));
    }

    private AbstractStrategy pickBestStrategy(AbstractStrategy activeStrategy, GameState state, int playerId) {
        AbstractStrategy bestStrategy;
        Map<AbstractStrategy, Double> strategies = new HashMap<>();

        for (StrategySupplier supplier : suppliers) {
            for (AbstractStrategy strategy : supplier.get(state, playerId)) {
                if (strategy.isFinished(roundNumber)) {
                    continue;
                }

                strategies.put(strategy, evaluateStrategy(strategy, roundNumber));
            }
        }

        try {
            bestStrategy = strategies.entrySet().stream()
                .sorted(Comparator.comparingDouble(v -> -v.getValue()))
                .map(v -> v.getKey())
                .findFirst()
                .orElseThrow(RuntimeException::new);
        }
        catch (RuntimeException e) {
            bestStrategy = new NoOpStrategy(state, playerId);
        }

        if (activeStrategy == null || activeStrategy.isFinished(roundNumber)) {
            return bestStrategy;
        }
        else {
            if (evaluateStrategy(bestStrategy, roundNumber) / evaluateStrategy(activeStrategy, roundNumber) <= 1.15) {
                return activeStrategy;
            }
            else {
                return bestStrategy;
            }
        }
    }
}
