package me.selslack.codingame.tron;

import org.junit.Test;

import static org.junit.Assert.*;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Measurement(iterations = 5)
@Warmup(iterations = 5)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class GameRulesTest {
    @Benchmark
    @OperationsPerInvocation(4)
    public void testPushState() throws Exception {
        GameState state = new GameState(4, 20, 30);
        Random    rnd   = new Random();

        for (int i = 0; i < state.getPlayerCount(); i++) {
            state.push(i, rnd.nextInt(19), i);
        }
    }

    @Benchmark
    public void testClone() throws Exception {
        GameState state = new GameState(4, 20, 30);
        Random    rnd   = new Random();

        state.push(rnd.nextInt(4), rnd.nextInt(20), rnd.nextInt(30));
        state.clone();
    }
}