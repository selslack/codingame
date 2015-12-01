package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.mcts.Config;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameServerTest {
    @Test
    public void testRun() throws Exception {
        assertEquals(10, (int) GameServer.run(GameServer.cases[2], new Config()).get());
    }
}