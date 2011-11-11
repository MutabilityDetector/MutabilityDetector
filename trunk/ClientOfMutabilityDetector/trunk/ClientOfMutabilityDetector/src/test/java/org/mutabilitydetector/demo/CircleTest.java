package org.mutabilitydetector.demo;

import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.demo.PointTest.Point;
import org.mutabilitydetector.unittesting.MutabilityAssert;

public class CircleTest {
    @Test
    public void circleIsImmutable() throws Exception {
        MutabilityAssert.assertInstancesOf(Circle.class, areImmutable());
    }

    public static final class Circle {
        public final int radius;
        public final Point point;

        public Circle(int radius, Point point) {
            this.radius = radius;
            this.point = point;
        }
    }
}
