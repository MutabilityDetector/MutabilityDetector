package org.mutabilitydetector.demo;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Ignore;
import org.junit.Test;

public class CircleUsingAbstractPointTest {
    
    @Test
    @Ignore
    public void circleIsImmutable() throws Exception {
        assertInstancesOf(CircleUsingAbstractPoint.class, areImmutable());
    }
    
    public static final class CircleUsingAbstractPoint {
        public final int radius;
        public final Point centre;

        public CircleUsingAbstractPoint(int radius, Point centre) {
            this.radius = radius;
            this.centre = centre;
        }

    }

    static interface Point {
        int getX();
        int getY();
    }

    static final class ImmutablePoint implements Point {

        private final int y;
        private final int x;

        public ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    static final class MutablePoint implements Point {

        private int y;
        private int x;

        public MutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
