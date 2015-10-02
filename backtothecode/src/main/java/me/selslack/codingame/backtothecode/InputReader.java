package me.selslack.codingame.backtothecode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InputReader {
    final static private
        Map<Character, Byte> fieldStateSymbolToByteMap = new HashMap<>();

    static {
        fieldStateSymbolToByteMap.put('.', (byte) -1);
        fieldStateSymbolToByteMap.put('0', (byte) 0);
        fieldStateSymbolToByteMap.put('1', (byte) 1);
        fieldStateSymbolToByteMap.put('2', (byte) 2);
        fieldStateSymbolToByteMap.put('3', (byte) 3);
    }

    static public GameState readOpponentCount(Scanner in) {
        return new GameState(in.nextInt());
    }

    static public void readRoundNumber(GameState state, Scanner in) {
        state.round = in.nextInt();
    }

    static public void readPlayerState(GameState state, int playerId, Scanner in) {
        if (playerId < 0 || playerId >= state.getPlayers().length) {
            throw new IllegalArgumentException("Invalid playerId for this state: " + playerId);
        }

        int x = in.nextInt(),
            y = in.nextInt(),
            backshifts  = in.nextInt();

        state.getPlayers()[playerId].setPosition(x, y);
        state.getPlayers()[playerId].backshifts = backshifts;
    }

    static public void readMapState(GameState state, int line, Scanner in) {
        String data = in.next();

        if (data.length() != GameState.X) {
            throw new IllegalArgumentException("Invalid map state line length: " + data.length());
        }

        for (int i = 0; i < GameState.X; i++) {
            Character symbol = data.charAt(i);

            if (! fieldStateSymbolToByteMap.containsKey(symbol)) {
                throw new IllegalArgumentException("Unknown map state symbol: " + symbol);
            }

            state.getField().set(fieldStateSymbolToByteMap.get(symbol), i, line);
        }
    }
}
