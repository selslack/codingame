package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.mcts.Config;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameServerTest {
    @Test
    public void testRun() throws Exception {
        System.out.println(
            new ParallelGameTask(new Config(), GameStateBuilder.build("03-2_zombies_redux"), 1).fork().join()
        );
    }
}