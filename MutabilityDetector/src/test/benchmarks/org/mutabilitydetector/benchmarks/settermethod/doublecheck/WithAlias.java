package org.mutabilitydetector.benchmarks.settermethod.doublecheck;

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

        public static final class MessageHolder {
            private final String message;
            private volatile int cachedValue;

            public MessageHolder(final String aMessage) {
                message = aMessage;
                cachedValue = -1;
            }

            public String getMessage() {
                return message;
            }

            /**
             * Lazy method.
             */
            public int getMessageLength() {
                int result = cachedValue;
                if (-1 == result) {
                    synchronized (this) {
                        if (-1 == result) {
                            result = calculateMessageLength();
                            cachedValue = result;
                        }
                    }
                }
                return result;
            }

            private int calculateMessageLength() {
                return message.length();
            }
        } // class MessageHolder


        public static final class MessageHolderWithWrongAssignmentGuard {
            private final String message;
            private volatile int cachedValue;

            public MessageHolderWithWrongAssignmentGuard(final String aMessage) {
                message = aMessage;
                cachedValue = -1;
            }

            public String getMessage() {
                return message;
            }

            /**
             * Lazy method.
             */
            public int getMessageLength() {
                int result = cachedValue;
                if (-1 == result) {
                    synchronized (this) {
                        if (-2 == result) {
                            result = calculateMessageLength();
                            cachedValue = result;
                        }
                    }
                }
                return result;
            }

            private int calculateMessageLength() {
                return message.length();
            }
        } // class MessageHolderWithWrongAssignmentGuard


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
