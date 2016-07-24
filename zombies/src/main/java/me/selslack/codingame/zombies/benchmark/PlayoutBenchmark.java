package me.selslack.codingame.zombies.benchmark;

import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.mcts.FlexibleMoveGenerator;
import me.selslack.codingame.zombies.mcts.MoveGenerator;
import me.selslack.codingame.zombies.mcts.Solver;
import me.selslack.codingame.zombies.server.GameStateBuilder;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.List;

public class PlayoutBenchmark extends AbstractBenchmark {
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
