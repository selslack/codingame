package me.selslack.codingame.tron;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

final class GameField implements Cloneable {
    final
    private int[]   _dimensions;
    private byte[]  _map;

    public GameField(final int... dimensions) {
        _dimensions = dimensions;
        _map        = new byte[Arrays.stream(_dimensions).filter(i -> i >= 0).reduce((a, b) -> a * b).orElse(0)];
    }

    public void fill(byte value) {
        fill(value, v -> true);
    }

    public void fill(byte value, Predicate<Byte> predicate) {
        for (int i = 0; i < _map.length; i++) {
            if (predicate.test(_map[i])) {
                _map[i] = value;
            }
        }
    }

    public void set(byte value, final int... dimensions) {
        _map[index(dimensions)] = value;
    }

    public byte get(final int... dimensions) {
        return _map[index(dimensions)];
    }

    public int index(final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            throw new IllegalArgumentException("Dimensions do not match");
        }

        int result = 0;

        for (byte i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                throw new IllegalArgumentException("Invalid dimension given for axis " + i + ": " + Arrays.deepToString(IntStream.of(dimensions).boxed().toArray()));
            }

            result = dimensions[i] + _dimensions[i] * result;
        }

        return result;
    }

    public boolean check(final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            return false;
        }

        for (byte i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected GameField clone() {
        GameField result;

        try {
            result = (GameField) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result._map = Arrays.copyOf(this._map, this._map.length);

        return result;
    }
}
