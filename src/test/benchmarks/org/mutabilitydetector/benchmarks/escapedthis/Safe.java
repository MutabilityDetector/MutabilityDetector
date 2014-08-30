package org.mutabilitydetector.benchmarks.escapedthis;

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

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Safe {

    public static final class SaveThisReferenceToPrivateInstanceField {
        private final SaveThisReferenceToPrivateInstanceField thisReference;

        public SaveThisReferenceToPrivateInstanceField() {
            thisReference = this;
        }
    }
    
    public static final class NewsUpObjectToAssignToField {
        private final Object thisReference;
        private final int x;

        public NewsUpObjectToAssignToField(int x) {
            this.x = x;
            thisReference = new Object();
        }
    }
    
    public static class Super { }
    
    public static final class ImplicitCallToSuper extends Super {
        public ImplicitCallToSuper() { }
    }

    public static final class ExplicitCallToSuper extends Super {
        public ExplicitCallToSuper() {
            super();
        }
    }
    
    public static final class CallToOtherConstructor {
        private final int x;
        public CallToOtherConstructor() {
            this(42);
        }
        
        public CallToOtherConstructor(int x) {
            this.x = x;
        }
    }
    
    public static final class PassesInitialisedFieldToOtherMethod {
        private final Object field;
        public PassesInitialisedFieldToOtherMethod() {
            field = new Object();
            new GiveMeYourThisReference(this.field);
        }
    }
    
    public static final class IsMutableForReassigningFieldNotForThisEscaping {
        private int intField;

        public IsMutableForReassigningFieldNotForThisEscaping(int param) {
            this.intField = param;
        }
        
        public void setIntField(int intField) {
            this.intField = intField;
        }
    }

    public static final class PassThisReferenceToClassWhichDoesNotPublishIt {
        public PassThisReferenceToClassWhichDoesNotPublishIt() {
            new JustAssignTheReference(this);
        }
    }

    private static final class JustAssignTheReference {
        private final Object reference;

        public JustAssignTheReference(Object reference) {
            this.reference = reference;
        }
    }
    
    public static final class PassesThisReferenceAfterConstruction {
        private final int passed = 1;
        
        public void nowYouCanEscape() {
            new GiveMeYourThisReference(this);
        }
    }
    
    @SuppressWarnings("cast")
    public static final class NoThisPassedToOtherObjectAsOneOfManyParametersAndDoesWeirdStuffInNewCall {
        public static final Object staticField = null;
        
        public NoThisPassedToOtherObjectAsOneOfManyParametersAndDoesWeirdStuffInNewCall(boolean param) {
            new GiveMeYourThisReference(staticField, 
                    param ? "hi there" : "bye there", 
                    getLong(), null, newMap(), (double)1.0d);
        }
        
        private long getLong() {
            return 1;
        }
        
        public static Map<String, String> newMap() {
            return new HashMap<String, String>();
        }
    }
    
}
