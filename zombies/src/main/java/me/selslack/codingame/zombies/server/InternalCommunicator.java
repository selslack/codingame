package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.Communicator;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.Waypoint;

/**
 * Internal server does not communicate with clients. Instead, it executes client's solver directly.
 */
public class InternalCommunicator implements Communicator {
    @Override
    public void readState(GameState state, boolean toDevNull) {

    }

    @Override
    public void sendCommand(Waypoint action) {

    }
}
