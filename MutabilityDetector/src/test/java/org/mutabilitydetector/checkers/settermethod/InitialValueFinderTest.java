/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.*;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias.WithCustomInitialValue.IntegerValid2;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias.WithJvmInitialValue.SynchronizedObjectValid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.IntegerInvalid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.ObjectInvalidWithMultipleInitialValues;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.ObjectInvalidWithMultipleInitialValues2;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.StringInvalid2;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.Stateless;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.StringStaticValid;
import org.mutabilitydetector.checkers.settermethod.UnknownTypeValue.Default;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Entry;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Initialisers;
import org.objectweb.asm.tree.FieldNode;


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
            final EnhancedClassNode classNode = createAppropriateClassNode(targetClass);
            final VariableInitialisersMappingFactory factory = VariableInitialisersMappingFactory.getInstance();
            CandidatesInitialisersMapping m = factory.getVariableInitialisersAssociationFor(classNode);
            for (final Entry entry : m) {
                final FieldNode candidate = entry.getCandidate();
                if (candidate.name.equals(variableName)) {
                    final Initialisers setters = entry.getInitialisers();
                    final InitialValueFinder f = InitialValueFinder.newInstance(candidate, setters, classNode);
                    return f.find();
                }
            }
            return Collections.emptySet();
        }

        private static EnhancedClassNode createAppropriateClassNode(final Class<?> targetClass) {
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

    // FIXME: Das tatsaechliche Ergebnis ist UNKNOWN_PRIMITIVE
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
                WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.class, "someObject");
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
