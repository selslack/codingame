package me.selslack.codingame.backtothecode;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(3)
public class GameTest {
    @Benchmark
    @OperationsPerInvocation(14)
    public GameState benchmarkMove() {
        GameState state = new GameState(1);

        state.getPlayer(0).setPosition(5, 5);
        state.getField().set(0, 5, 5);

        Game.move(state, 0, Game.Direction.UP);
        Game.move(state, 0, Game.Direction.RIGHT);
        Game.move(state, 0, Game.Direction.RIGHT);
        Game.move(state, 0, Game.Direction.RIGHT);
        Game.move(state, 0, Game.Direction.RIGHT);
        Game.move(state, 0, Game.Direction.DOWN);
        Game.move(state, 0, Game.Direction.LEFT);
        Game.move(state, 0, Game.Direction.DOWN);
        Game.move(state, 0, Game.Direction.DOWN);
        Game.move(state, 0, Game.Direction.UP);
        Game.move(state, 0, Game.Direction.LEFT);
        Game.move(state, 0, Game.Direction.LEFT);
        Game.move(state, 0, Game.Direction.LEFT);
        Game.move(state, 0, Game.Direction.UP);

        return state;
    }
}