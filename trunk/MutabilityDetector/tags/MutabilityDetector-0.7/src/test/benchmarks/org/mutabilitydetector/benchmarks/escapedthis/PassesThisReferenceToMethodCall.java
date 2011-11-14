/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.benchmarks.escapedthis;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PassesThisReferenceToMethodCall {

    public static final class AsSingleParameter {
        public AsSingleParameter() {
            new GiveMeYourThisReference(this);
        }
    }

    public static final class AsOneOfSeveralParameters {
        public AsOneOfSeveralParameters() {
            new GiveMeYourThisReference(null, "hi there", 1, this, new HashMap<String, String>(), 1.0d);
        }
    }
    
    public static final class AsLastOfSeveralParameters {
        public AsLastOfSeveralParameters() {
            new GiveMeYourThisReference(null, "hi there", 1, 42.3d, new HashMap<String, String>(), this);
        }
    }
    
    public static final class AsFirstOfSeveralParameters {
        public AsFirstOfSeveralParameters() {
            new GiveMeYourThisReference(this, "hi there", 1, 42.3d, new HashMap<String, String>(), null);
        }
    }
    
    public static final class AsMoreThanOneOfSeveralParameters {
        public AsMoreThanOneOfSeveralParameters() {
            new GiveMeYourThisReference(this, "hi there", 1, 42.3d, new HashMap<String, String>(), this);
        }
    }

    public static final class AsOneOfSeveralParametersWithOtherWeirdCode {
        public static final Object staticField = null;
        
        public AsOneOfSeveralParametersWithOtherWeirdCode(boolean param) {
            new GiveMeYourThisReference(staticField, 
                    param ? "hi there" : "bye there", 
                    getLong(), this, newMap(), 1.0d);
        }
        
        private long getLong() {
            return 1;
        }
        
        public static Map<String, String> newMap() {
            return new HashMap<String, String>();
        }
    }

    public static final class InOneConstructorButNotTheOther {
        public static final Object staticField = null;
        
        public InOneConstructorButNotTheOther(boolean param) {
            new GiveMeYourThisReference(staticField, 
                    param ? "hi there" : "bye there", 
                    Long.valueOf("42"), this, newMap(), 1.0d);
        }
        
        public InOneConstructorButNotTheOther() {
            // This constructor is good. But the class is still mutable.
        }
        
        public static Map<String, String> newMap() {
            return new HashMap<String, String>();
        }
    }

    public static final class AsParameterToPrivateMethod {
        public AsParameterToPrivateMethod() {
            dontPublishThisReference(this);
        }
    
        private static void dontPublishThisReference(Object thisReference) {
            String toString = thisReference.toString();
            System.out.println(toString);
        }
    }

    public static class AsParameterToStaticMethod {
        public AsParameterToStaticMethod() {
            GiveMeYourThisReference.staticMethod(this);
        }
    }

}
