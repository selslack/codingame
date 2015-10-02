package me.selslack.codingame.backtothecode;

import java.util.Arrays;

public class GameState implements Cloneable {
    static public class Player implements Cloneable {
        private byte id;
        private int[] position;
        public int backshifts;

        Player(byte id) {
            this.id = id;
        }

        public byte getId() {
            return id;
        }

        public int[] getPosition() {
            return position;
        }

        public void setPosition(int... coordinates) {
            if (coordinates.length != GameState.DIMENSIONS) {
                throw new IllegalArgumentException("Invalid dimensions supplied: " + coordinates.length);
            }

            position = coordinates;
        }

        public boolean isActive() {
            return position[0] >= 0 && position[0] < X
                && position[1] >= 0 && position[1] < Y;
        }

        @Override
        public Player clone() {
            Player result;

            try {
                result = (Player) super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

            result.position = Arrays.copyOf(this.position, this.position.length);

            return result;
        }
    }

    static final public int
        DIMENSIONS = 2,
        X          = 35,
        Y          = 20;

    public int round;
    public GameField field;
    private Player[] players;

    final private int maxRounds;

    public GameState(int opponents) {
        switch (opponents) {
            case 1:
                players = new Player[] { new Player((byte) 0), new Player((byte) 1), };
                maxRounds = 350;
                break;

            case 2:
                players = new Player[] { new Player((byte) 0), new Player((byte) 1), new Player((byte) 2), };
                maxRounds = 300;
                break;

            case 3:
                players = new Player[] { new Player((byte) 0), new Player((byte) 1), new Player((byte) 2), new Player((byte) 3), };
                maxRounds = 250;
                break;

            default:
                throw new IllegalArgumentException("Invalid number of opponents: " + opponents);
        }

        round = 0;

        field = new GameField(X, Y);
        field.fill((byte) -1);
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public Player getPlayer(int playerId) {
        if (playerId < 0 || playerId >= players.length) {
            throw new IllegalArgumentException("Invalid playerId: " + playerId);
        }

        return players[playerId];
    }

    public Player[] getPlayers() {
        return players;
    }

    @Override
    public GameState clone() {
        GameState result;

        try {
            result = (GameState) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result.field = this.field.clone();
        result.players = Arrays.stream(this.players).map(v -> v.clone()).toArray(Player[]::new);

        return result;
    }
}
