package de.htwg_konstanz.jia.lazyinitialisation;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NotThreadSafe
final class UnknownTypeValueDefault implements UnknownTypeValue {

    private final Object value;

    private UnknownTypeValueDefault(final Object theBaseValue) {
        value = theBaseValue;
    }

    public static UnknownTypeValue getInstance(final Object baseValue) {
        if (baseValue instanceof UnknownTypeValueDefault) {
            return (UnknownTypeValueDefault) baseValue;
        }
        return new UnknownTypeValueDefault(baseValue);
    }

    public static UnknownTypeValue getInstanceForNull() {
        return new UnknownTypeValueDefault(Null.INSTANCE);
    }

    @Override
    public boolean asBoolean() {
        final Boolean result = tryToCast(value, Boolean.class);
        return result.booleanValue();
    }

    private static <T> T tryToCast(final Object value, final Class<T> targetClass) {
        try {
            return cast(value, targetClass);
        } catch (final ClassCastException e) {
            final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
            final Logger logger = LoggerFactory.getLogger(UnknownTypeValueDefault.class);
            logger.error(msg, e);
        }
        return null;
    }

    private static <T> T cast(final Object value, final Class<T> targetClass) {
        final T result = targetClass.cast(value);
        if (null == result) {
            final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
            throw new NullPointerException(msg);
        }
        return result;
    }

    @Override
    public byte asByte() {
        return tryToCastToNumber().byteValue();
    }

    private Number tryToCastToNumber() {
        return tryToCast(value, Number.class);
    }

    @Override
    public char asChar() {
        final Character result = tryToCast(value, Character.class);
        return result.charValue();
    }

    @Override
    public short asShort() {
        return tryToCastToNumber().shortValue();
    }

    @Override
    public int asInt() {
        return tryToCastToNumber().intValue();
    }

    @Override
    public long asLong() {
        return tryToCastToNumber().longValue();
    }

    @Override
    public float asFloat() {
        return tryToCastToNumber().floatValue();
    }

    @Override
    public double asDouble() {
        return tryToCastToNumber().doubleValue();
    }

    @Override
    public String asString() {
        final String result = tryToCast(value, String.class);
        return result;
    }

    @Override
    public Object asObject() {
        return value;
    }

    @Override
    public <T> T asType(final Class<T> targetTypeClass) {
        final T result = tryToCast(value, targetTypeClass);
        return result;
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
        return true;
    }

    @Override
    public boolean isNull() {
        return Null.INSTANCE == value;
    }

    @Override
    public boolean isNotNull() {
        return !isNull();
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
        if (!(obj instanceof UnknownTypeValueDefault)) {
            return false;
        }
        final UnknownTypeValueDefault other = (UnknownTypeValueDefault) obj;
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
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append(value);
        return builder.toString();
    }

}
