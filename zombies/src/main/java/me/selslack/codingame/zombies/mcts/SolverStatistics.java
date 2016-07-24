package me.selslack.codingame.zombies.mcts;

import java.util.LongSummaryStatistics;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SolverStatistics {
    Optional<LongSummaryStatistics> statistics = Optional.empty();
    long startTime = 0L;
    long prevInvocationTime = 0L;

    protected void reset() {
        statistics = Optional.of(new LongSummaryStatistics());
        startTime = prevInvocationTime = System.nanoTime();
    }

    protected void done() {
        statistics.get().accept(
            System.nanoTime() - prevInvocationTime
        );

        prevInvocationTime = System.nanoTime();
    }

    protected long getRunningTime(TimeUnit timeUnit) {
        return timeUnit.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }

    @Override
    public String toString() {
        if (statistics.isPresent()) {
            return "SolverStatistics{count=" + statistics.get().getCount() + ", average=" + String.format("%.5g", statistics.get().getAverage() / 1E6d) + "ms}";
        } else {
            return "SolverStatistics{NO DATA}";
        }
    }
}
