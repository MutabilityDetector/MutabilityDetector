package org.mutabilitydetector.checkers.settermethod;

import static org.mutabilitydetector.checkers.settermethod.InitialisersFinderTest.Reason.variable;
import static org.mutabilitydetector.checkers.settermethod.InitialisersFinderTest.SettersSizeMatcher.hasNumberOfSetters;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias.WithCustomInitialValue.IntegerValid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias.WithJvmInitialValue.ByteStaticValid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias.WithJvmInitialValue.ShortValid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.FloatInvalid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithCustomInitialValue.StringValid2;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.CustomObjectInvalid;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.Stateless;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias.WithJvmInitialValue.StringStaticValid;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 05.03.2013
 */
public final class InitialisersFinderTest {

    @NotThreadSafe
    static final class Reason {

        private final String variableName;
        private Class<?> klasse;
        
        private Reason(final String theVariableName) {
            variableName = theVariableName;
            klasse = null;
        }

        public static Reason variable(final String variableName) {
            return new Reason(variableName);
        }

        public Reason ofClass(final Class<?> dieKlasse) {
            klasse = dieKlasse;
            return this;
        }

        public CandidatesInitialisersMapping getVariableInitialisersMapping() {
            final EnhancedClassNode classNode = createConvenienceClassNodeFor(klasse);
            final VariableInitialisersMappingFactory factory = VariableInitialisersMappingFactory.getInstance();
            return factory.getVariableInitialisersAssociationFor(classNode);
        }

        private static EnhancedClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.getConvenienceClassNodeFor(klasse);
        }

        public String getVariableName() {
            return variableName;
        }

    } // class Reason
    

    @ThreadSafe
    public static final class SettersSizeMatcher extends TypeSafeMatcher<Reason> {

        private final int expectedNumber;
        private volatile int actualNumber;

        private SettersSizeMatcher(final int theExpectedNumber) {
            expectedNumber = theExpectedNumber;
        }

        public static SettersSizeMatcher hasNumberOfSetters(final int expectedNumber) {
            return new SettersSizeMatcher(expectedNumber);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValue(expectedNumber);            
            description.appendText(" setter methods for variable.");
        }

        @Override
        protected void describeMismatchSafely(final Reason item, final Description mismatchDescription) {
            mismatchDescription.appendText("got actually ");
            mismatchDescription.appendValue(actualNumber);
        }

        @Override
        protected boolean matchesSafely(final Reason reason) {
            final CandidatesInitialisersMapping m = reason.getVariableInitialisersMapping();
            final Collection<MethodNode> setterMethods = m.getInitialisingMethodsFor(reason.getVariableName());
            actualNumber = setterMethods.size();
            return expectedNumber == actualNumber;
        }

    } // class CandidatesSizeMatcher


    @Test
    public void exceptionIfFirstArgumentIsNull() {
        final String expMsg = "Argument 'methodsOfAnalysedClass' must not be null!";
        try {
            InitialisersFinder.newInstance(null, CandidatesInitialisersMapping.newInstance());
            fail(format("Expected NullPointerException with message '%s'.", expMsg));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(expMsg)));
        }
    }

    @Test
    public void exceptionIfSecondArgumentIsNull() {
        final String expMsg = "Argument 'variableInitialiserMapping' must not be null!";
        try {
            InitialisersFinder.newInstance(Collections.<MethodNode> emptyList(), null);
            fail(format("Expected NullPointerException with message '%s'.", expMsg));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(expMsg)));
        }
    }

    @Test
    public void stringWithInitialiserBlockHasOneSetter() {
        assertThat(variable("hash").ofClass(StringValid2.class), hasNumberOfSetters(1));
    }

    @Test
    public void stringWithStaticVariableHasOneSetter() {
        assertThat(variable("hash").ofClass(StringStaticValid.class), hasNumberOfSetters(1));
    }

    @Test
    public void classWithAdditionalSetter() {
        assertThat(variable("hash").ofClass(FloatInvalid.class), hasNumberOfSetters(2));
    }

    @Test
    public void variableWithCustomTypeHasOneSetter() {
        assertThat(variable("someObject").ofClass(CustomObjectInvalid.class), hasNumberOfSetters(1));
    }

    @Test
    public void classWitzLazyMethodWithAliasHasOneSetter() {
        assertThat(variable("hash").ofClass(ShortValid.class), hasNumberOfSetters(1));
    }

    @Test
    public void integerValidWithTwoVariablesHasOneSetterMethodForLazyVariable() {
        assertThat(variable("cachedValue").ofClass(IntegerValid.class), hasNumberOfSetters(1));
    }

    @Test
    public void classWithStaticLazyMethodHasOneSetter() {
        assertThat(variable("hash").ofClass(ByteStaticValid.class), hasNumberOfSetters(1));
    }

    @Test
    public void statelessClassHasNoSetters() {
        assertThat(variable("foo").ofClass(Stateless.class), hasNumberOfSetters(0));
    }

}
