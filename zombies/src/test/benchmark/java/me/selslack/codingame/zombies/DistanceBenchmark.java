package me.selslack.codingame.zombies;

import org.openjdk.jmh.annotations.*;

public class DistanceBenchmark extends AbstractBenchmark {
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

    @Benchmark
    public double benchmarkDistance(DistanceState state) {
        return Utils.distance(state.from.x, state.from.y, state.to.x, state.to.y);
    }
}
