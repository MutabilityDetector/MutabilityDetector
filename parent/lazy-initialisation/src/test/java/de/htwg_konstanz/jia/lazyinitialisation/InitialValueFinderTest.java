/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.objectweb.asm.tree.FieldNode;

import de.htwg_konstanz.jia.lazyinitialisation.UnknownTypeValue.Default;
import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.*;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias.WithCustomInitialValue.IntegerValid2;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias.WithJvmInitialValue.SynchronizedObjectValid;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithCustomInitialValue.IntegerInvalid;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithCustomInitialValue.ObjectInvalidWithMultipleInitialValues;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithCustomInitialValue.ObjectInvalidWithMultipleInitialValues2;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithCustomInitialValue.StringInvalid2;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.Stateless;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.StringStaticValid;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
public final class InitialValueFinderTest {

    @ThreadSafe
    private static final class InitialValuesFactory {

        private static final InitialValuesFactory INSTANCE = new InitialValuesFactory();

        private InitialValuesFactory() {
            super();
        }

        public static InitialValuesFactory getInstance() {
            return INSTANCE;
        }

        public Set<UnknownTypeValue> getPossibleInitialValuesFor(final Class<?> targetClass, final String variableName) {
            final ConvenienceClassNode classNode = createAppropriateClassNode(targetClass);
            final VariableSetterCollection varSetters = classNode.getVariableSetterCollection();
            for (final Entry<FieldNode, Setters> entry : varSetters) {
                final FieldNode variable = entry.getKey();
                if (variable.name.equals(variableName)) {
                    final Setters setters = entry.getValue();
                    final InitialValueFinder initialValueFinder = InitialValueFinder.newInstance(variable, setters);
                    return initialValueFinder.getPossibleInitialValues();
                }
            }
            return Collections.emptySet();
        }

        private static ConvenienceClassNode createAppropriateClassNode(final Class<?> targetClass) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.getConvenienceClassNodeFor(targetClass);
        }

    } // class InitialValuesFactory


    @Test
    public void invalidFloatWithMultipleCustomInitialValues() {
        final Set<UnknownTypeValue> expected = createExpected(Float.valueOf(-1.0F), Float.valueOf(23.0F),
                Default.UNKNOWN_PRIMITIVE);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class, "hash");
        assertThat(actual, is(expected));
    }

    private static Set<UnknownTypeValue> getPossibleInitialValuesFor(final Class<?> klasse, final String variableName) {
        final InitialValuesFactory factory = InitialValuesFactory.getInstance();
        return factory.getPossibleInitialValuesFor(klasse, variableName);
    }

    private static Set<UnknownTypeValue> createExpected(final Object first, final Object ... further)  {
        final Set<UnknownTypeValue> result = new HashSet<UnknownTypeValue>();
        result.add(DefaultUnknownTypeValue.getInstance(first));
        for (final Object next : further) {
            result.add(DefaultUnknownTypeValue.getInstance(next));
        }
        return result;
    }

    @Test
    public void objectTypeWithNullAsInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected(null);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(SynchronizedObjectValid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void testForJavaLangString() {
        final Set<UnknownTypeValue> expected = createExpected(Integer.valueOf(0));
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(String.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void validIntegerWithCustomInitialValueScli() {
        final Set<UnknownTypeValue> expected = createExpected(Integer.valueOf(-1));
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithoutAlias.WithCustomInitialValue.IntegerValid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void aliasedValidIntegerWithCustomInitialValueScli() {
        final Set<UnknownTypeValue> expected = createExpected(Integer.valueOf(-2));
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithAlias.WithCustomInitialValue.IntegerValid.class, "cachedValue");
        assertThat(actual, is(expected));
    }

    @Test
    public void validStringWithJvmInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected(DefaultUnknownTypeValue.getInstanceForNull());
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithoutAlias.WithJvmInitialValue.StringValid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void validStringWithCustomInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected("");
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithoutAlias.WithCustomInitialValue.StringValid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void validCustomObjectWithNullAsInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected(DefaultUnknownTypeValue.getInstanceForNull());
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(
                WithoutAlias.WithJvmInitialValue.CustomObjectValid.class, "someObject");
        assertThat(actual, is(expected));
    }

    @Test
    public void statelessClass() {
        final Set<UnknownTypeValue> expected = Collections.emptySet();
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(Stateless.class, null);
        assertThat(actual, is(expected));
    }

    @Test
    public void staticStringWithJvmInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected(null);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(StringStaticValid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void invalidStringWithEmptyStringObjectAsInitialValue() {
        final Set<UnknownTypeValue> expected = createExpected(Default.UNKNOWN_REFERENCE);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(StringInvalid2.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void invalidIntegerWithTwoDifferntCustomInitialValues() {
        final Set<UnknownTypeValue> expected = createExpected(-2);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(IntegerInvalid.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void invalidObjectWithDifferentCustomInitialValues() {
        final Set<UnknownTypeValue> expected = createExpected(Default.UNKNOWN_REFERENCE);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(ObjectInvalidWithMultipleInitialValues.class,
                "obj");
        assertThat(actual, is(expected));
    }

    @Test
    public void invalidObject2WithDifferentCustomInitialValues() {
        final Set<UnknownTypeValue> expected = createExpected(Default.UNKNOWN_REFERENCE, Default.NULL);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(ObjectInvalidWithMultipleInitialValues2.class,
                "obj");
        assertThat(actual, is(expected));
    }

    @Test
    public void validIntegerWithInitialisingMethodAtConstruction() {
        final Set<UnknownTypeValue> expected = createExpected(Default.UNKNOWN_PRIMITIVE);
        final Set<UnknownTypeValue> actual = getPossibleInitialValuesFor(IntegerValid2.class, "cached");
        assertThat(actual, is(expected));
    }

}
