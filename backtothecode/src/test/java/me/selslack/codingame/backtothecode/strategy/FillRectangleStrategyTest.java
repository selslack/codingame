package me.selslack.codingame.backtothecode.strategy;

import me.selslack.codingame.backtothecode.GameField;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(3)
public class FillRectangleStrategyTest {
    public GameField field = new GameField(35, 20).fill(-1, v -> true);

    @Test
    public void testDoIt() throws Exception {
        GameField field = new GameField(35, 20).fill(-1, v -> true);

        field.set(1, 10, 10);
        field.set(2, 30, 18);

        new FillRectangleStrategy().doIt(field, 0, new int[] { 4, 3 });
    }

    @Benchmark
    public Deque<Deque<int[]>> benchmarkDoIt() {
        field.set(1, 10, 10);
        field.set(2, 30, 18);

        return FillRectangleStrategy.doIt(field, 0, new int[]{4, 3});
    }
}