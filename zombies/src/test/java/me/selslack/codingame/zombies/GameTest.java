package me.selslack.codingame.zombies;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class GameTest {
    @State(Scope.Thread)
    public static class GameStateState {
        public GameState state = new GameState();
        public Random rnd = new Random();

        @Setup(Level.Invocation)
        public void setUp() {
            state.getAsh().x = rnd.nextInt(2000);
            state.getAsh().y = rnd.nextInt(2000);

            state.getZombies().clear();

            for (int i = 0; i < 26; i++) {
                state.getZombies().add(new Human(Human.Type.ZOMBIE, i, rnd.nextInt(10000) + 5000, rnd.nextInt(4000) + 5000));
            }

            state.getHumans().clear();
            state.getHumans().add(new Human(Human.Type.HUMAN, 0, rnd.nextInt(5000), rnd.nextInt(3000)));
            state.getHumans().add(new Human(Human.Type.HUMAN, 1, rnd.nextInt(5000), rnd.nextInt(3000)));
            state.getHumans().add(new Human(Human.Type.HUMAN, 2, rnd.nextInt(5000), rnd.nextInt(3000)));
        }
    }

    @Benchmark
    public void benchmarkProcess(GameStateState state) {
        Game.process(state.state, 2000, 1000);
    }

    @Benchmark
    public void benchmarkHumanMovement(GameStateState state) {
        Game.humanMovement(state.state.getAsh(), state.state.getZombies().get(0));
    }
}