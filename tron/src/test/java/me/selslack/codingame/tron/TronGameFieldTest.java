package me.selslack.codingame.tron;

import org.junit.Before;

import static org.junit.Assert.*;

public class TronGameFieldTest {
    Game.TronGameField field;

    @Before
    public void setUp() throws Exception {
        field = new Game.TronGameField(2);
    }
}