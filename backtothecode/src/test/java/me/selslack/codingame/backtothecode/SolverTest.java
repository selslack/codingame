package me.selslack.codingame.backtothecode;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pashka on 10/4/15.
 */
public class SolverTest {
    @Test
    public void testTest1() throws Exception {
        for (int i = 0; i < 30; i++) {
            System.out.println(new Solver().test(110, i));
        }
    }
}