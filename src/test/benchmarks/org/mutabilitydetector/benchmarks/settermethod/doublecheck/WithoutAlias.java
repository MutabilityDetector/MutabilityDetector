package org.mutabilitydetector.benchmarks.settermethod.doublecheck;

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

/**
 * Subject classes for testing. They do not use aliases for instance variables
 * in lazy method.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 28.02.2013
 */
public final class WithoutAlias {

    /**
     * The lazy variables have the default value which is assigned by JVM
     * appropriately to their type.
     */
    public static final class WithJvmInitialValue {

        public static final class ObjectValid {
            private volatile Object complexObject;
            public Object getComplexObject() {
                if (null == complexObject) {
                    synchronized (this) {
                        if (null == complexObject) {
                            complexObject = new Object();
                        }
                    }
                }
                return complexObject;
            }
        } // class ObjectValid

    } // class WithJvmInitialValue


    /**
     * After object construction a custom value will have been assigned to the
     * lazy variables.
     */
    public static final class WithCustomInitialValue {

        
    } // class WithCustomInitialValue


}
