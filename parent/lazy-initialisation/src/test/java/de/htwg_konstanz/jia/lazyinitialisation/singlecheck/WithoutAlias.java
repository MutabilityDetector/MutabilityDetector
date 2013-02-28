package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;

/**
 * Subject classes for testing. They do not use aliases for instance variables
 * in lazy method.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
public final class WithoutAlias {

    /**
     * The lazy variables have the default value which is assigned by JVM
     * appropriately to their type.
     */
    public static final class WithJvmInitialValue {
        
        public static final class CharValid {
            private char hash;
            public char hashCodeChar() {
                if (' ' == hash) {
                    hash = 'a';
                }
                return hash;
            }
        } // class CharValid


        public static final class FloatValid {
            private float hash;
            public float hashCodeFloat() {
                if (0.0F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
        } // class FloatValid


        public static final class IntegerValid {
            private int hash;
            @Override
            public int hashCode() {
                if (0 == hash) {
                    hash = 2342;
                }
                return hash;
            }
        } // class IntegerWithDefault


        public static final class ObjectValid {
            private Object hash = null;
            public Object hashCodeObject() {
                if (null == hash) {
                    hash = new Object();
                }
                return hash;
            }
        } // class ObjectValid


        public static final class SynchronizedObjectValid {
            private Object complexObject;
            public synchronized Object getComplexObject() {
                if (null == complexObject) {
                    complexObject = new Object();
                }
                return complexObject;
            }
        } // class SynchronizedObjectValid


        public static final class StringValid {
            private String hash;
            public String hashCodeString() {
                if (null == hash) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringValid


        public static final class CustomObjectValid {
            private static final class SomeObject {
                private SomeObject() {
                    super();
                }
                public static SomeObject getInstance() {
                    return new SomeObject();
                }
            }
            private SomeObject someObject;
            public SomeObject hashCodeSomeObject() {
                if (null == someObject) {
                    someObject = SomeObject.getInstance();;
                }
                return someObject;
            }
        } // class CustomObjectValid


    } // class WithJvmInitialValue


    /**
     * After object construction a custom value will have been assigned to the
     * lazy variables.
     */
    public static final class WithCustomInitialValue {

        public static final class IntegerValid {
            private int hash = -1;
            @Override
            public int hashCode() {
                if (-1 == hash) {
                    hash = 2342;
                }
                return hash;
            }
        } // class IntegerValid


        public static final class FloatValid {
            private float hash = -1.0F;
            public float hashCodeFloat() {
                if (0.0F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
        } // class FloatValid


        public static final class FloatInvalidWithMultipleInitialValues {
            private float hash = 0.0F;
            public FloatInvalidWithMultipleInitialValues() {
                hash = -1.0F;
            }
            public FloatInvalidWithMultipleInitialValues(final float initial) {
                hash = initial;
            }
            public FloatInvalidWithMultipleInitialValues(final String foo) {
                hash = 23.0F;
            }
            public float hashCodeFloat() {
                if (0.0F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
        } // class FloatValidWithMultipleInitialValues


        public static final class StringValid {
            private String hash = "";
            public String hashCodeString() {
                if ("" == hash) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringValid

        
    } // class WithCustomInitialValue


}
