package org.mutabilitydetector.demo;

import org.junit.Test;

public class PointTest {
    
    // all the other unit tests you undoubtedly have
    
    @Test
    public void pointIsImmutable() throws Exception {
        // how to check?
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
