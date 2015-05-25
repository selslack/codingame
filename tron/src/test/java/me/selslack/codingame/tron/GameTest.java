package me.selslack.codingame.tron;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Measurement(iterations = 5)
@Warmup(iterations = 5)
@Fork(3)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class GameTest {
    final private Random rnd = new Random();

    private Game  game;
    private int   pId;
    private int[] coords;

    @Setup
    public void setupState() {
        game = new Game();
        game.initialize(4, 0);

        pId    = rnd.nextInt(4);
        coords = new int[] { rnd.nextInt(20), rnd.nextInt(30) };
    }

    @Benchmark
    public void push() {
        game.push(pId, 0, 0, coords[0], coords[1]);
    }
}