package org.mutabilitydetector.benchmarks.circular;



@SuppressWarnings("unused")
public final class OuterClassWithInnerClassAsField {

    private final Inner inner = new Inner();
    private OuterClassWithInnerClassAsField referenceToSelfType;
    private final SomeOtherClass someOtherClass = new SomeOtherClass();
    
    private void setSelfTypeField(OuterClassWithInnerClassAsField other) {
        this.referenceToSelfType = other;
    }
    
    
    public final class Inner {
        
        private OuterClassWithInnerClassAsField owner;

        public void setOwner(OuterClassWithInnerClassAsField owner) {
            this.owner = owner;
        }
    }
    
    public static final class SomeOtherClass {
        Inner inner;
        SomeOtherClass self;

        public void setInner(Inner inner) {
            this.inner = inner;
        }
        
        public void setSelf(SomeOtherClass self) {
            this.self = self;
        }
    }
    
    
}
