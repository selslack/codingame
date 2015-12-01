package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.Solver;
import me.selslack.codingame.zombies.mcts.Waypoint;
import me.selslack.codingame.zombies.server.GameServer;
import org.openjdk.jmh.annotations.*;

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

    public int benchmarkDistance(DistanceState state) {
        return Utils.distance(state.from, state.to);
    }

    @State(Scope.Thread)
    static public class SimplePlayoutState {
        public GameState state;

        @Setup(Level.Invocation)
        public void setUp() {
            state = GameServer.cases[2].clone();
        }
    }

    @Benchmark
    public void benchmarkPlayout(SimplePlayoutState state) {
        Solver.playout(state.state, 25);
    }
}
