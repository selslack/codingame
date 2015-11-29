package me.selslack.codingame.zombies.mcts;

import me.selslack.codingame.zombies.Game;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Utils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KitingWaypointGenerator implements WaypointGenerator {
    @Override
    public List<Waypoint> generate(GameState state) {
        GameState localState = state.clone();
        List<Waypoint> result = new LinkedList<>();

        Game.process(localState);

        List<Waypoint> clusters = Utils.clusterize(new ZombiesWaypointGenerator().generate(localState), 1500);

        if (clusters.isEmpty()) {
            return result;
        }

        result.add(clusters.stream().sorted(Comparator.comparingInt(v -> -v.marker)).findFirst().get());

        return result;
    }
}
