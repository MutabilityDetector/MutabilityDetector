package org.mutabilitydetector.benchmarks.settermethod;

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

@SuppressWarnings("unused")
public class SetsFieldsOfDifferentTypes {

    public static class SetsReference {
        private Object field;

        public void setField(Object field) {
            this.field = field;
        }
    }

    public static class SetsInt {
        private int field;

        public void setField(int field) {
            this.field = field;
        }
    }

    public static class SetsDouble {
        private double field;

        public void setField(double field) {
            this.field = field;
        }
    }

    public static class SetsFloat {
        private float field;

        public void setField(float field) {
            this.field = field;
        }
    }

    public static class SetsShort {
        private short field;

        public void setField(short field) {
            this.field = field;
        }
    }

    public static class SetsByte {
        private byte field;

        public void setField(byte field) {
            this.field = field;
        }
    }

    public static class SetsChar {
        private char field;

        public void setField(char field) {
            this.field = field;
        }
    }

    public static class SetsBoolean {
        private boolean field;

        public void setField(boolean parameter) {
            this.field = parameter;
        }
    }

    public static class SetsLong {
        private long field;

        public void setField(long field) {
            this.field = field;
        };
    }

    public static class SetsObjectArray {
        private Object[] field;

        public void setField(Object[] field) {
            this.field = field;
        };
    }

    public static class SetsObjectArrayArray {
        private Object[][] field;

        public void setField(Object[][] field) {
            this.field = field;
        };
    }
}
