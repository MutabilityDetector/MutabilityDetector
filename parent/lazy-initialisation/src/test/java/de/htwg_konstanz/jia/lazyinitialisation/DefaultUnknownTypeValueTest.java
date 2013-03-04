/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 04.03.2013
 */
public final class DefaultUnknownTypeValueTest {

    private UnknownTypeValue utv = null;

    @After
    public void tearDown() {
        utv = null;
    }

    @Test
    public void intIsNotObject() {
        initialiseUtvWithInteger();
        assertFalse(utv.isObject());
    }

    private void initialiseUtvWithInteger() {
        initialiseUtvWith(Integer.valueOf(23));
    }

    private void initialiseUtvWith(final Object value) {
        utv = DefaultUnknownTypeValue.getInstance(value);
    }

    @Test
    public void zeroIntIsZero() {
        initialiseUtvWith(Integer.valueOf(0));
        assertTrue(utv.isZero());
    }

    @Test
    public void nonzeroIntIsNotZero() {
        initialiseUtvWithInteger();
        assertFalse(utv.isZero());
    }

    @Test
    public void intIsNotNull() {
        initialiseUtvWithInteger();
        assertFalse(utv.isNull());
        assertTrue(utv.isNotNull());
    }

    @Test
    public void getIntAsDouble() {
        initialiseUtvWithInteger();
        assertThat(utv.asDouble(), is(Double.valueOf(23.0D)));
    }
    
    @Test
    public void nullIsNullObject() {
        initialiseUtvWithNull();
        assertTrue(utv.isNull());
    }
    
    private void initialiseUtvWithNull() {
        initialiseUtvWith(null);
    }

    @Test
    public void nullObjectIsObject() {
        initialiseUtvWithNullObject();
        assertTrue(utv.isNull());
    }

    private void initialiseUtvWithNullObject() {
        utv = DefaultUnknownTypeValue.getInstanceForNull();
    }

    @Test
    public void nullObjectIsNotZero() {
        initialiseUtvWithNullObject();
        assertFalse(utv.isZero());
        assertTrue(utv.isNotZero());
    }

    @Test(expected = NullPointerException.class)
    public void getNullObjectAsFloat() {
        initialiseUtvWithNullObject();
        utv.asFloat();
    }

    @Test
    public void doubleIsNotObject() {
        initialiseUtvWithDouble();
        assertFalse(utv.isObject());
    }

    private void initialiseUtvWithDouble() {
        initialiseUtvWith(Double.valueOf(42.23D));
    }

    @Test
    public void doubleIsDouble() {
        initialiseUtvWithDouble();
        assertTrue(utv.isDouble());
    }

    @Test
    public void doubleIsNotFloat() {
        initialiseUtvWithDouble();
        assertFalse(utv.isFloat());
    }

    @Test
    public void doubleIsNotZero() {
        initialiseUtvWithDouble();
        assertFalse(utv.isZero());
        assertTrue(utv.isNotZero());
    }

    @Test
    public void getDoubleAsInt() {
        initialiseUtvWithDouble();
        assertThat(utv.asInt(), is(Integer.valueOf(42)));
    }

    @Test
    public void stringIsString() {
        initialiseUtvWithString();
        assertTrue(utv.isString());
    }

    private void initialiseUtvWithString() {
        initialiseUtvWith("The quick brown fox jumps over the lazy dog");
    }

    @Test
    public void stringIsNotShort() {
        initialiseUtvWithString();
        assertFalse(utv.isShort());
    }

    @Test
    public void stringIsNotObject() {
        initialiseUtvWithString();
        assertFalse(utv.isObject());
    }

    @Test
    public void stringIsNotNull() {
        initialiseUtvWithString();
        assertTrue(utv.isNotNull());
    }

    @Test
    public void characterIsNotZero() {
        initialiseUtvWithCharacter();
        assertFalse(utv.isZero());
    }

    private void initialiseUtvWithCharacter() {
        initialiseUtvWith(Character.valueOf('w'));
    }

    @Test
    public void characterIsZero() {
        initialiseUtvWith(Character.valueOf(Character.MIN_VALUE));
        assertTrue(utv.isZero());
    }

}
