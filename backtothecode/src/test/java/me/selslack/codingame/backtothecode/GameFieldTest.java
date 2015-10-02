package me.selslack.codingame.backtothecode;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfProfiler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(3)
public class GameFieldTest {
    public GameField field = new GameField(35, 20).fill(-1, v -> true);

    @Benchmark
    public long benchmarkCount() throws Exception {
        return field.count(v -> v == -1);
    }

    @Benchmark
    public GameField benchmarkClone() throws Exception {
        return field.clone();
    }

    @Test
    public void testDecode() {
        GameField field = new GameField(10, 20, 30);

        assertArrayEquals(new int[]{5, 3, 1}, field.decode(field.hash(5, 3, 1)));
        assertArrayEquals(new int[]{7, 17, 27}, field.decode(field.hash(7, 17, 27)));
    }

    @Test
    public void testHash() {
        GameField field = new GameField(10, 20);

        assertEquals(100, field.hash(5, 0));
    }
}