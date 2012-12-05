package org.mutabilitydetector.benchmarks.circular;

@SuppressWarnings("unused")
public final class SeveralHopsCircularDependency {

    private final A a = new A();
    
    public final static class A {
        private final B b = new B();
    }
    public final static class B {
        private final C c = new C();
    }
    public final static class C {
        private final D d = new D();
    }
    public final static class D {
        private final E e = new E();
    }
    public final static class E {
        private final A a = new A();
        private final B b = new B();
        private final C c = new C();
    }
    
    
}
