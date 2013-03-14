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

        public static final class Stateless {
            public static int sum(final int firstAddend, final int secondAddend) {
                return firstAddend + secondAddend;
            }
        } // class Stateless


        public static final class CharValid {
            private char hash;
            public char hashCodeChar() {
                if (0 == hash) {
                    hash = 'a';
                }
                return hash;
            }
        } // class CharValid


        public static final class CharInvalid {
            private char hash;
            public char hashCodeChar() {
                if ("".equals(hash)) {
                    hash = 'a';
                }
                return hash;
            }
        } // class CharInvalid


        public static final class CharInvalid2 {
            @SuppressWarnings("unused")
            private boolean initialised;
            private char hash;
            public CharInvalid2() {
                initialised = false;
            }
            public char hashCodeChar() {
                if ("".equals(hash)) {
                    hash = 'a';
                }
                return hash;
            }
        } // class CharInvalid


        public static final class FloatValid {
            private float hash;
            public float hashCodeFloat() {
                if (0.0F == hash) {
                    hash = 2342.0F / 23;
                }
                return hash;
            }
        } // class FloatValid


        public static final class FloatInvalid {
            private float hash;
            public float hashCodeFloat() {
                if (0.0001F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
        } // class FloatInvalid


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


        public static final class IntegerInvalid {
            private int hash;
            @Override
            public int hashCode() {
                if (-1 == hash) {
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


        public static final class ObjectInvalid {
            private final Object comparativeObject = new Object();
            private Object hash = null;
            public Object hashCodeObject() {
                if (comparativeObject.equals(hash)) {
                    hash = new Object();
                }
                return hash;
            }
        } // class ObjectInvalid


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


        public static final class StringStaticValid {
            private static String hash;
            public String hashCodeString() {
                if (null == hash) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringStaticValid


        public static final class StringInvalid {
            private String hash;
            public String hashCodeString() {
                hash = "Hash code";
                return hash;
            }
        } // class StringInvalid


        public static final class CustomObjectInvalid {
            public static final class SomeObject {
                private SomeObject() {
                    super();
                }
                public static SomeObject getInstance() {
                    return new SomeObject();
                }
            }
            private SomeObject someObject;
            public SomeObject hashCodeSomeObject() {
                if (someObject == null) {
                    someObject = SomeObject.getInstance();;
                }
                return someObject;
            }
        } // class CustomObjectInvalid


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


        public static final class IntegerInvalid {
            private int hash = -1;
            public IntegerInvalid() {
                hash = -2;
            }
            @Override
            public int hashCode() {
                if (0 == hash) {
                    hash = 2342;
                }
                return hash;
            }
        } // class IntegerInvalid


        public static final class FloatValid {
            private float hash = -1.0F;
            public float hashCodeFloat() {
                if (-1.0F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
        } // class FloatValid


        public static final class FloatInvalid {
            private float hash = -1.0F;
            public float hashCodeFloat() {
                if (-1.0F == hash) {
                    hash = 2342.0F;
                }
                return hash;
            }
            public void setHash(final float newValue) {
                hash = newValue;
            }
        } // class FloatInvalid


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


        public static final class ObjectInvalidWithMultipleInitialValues {
            private static Object obj;
            public ObjectInvalidWithMultipleInitialValues() {
                obj = new Object();
            }
            public ObjectInvalidWithMultipleInitialValues(final int foo) {
                obj = Integer.valueOf(foo);
            }
            public ObjectInvalidWithMultipleInitialValues(final String bar) {
                obj = new String(bar);
            }
            public static Object getObject() {
                if (null == obj) {
                    obj = new Object();
                }
                return obj;
            }
        } // class ObjectInvalidWithMultipleInitialValues


        public static final class ObjectInvalidWithMultipleInitialValues2 {
            private static Object obj;
            public ObjectInvalidWithMultipleInitialValues2() {
                obj = null;
            }
            public ObjectInvalidWithMultipleInitialValues2(final String bar) {
                obj = new String(bar);
            }
            public static Object getObject() {
                if (null == obj) {
                    obj = new Object();
                }
                return obj;
            }
        } // class ObjectInvalidWithMultipleInitialValues2


        public static final class StringValid {
            private String hash = "";
            public String hashCodeString() {
                if ("".equals(hash)) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringValid


        public static final class StringValid2 {
            {
                hash = "";
            }
            private String hash;
            public String hashCodeString() {
                if ("".equals(hash)) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringValid2


        public static final class StringInvalid {
            private String hash = "abc";
            public String hashCodeString() {
                if ("".equals(hash) && 3 == hash.length()) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringInvalid


        public static final class StringInvalid2 {
            private String hash = new String();
            public String hashCodeString() {
                if (null == hash) {
                    hash = "Hash code";
                }
                return hash;
            }
        } // class StringInvalid

        
    } // class WithCustomInitialValue


}
