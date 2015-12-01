package me.selslack.codingame.zombies;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class UtilsTest {
    @Test
    public void testDistance() {
        assertEquals(401, Utils.distance(0, 0, 283, 283));
    }
}