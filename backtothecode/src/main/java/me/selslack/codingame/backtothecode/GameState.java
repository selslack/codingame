package me.selslack.codingame.backtothecode;

import java.util.Arrays;

public class GameState implements Cloneable {
    static final public int
        DIMENSIONS = 2,
        X          = 35,
        Y          = 20;

    public int round;
    public GameField field;
    private Player[] players;

    public GameState(int opponents) {
        switch (opponents) {
            case 1:
                players = new Player[] { new Player(0), new Player(1), };
                break;

            case 2:
                players = new Player[] { new Player(0), new Player(1), new Player(2), };
                break;

            case 3:
                players = new Player[] { new Player(0), new Player(1), new Player(2), new Player(3), };
                break;

            default:
                throw new IllegalArgumentException("Invalid number of opponents: " + opponents);
        }

        round = 0;
        field = new GameField(X, Y).fill(-1, v -> true);
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
        result.players = Arrays.stream(this.players).map(Player::clone).toArray(Player[]::new);

        return result;
    }

    static public class Player implements Cloneable {
        private int id;
        private int[] position;
        public int backshifts;

        Player(int id) {
            this.id = id;
        }

        public int getId() {
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
}
