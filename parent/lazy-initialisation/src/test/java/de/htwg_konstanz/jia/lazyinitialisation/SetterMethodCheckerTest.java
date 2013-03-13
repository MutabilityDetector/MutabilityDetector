package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.SetterMethodCheckerTest.IsImmutableMatcher.isImmutable;
import static de.htwg_konstanz.jia.lazyinitialisation.SetterMethodCheckerTest.IsMutableMatcher.isMutable;
import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.io.IOException;
import java.util.*;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;


import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mutabilitydetector.*;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.*;
import org.mutabilitydetector.checkers.AsmMutabilityChecker.CheckerResult;
import org.mutabilitydetector.checkers.VarStack.VarStackSnapshot;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.CustomObjectValid.SomeObject;

@RunWith(Enclosed.class)
public final class SetterMethodCheckerTest {

    @Immutable
    static final class IsImmutableMatcher extends TypeSafeMatcher<Class<?>> {

        private static final IsImmutableMatcher INSTANCE = new IsImmutableMatcher();

        private IsImmutableMatcher() {
            super();
        }

        public static IsImmutableMatcher isImmutable() {
            return INSTANCE;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("is immutable.");
        }

        @Override
        protected boolean matchesSafely(final Class<?> klasse) {
            final AsmMutabilityChecker checker = createSetterMethodChecker(klasse);
            return IsImmutable.IMMUTABLE == checker.result();
        }

        private AsmMutabilityChecker createSetterMethodChecker(final Class<?> eineKlasse) {
            final ClassName dotted = Dotted.fromClass(notNull(eineKlasse));
            final ClassReader cr = tryToCreateClassReaderFor(dotted.asString());
            final AsmMutabilityChecker result = SetterMethodChecker.newInstance();
            cr.accept(result, 0);
            return result;
        }

        private static ClassReader tryToCreateClassReaderFor(final String dottedClassName) {
            try {
                return new ClassReader(dottedClassName);
            } catch (final IOException e) {
                final String msg = String.format("Unable to create ClassReader for '%s'.", dottedClassName);
                throw new IllegalStateException(msg, e);
            }
        }
        
    } // class IsImmutableMatcher

    
    @Immutable
    static final class IsMutableMatcher extends TypeSafeMatcher<Class<?>> {

        private static final IsMutableMatcher INSTANCE = new IsMutableMatcher();

        private IsMutableMatcher() {
            super();
        }

        public static IsMutableMatcher isMutable() {
            return INSTANCE;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("is mutable.");
        }

        @Override
        protected boolean matchesSafely(final Class<?> klasse) {
            final AsmMutabilityChecker checker = createSetterMethodChecker(klasse);
            return IsImmutable.NOT_IMMUTABLE == checker.result();
        }

        private AsmMutabilityChecker createSetterMethodChecker(final Class<?> eineKlasse) {
            final ClassName dotted = Dotted.fromClass(notNull(eineKlasse));
            final ClassReader cr = tryToCreateClassReaderFor(dotted.asString());
            final AsmMutabilityChecker result = SetterMethodChecker.newInstance();
            cr.accept(result, 0);
            return result;
        }

        private static ClassReader tryToCreateClassReaderFor(final String dottedClassName) {
            try {
                return new ClassReader(dottedClassName);
            } catch (final IOException e) {
                final String msg = String.format("Unable to create ClassReader for '%s'.", dottedClassName);
                throw new IllegalStateException(msg, e);
            }
        }
        
    } // class IsImmutableMatcher


    public static final class ValidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void verifyIntegerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void integerWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void charWithJvmInitial() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void floatWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void customObjectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CustomObjectValid.class;
            assertThat(klasse, isImmutable());
        }

    } // class TestsForValidSingleCheckLazyInitialisationWithoutAlias


    public static final class InvalidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void charWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CharInvalid.class;
            assertThat(klasse, isMutable());
        }


        @Test
        public void integerWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.ObjectInvalid.class).forMethod(
                    "hashCodeObject", Type.getType(Object.class)).andVariable("hash");
            // FIXME
            assertThat(r.numberOfAssignmentGuards(), is(0));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(0));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void floatWithMultipleCustomInitialValues() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
            final Reason r = new Reason(klasse).forMethod("hashCodeFloat", Type.FLOAT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
            final Reason r = new Reason(klasse).forMethod("hashCode", Type.INT_TYPE).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(1));
            assertThat(r.block(0), containsAssignmentGuardFor(r.variableName()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithCustomInitialValue.StringInvalid.class).forMethod(
                    "hashCodeString", Type.getType(String.class)).andVariable("hash");
            assertThat(r.numberOfAssignmentGuards(), is(0));
//            assertThat(r.block(0), covers(r.relevantJumpInstructions()));
            assertFalse(checksAgainstAppropriateComparativeValue(r));
        }

    } // class InvalidSingleCheckLazyInitialisationWithoutAlias


    public static final class ValidSingleCheckLazyInitialisationWithAlias {

        @Test
        public void byteWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ByteValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void shortWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.ShortValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.FloatValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void javaLangString() {
            final Class<?> klasse = String.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithAlias.WithJvmInitialValue.StringValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.StringValid.class;
            assertThat(klasse, isImmutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithAlias.WithCustomInitialValue.IntegerValid.class;
            assertThat(klasse, isImmutable());
        }

    } // TestsForValidSingleCheckLazyInitialisationWithAlias


    public static final class ValidDoubleCheckLazyInitialisationWithAlias {

        @Test
        public void aliasedValidIntegerWithJvmInitialValue() {
            final Class<?> klasse = de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault.class;
            assertThat(klasse, isImmutable());
        }

    } // ValidDoubleCheckLazyInitialisationWithAlias

}
