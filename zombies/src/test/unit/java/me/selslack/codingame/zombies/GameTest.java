package me.selslack.codingame.zombies;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {
    @Test
    public void testHumanMovement() {
        Human subject1 = new Human(Human.Type.ZOMBIE, 7, 5000, 1000);

        Game.humanMovement(subject1, 6000, 2000);

        assertEquals(5282, subject1.x);
        assertEquals(1282, subject1.y);
    }
}