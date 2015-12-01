package me.selslack.codingame.zombies;

import me.selslack.codingame.zombies.mcts.Waypoint;

public interface Communicator {
    void wasteNextRead();
    void readState(GameState state) throws InterruptedException;
    void sendCommand(Waypoint action) throws InterruptedException;
}
