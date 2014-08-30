package org.mutabilitydetector.benchmarks.settermethod.singlecheck;

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


        public static final class ByteStaticValid {
            private static byte hash;
            public static byte hashCodeByte() {
                byte result = hash;
                if (0 == result) {
                    result = (byte) 128;
                    hash = result;
                }
                return result;
            }
        } // class ByteStaticValid


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


        public static final class SynchronizedObjectValid {
            private Object hash;
            public Object hashCodeObject() {
                Object result = hash;
                if (null == result) {
                    result = new Object();
                    hash = result;
                }
                return result;
            }
        } // class SynchronizedObjectValid

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
                    result = calculateMessageLength();
                    cachedValue = result;
                }
                return result;
            }
            int calculateMessageLength() {
                return message.length();
            }
        } // class IntegerValid

        
        public static final class IntegerValid2 {
            private int cached;
            public IntegerValid2() {
                cached = getIinitialiseCached();
            }
            private int getIinitialiseCached() {
                return -1;
            }
            public int getCached() {
                int result = cached;
                if (0 == result) {
                    result = 12;
                    cached = result;
                }
                return result;
            }
        } // class IntegerValid


        public static final class StringValid {
            private String hash = "";
            public String hashCodeString() {
                String result = hash;
                if ("" == result) {
                    result = getValue();
                    hash = result;
                }
                return result;
            }
            private String getValue() {
                return "Hallo Welt.";
            }
        } // class StringValid


    } // class WithCustomInitialValue


} // class WithAlias
