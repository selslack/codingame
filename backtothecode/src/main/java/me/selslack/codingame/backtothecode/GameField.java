package me.selslack.codingame.backtothecode;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

final public class GameField implements Cloneable {
    final
    private int[] _dimensions;
    private int[] _map;

    public GameField(final int... dimensions) {
        _dimensions = dimensions;
        _map        = new int[Arrays.stream(_dimensions).filter(i -> i >= 0).reduce((a, b) -> a * b).orElse(0)];
    }

    public void set(int value, final int... dimensions) {
        set(value, v -> true, dimensions);
    }

    public void set(int value, IntPredicate predicate, final int... dimensions) {
        int index = hash(dimensions);

        if (predicate.test(_map[index])) {
            _map[index] = value;
        }
    }

    public int get(final int... dimensions) {
        return _map[hash(dimensions)];
    }

    public int hash(final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            throw new IllegalArgumentException("Dimensions do not match");
        }

        int result = 0;

        for (int i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                throw new IllegalArgumentException("Invalid dimension given for axis " + i + ": " + Arrays.deepToString(IntStream.of(dimensions).boxed().toArray()));
            }

            result = dimensions[i] + _dimensions[i] * result;
        }

        return result;
    }

    public int[] decode(int hash) {
        int[] result = new int[_dimensions.length];

        for (int i = _dimensions.length - 1; i >= 0; i--) {
            result[i] = hash % _dimensions[i];
            hash = hash / _dimensions[i];
        }

        return result;
    }

    public boolean check(final int... dimensions) {
        return check(false, dimensions);
    }

    public boolean check(boolean quick, final int... dimensions) {
        if (!(_dimensions.length == dimensions.length)) {
            return false;
        }

        if (quick) {
            return true;
        }

        for (byte i = 0; i < _dimensions.length; i++) {
            if (!(dimensions[i] >= 0 && dimensions[i] < _dimensions[i])) {
                return false;
            }
        }

        return true;
    }

    public int manhattan(final int[] a, final int[] b) {
        return 0;
    }

    public Optional<int[]> sum(final int[] initial, final int[]... components) {
        if (initial.length != _dimensions.length) {
            return Optional.empty();
        }

        int[] result = initial.clone();

        for (int[] component : components) {
            if (component.length != _dimensions.length) {
                return Optional.empty();
            }

            for (int i = 0; i < _dimensions.length; i++) {
                result[i] += component[i];
            }
        }

        if (!check(result)) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    public void fill(int value) {
        fill(value, v -> true);
    }

    public GameField fill(int value, IntPredicate predicate) {
        for (int i = 0; i < _map.length; i++) {
            if (predicate.test(_map[i])) {
                _map[i] = value;
            }
        }

        return this;
    }

    public GameField fill(int value, IntPredicate vPredicate, Predicate<int[]> cPredicate) {
        for (int i = 0; i < _map.length; i++) {
            if (vPredicate.test(_map[i])) {
                _map[i] = value;
            }
        }

        return this;
    }


    public int count(IntPredicate predicate) {
        return (int) Arrays.stream(_map).filter(predicate).count();
    }

    @Override
    public GameField clone() {
        GameField result;

        try {
            result = (GameField) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        result._map = _map.clone();

        return result;
    }
}