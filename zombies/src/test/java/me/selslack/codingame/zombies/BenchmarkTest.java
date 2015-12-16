package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.FlexibleMoveGenerator;
import me.selslack.codingame.zombies.mcts.Solver;
import me.selslack.codingame.zombies.mcts.MoveGenerator;
import me.selslack.codingame.zombies.server.GameStateBuilder;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class BenchmarkTest {
    // Some randomness is always good
    static final public Random rnd = new Random();

    @State(Scope.Thread)
    static public class DistanceState {
        public Waypoint from;
        public Waypoint to;

        @Setup(Level.Invocation)
        public void setUp() {
            from = new Waypoint(rnd.nextInt(16000), rnd.nextInt(9000));
            to = new Waypoint(rnd.nextInt(16000), rnd.nextInt(9000));
        }
    }

    public double benchmarkDistance(DistanceState state) {
        return Utils.distance(state.from.x, state.from.y, state.to.x, state.to.y);
    }

    @State(Scope.Thread)
    static public class SimplePlayoutState {
        public GameState state;
        public List<MoveGenerator> generators;
        public Solver solver;

        @Setup(Level.Invocation)
        public void setUp() {
            state = GameStateBuilder.build("03-2_zombies_redux");
            generators = Arrays.asList(new FlexibleMoveGenerator(150, 3), new FlexibleMoveGenerator(1000, 5));
        }
    }

    @Benchmark
    public void benchmarkPlayout(SimplePlayoutState state) {
        state.solver.playout(state.state);
    }
}
