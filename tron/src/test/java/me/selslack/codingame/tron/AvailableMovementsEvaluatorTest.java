package me.selslack.codingame.tron;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Measurement(iterations = 5)
@Warmup(iterations = 5)
@Fork(3)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class AvailableMovementsEvaluatorTest {
    private Game game;
    private AI.Evaluator evaluator;

    @Setup
    public void setupState() {
        game = new Game();
        game.initialize(2, 0);
        game.push(0, 0, 0, new Random().nextInt(10),      new Random().nextInt(30));
        game.push(1, 0, 0, new Random().nextInt(10) + 10, new Random().nextInt(30));

        evaluator = new AI.PossibleMovementsEvaluator();
    }

    @Benchmark
    public void evaluate() {
        int score = evaluator.evaluate(game, 0);

        if (score != 200) {
            throw new RuntimeException("Possible movements = " + score);
        }
    }
}