package org.mutabilitydetector.demo;

import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssert;

public class PointTest {
    
    @Test
    public void pointIsImmutable() throws Exception {
        MutabilityAssert.assertInstancesOf(Point.class, areImmutable());
    }

    public static final class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
