package org.mutabilitydetector.benchmarks.cyclic;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



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
