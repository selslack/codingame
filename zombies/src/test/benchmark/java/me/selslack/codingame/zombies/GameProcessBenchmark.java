package me.selslack.codingame.zombies;

import org.openjdk.jmh.annotations.*;

public class GameProcessBenchmark extends AbstractBenchmark {
    @State(Scope.Thread)
    public static class GameStateState {
        public GameState state = new GameState();

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
}
