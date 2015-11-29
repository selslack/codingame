package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.Waypoint;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testClusterize() throws Exception {
        List<Waypoint> list = new LinkedList<>();

        list.add(new Waypoint(1000, 1000));
        list.add(new Waypoint(1500, 1000));
        list.add(new Waypoint(1500, 1200));
        list.add(new Waypoint(1250, 1200));
        list.add(new Waypoint(1250, 1300));

        list.add(new Waypoint(2350, 1300));

        System.out.println(Utils.clusterize(list, 1000));
    }
}