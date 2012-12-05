package org.mutabilitydetector.benchmarks.circular;

public final class MultipleCircularAssignments {

    public final static class A {
        public final B b;
        public final B secondB;

        public A(B b) {
            this.b = b;
            this.secondB = b;
        }
    }

    public final static class B {
        public final A a;
        public final A secondA;

        public B(A a) {
            this.a = a;
            this.secondA = a;
        }
    }
}
