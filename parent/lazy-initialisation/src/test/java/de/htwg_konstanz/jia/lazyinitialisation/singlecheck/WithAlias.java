package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;

/**
 * Subject classes for testing. They have aliases for instance variables.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
public final class WithAlias {

    /**
     * The lazy variables have the default value which is assigned by JVM
     * appropriately to their type.
     */
    public static final class WithJvmInitialValue {

        public static final class ByteValid {
            private byte hash;
            public byte hashCodeByte() {
                byte result = hash;
                if (0 == result) {
                    result = (byte) 128;
                    hash = result;
                }
                return result;
            }
        } // class ByteValid


        public static final class FloatValid {
            private float hash;
            public float hashCodeFloat() {
                float result = hash;
                if (0.0F == result) {
                    result = 2342.0F;
                    hash = result;
                }
                return result;
            }
        } // class FloatValid


        public static final class ShortValid {
            private short hash;
            public short hashCodeShort() {
                short result = hash;
                if (0 == result) {
                    result = (short) 128;
                    hash = result;
                }
                return result;
            }
        } // class ShortValid


        public static final class StringValid {
            private String hash;
            public String hashCodeString() {
                String result = hash;
                if (null == result) {
                    result = "Hallo Welt";
                    hash = result;
                }
                return result;
            }
        } // class StringValid


        public static final class ObjectValid {
            private Object hash;
            public Object hashCodeObject() {
                Object result = hash;
                if (null == result) {
                    result = new Object();
                    hash = result;
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
