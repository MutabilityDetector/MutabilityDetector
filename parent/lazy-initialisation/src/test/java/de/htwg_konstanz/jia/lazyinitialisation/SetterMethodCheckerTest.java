package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.SetterMethodCheckerTest.IsImmutableMatcher.isImmutable;
import static de.htwg_konstanz.jia.lazyinitialisation.SetterMethodCheckerTest.IsMutableMatcher.isMutable;
import static org.apache.commons.lang3.Validate.notNull;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

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
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.CustomObjectInvalid.class;
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
            assertThat(klasse, isMutable());
        }

        @Test
        public void floatWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.FloatInvalid.class;
            assertThat(klasse, isMutable());
        }

        @Test
        public void objectWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.ObjectInvalid.class;
            assertThat(klasse, isMutable());
        }

        @Test
        public void stringWithJvmInitialValue() {
            final Class<?> klasse = WithoutAlias.WithJvmInitialValue.StringInvalid.class;
            assertThat(klasse, isMutable());
        }

        @Test
        public void floatWithMultipleCustomInitialValues() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
            assertThat(klasse, isMutable());
        }

        @Test
        public void integerWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.IntegerInvalid.class;
            assertThat(klasse, isMutable());
        }

        @Test
        public void stringWithCustomInitialValue() {
            final Class<?> klasse = WithoutAlias.WithCustomInitialValue.StringInvalid.class;
            assertThat(klasse, isMutable());
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
