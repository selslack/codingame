package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.server.GameStateBuilder;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

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
    public GameState benchmarkProcess(GameStateState state) {
        return Game.process(state.state, new Waypoint(2000, 3000));
    }

//    @Benchmark
    public void benchmarkHumanMovement(GameStateState state) {
        Game.humanMovement(state.state.getAsh(), state.state.getZombies().get(0));
    }

//    @Test
    public void testHumanMovement() {
        Human subject1 = new Human(Human.Type.ZOMBIE, 7, 5000, 1000);
        Human subject2 = new Human(Human.Type.ZOMBIE, 7, 5000, 1000);

        Game.humanMovement(subject1, 4000, 1000);
//        Game.humanMovementSqrt(subject2, 4000, 1000);

        assertEquals(subject1.x, subject2.x);
        assertEquals(subject1.y, subject2.y);
    }
}