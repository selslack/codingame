package me.selslack.codingame.zombies;

import org.openjdk.jmh.annotations.*;

public class GameHumanMovementBenchmark extends AbstractBenchmark {
    @State(Scope.Thread)
    static public class MovementState {
        public Human ash;
        public Waypoint to;

        @Setup(Level.Iteration)
        public void setUpIteration() {
            ash = new Human(Human.Type.ASH, 0, rnd.nextInt(16000), rnd.nextInt(9000));
        }

        @Setup(Level.Invocation)
        public void setUpTrial() {
            to = new Waypoint(rnd.nextInt(16000), rnd.nextInt(9000));
        }
    }

    @Benchmark
    public void benchmarkHumanMovement(MovementState state) {
        Game.humanMovement(state.ash, state.to);
    }
}
