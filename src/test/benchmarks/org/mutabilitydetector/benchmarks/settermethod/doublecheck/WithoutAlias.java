package org.mutabilitydetector.benchmarks.settermethod.doublecheck;

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
