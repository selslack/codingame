package me.selslack.codingame.zombies;

public class Utils {
    static final public double EPSILON = 1E-9;

    /**
     * Returns the distance between points (x1, y1) and (x2, y2).
     */
    static public double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
    }

    /**
     * Returns the squared distance between points (x1, y1) and (x2, y2).
     */
    static public int distanceSq(int x1, int y1, int x2, int y2) {
        final int xd = x1 - x2;
        final int yd = y1 - y2;

        return xd * xd + yd * yd;
    }

    /**
     * Finds an intersection of a circle of radius {@code d} with the center at ({@code x1}, {@code y1})
     * and a ray starting from ({@code x1}, {@code y1}) and going through ({@code x2}, {@code y2}).
     *
     * Trigonometric implementation.
     */
    static public double[] projectionPrecise(int x1, int y1, int x2, int y2, int d) {
        double angle = Math.atan2(y2 - y1, x2 - x1);

        return new double[] {
            d * Math.cos(angle),
            d * Math.sin(angle)
        };
    }

    /**
     * Finds an intersection of a circle of radius {@code d} with the center at ({@code x1}, {@code y1})
     * and a ray starting from ({@code x1}, {@code y1}) and going through ({@code x2}, {@code y2}).
     *
     * Math implementation.
     */
    static public double[] projectionFast(int x1, int y1, int x2, int y2, int d) {
        int sign = x2 < x1 || x1 == x2 && y2 < y1 ? -1 : 1;

        if (x1 == x2) {
            return new double[] { 0.0, (double) d * sign };
        }
        else if (y1 == y2) {
            return new double[] { (double) d * sign, 0.0 };
        }

        double slope = (double) (y2 - y1) / (double) (x2 - x1);
        double result = sign * d / Math.sqrt(Math.pow(slope, 2.0) + 1);

        return new double[] {
            result,
            slope * result
        };
    }
}
