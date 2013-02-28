package de.htwg_konstanz.jia.lazyinitialisation.doublecheck;

/**
 * Subject classes for testing. They have aliases for instance variables.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 28.02.2013
 */
public final class WithAlias {

    /**
     * The lazy variables have the default value which is assigned by JVM
     * appropriately to their type.
     */
    public static final class WithJvmInitialValue {

        public static final class IntegerValid {
            private volatile int someNumber;
            public int getSomeNumber() {
                int result = someNumber;
                if (0 == result) {
                    synchronized (this) {
                        result = someNumber;
                        if (0 == result) {
                            result = 42;
                            someNumber = result;
                        }
                    }
                }
                return result;
            }
        } // class IntegerValid

        
        public static final class ObjectValid {
            private volatile Object complexObject;
            public Object getComplexObject() {
                Object result = complexObject;
                if (null == result) {
                    synchronized (this) {
                        result = complexObject;
                        if (null == result) {
                            result = new Object();
                            complexObject = result;
                        }
                    }
                }
                return result;
            }
        } // class ObjectValid


    } // class WithJvmInitialValue


    /**
     * After object construction a custom value will have been assigned to the
     * lazy variables.
     */
    public static final class WithCustomInitialValue {

        public static final class IntegerValid {
            private final String message;
            private int cachedValue;
            public IntegerValid(final String aMessage) {
                message = aMessage;
                cachedValue = -2;
            }
            public String getMessage() {
                return message;
            }
            public int getMessageLength() {
                int result = cachedValue;
                if (-2 == result) {
                    result = message.length();
                    cachedValue = result;
                }
                return result;
            }
        } // class IntegerValid


        public static final class StringValid {
            private String hash = "";
            public String hashCodeString() {
                String result = hash;
                if ("" == result) {
                    result = "Hallo Welt";
                    hash = result;
                }
                return result;
            }
        } // class StringValid


    } // class WithCustomInitialValue


} // class WithAlias
