package me.selslack.codingame.zombies.server;

import me.selslack.codingame.zombies.Communicator;
import me.selslack.codingame.zombies.GameState;
import me.selslack.codingame.zombies.mcts.Waypoint;

import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class GameServerCommunicator implements Communicator {
    final private LinkedTransferQueue<GameState> toClient = new LinkedTransferQueue<>();
    final private LinkedTransferQueue<Waypoint> toServer = new LinkedTransferQueue<>();

    private boolean toDevNull = false;

    @Override
    public void wasteNextRead() {
        toDevNull = true;
    }

    public GameServerCommunicator sendState(GameState state) throws InterruptedException {
        toClient.transfer(
            state.clone()
        );

        return this;
    }

    @Override
    public void readState(GameState state) throws InterruptedException {
        GameState serverState = toClient.take();

        if (toDevNull) {
            // Do nothing...
        }
        else {
            state.getAsh().x = serverState.getAsh().x;
            state.getAsh().y = serverState.getAsh().y;

            state.getHumans().clear();
            state.getZombies().clear();

            state.getHumans().addAll(serverState.getHumans());
            state.getZombies().addAll(serverState.getZombies());
        }

        toDevNull = false;
    }

    @Override
    public void sendCommand(Waypoint action) throws InterruptedException {
        toServer.transfer(action);
    }

    public Optional<Waypoint> readCommand(long time, TimeUnit timeUnit) throws InterruptedException {
        return Optional.ofNullable(toServer.poll(time, timeUnit));
    }
}
