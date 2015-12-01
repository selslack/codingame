package me.selslack.codingame.zombies;

public interface Communicator {
    void readState(GameState state, boolean toDevNull);
    void sendCommand(Waypoint action);
}
