package org.mutabilitydetector.demo;


import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import org.junit.Test;
import org.mutabilitydetector.demo.PointTest.Point;

public class CircleTest {
    
    @Test
    public void circleIsImmutable() throws Exception {
        assertImmutable(Circle.class);
    }

    public static final class Circle {
        public final int radius;
        public final Point centre;

        public Circle(int radius, Point centre) {
            this.radius = radius;
            this.centre = centre;
        }
    }
}
