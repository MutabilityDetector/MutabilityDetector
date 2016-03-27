package org.mutabilitydetector.checkers.settermethod;

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



import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
final class DefaultUnknownTypeValue implements UnknownTypeValue {

    private final Object value;

    private DefaultUnknownTypeValue(final Object theBaseValue) {
        value = theBaseValue;
    }

    public static UnknownTypeValue getInstance(final Object baseValue) {
        final UnknownTypeValue result;
        if (baseValue instanceof DefaultUnknownTypeValue) {
            result = (DefaultUnknownTypeValue) baseValue;
        } else if (null == baseValue) {
            result = getInstanceForNull();
        } else {
            result = new DefaultUnknownTypeValue(baseValue);
        }
        return result;
    }

    public static UnknownTypeValue getInstanceForNull() {
        return new DefaultUnknownTypeValue(Default.NULL);
    }
    
    public static UnknownTypeValue getInstanceForUnknownPrimitive() {
        return getInstance(Default.UNKNOWN_PRIMITIVE);
    }

    public static UnknownTypeValue getInstanceForUnknownReference() {
        return getInstance(Default.UNKNOWN_REFERENCE);
    }

    @Override
    public boolean asBoolean() {
        final Boolean result = tryToCast(Boolean.class);
        return result.booleanValue();
    }

    private <T> T tryToCast(final Class<T> targetClass) {
        try {
            return cast(targetClass);
        } catch (final ClassCastException e) {
            final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
            System.err.println(msg);
        }
        return null;
    }

    private <T> T cast(final Class<T> targetClass) {
        final T result = targetClass.cast(value);
        if (null == result) {
            final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
            throw new NullPointerException(msg);
        }
        return result;
    }

    @Override
    public char asChar() {
        final Character result = tryToCast(Character.class);
        return result.charValue();
    }

    @Override
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    @Override
    public boolean isByte() {
        return value instanceof Byte;
    }

    @Override
    public boolean isChar() {
        return value instanceof Character;
    }

    @Override
    public boolean isShort() {
        return value instanceof Short;
    }

    @Override
    public boolean isInt() {
        return value instanceof Integer;
    }

    @Override
    public boolean isLong() {
        return value instanceof Long;
    }

    @Override
    public boolean isFloat() {
        return value instanceof Float;
    }

    @Override
    public boolean isDouble() {
        return value instanceof Double;
    }

    @Override
    public boolean isString() {
        return value instanceof String;
    }

    @Override
    public boolean isObject() {
        final boolean result;
        if (isBoolean() || isChar() || isString()) {
            result = false;
        } else {
            result = (null == tryToCastToNumber());
        }
        return result;
    }

    private Number tryToCastToNumber() {
        return tryToCast(Number.class);
    }

    @Override
    public boolean isZero() {
        final boolean result;
        if (isObject() || isString()) {
            result = false;
        } else if (isBoolean()) {
            result = asBoolean() ? false : true;
        } else if (isChar()) {
            result = Character.MIN_VALUE == asChar();
        } else {
            final Number valueAsNumber = tryToCastToNumber();
            final int valueAsInt = valueAsNumber.intValue();
            result = (0 == valueAsInt);
        }
        return result;
    }

    @Override
    public boolean isNotZero() {
        return !isZero();
    }

    @Override
    public boolean isNull() {
        return Default.NULL == value;
    }

    @Override
    public boolean isNotNull() {
        return !isNull();
    }

    @Override
    public boolean isUnknownPrimitive() {
        return Default.UNKNOWN_PRIMITIVE == value;
    }

    @Override
    public boolean isUnknownReference() {
        return Default.UNKNOWN_REFERENCE == value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultUnknownTypeValue)) {
            return false;
        }
        final DefaultUnknownTypeValue other = (DefaultUnknownTypeValue) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [").append("value=(").append(getValueTypeAsString());
        b.append(") ").append(value).append(']');
        return b.toString();
    }

    private String getValueTypeAsString() {
        final String result;
        if (isBoolean()) {
            result = "boolean";
        } else if (isChar()) {
            result = "char";
        } else if (isShort()) {
            result = "short";
        } else if (isInt()) {
            result = "int";
        } else if (isLong()) {
            result = "long";
        } else if (isFloat()) {
            result = "float";
        } else if (isDouble()) {
            result = "double";
        } else if (isString()) {
            result = "String";
        } else {
            result = "unknown/Object";
        }
        return result;
    }

}
