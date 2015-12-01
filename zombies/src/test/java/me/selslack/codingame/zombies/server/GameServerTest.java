package me.selslack.codingame.zombies.server;

import org.junit.Test;

import java.util.concurrent.ForkJoinTask;

import static org.junit.Assert.*;

public class GameServerTest {
    @Test
    public void testRun() throws Exception {
        assertEquals(10, (int) GameServer.run(GameServer.cases[0]).compute());
    }
}