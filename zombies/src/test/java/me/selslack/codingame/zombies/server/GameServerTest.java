package me.selslack.codingame.zombies.server;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameServerTest {
    @Test
    public void testRun() throws Exception {
        assertEquals(10, new GameServer(GameServer.cases[0]).run());
    }
}