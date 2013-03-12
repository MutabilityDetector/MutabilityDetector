package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.CandidatesForLazyVariablesFinderTest.CandidatesSizeMatcher.hasNumberOfCandidates;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import javax.annotation.concurrent.ThreadSafe;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithCustomInitialValue.FloatValid;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.CharInvalid2;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.CharValid;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias.WithJvmInitialValue.StringStaticValid;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 05.03.2013
 */
public final class CandidatesForLazyVariablesFinderTest {

    @ThreadSafe
    public static final class CandidatesSizeMatcher extends TypeSafeMatcher<Class<?>> {

        private final int expectedNumber;

        private CandidatesSizeMatcher(final int theExpectedNumber) {
            expectedNumber = theExpectedNumber;
        }

        public static CandidatesSizeMatcher hasNumberOfCandidates(final int expectedNumber) {
            return new CandidatesSizeMatcher(expectedNumber);
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValue(expectedNumber);            
            description.appendText(" candidates for lazy variables.");
        }

        @Override
        protected boolean matchesSafely(final Class<?> klasse) {
            final VariableInitialisersAssociation c = findCandidatesForLazyVariablesIn(klasse);
            return expectedNumber == c.getSize();
        }

    } // class CandidatesSizeMatcher


    private static VariableInitialisersAssociation findCandidatesForLazyVariablesIn(final Class<?> klasse) {
        final CandidatesFinder f = getFinderForClass(klasse);
        return f.find();
    }

    private static CandidatesFinder getFinderForClass(final Class<?> klasse) {
        final EnhancedClassNode c = createConvenienceClassNodeFor(klasse);
        return CandidatesFinder.newInstance(c.getFields());
    }

    private static EnhancedClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        return factory.getConvenienceClassNodeFor(klasse);
    }


    @Test
    public void charValidContainsOneCandidate() {
        assertThat(CharValid.class, hasNumberOfCandidates(1));
    }

    @Test
    public void charInvalid2ContainsTwoCandidates() {
        assertThat(CharInvalid2.class, hasNumberOfCandidates(2));
    }

    @Test
    public void factoryMethodChecksArgument() {
        final String exp = "Argument 'variablesOfAnalysedClass' must not be null!";
        try {
            CandidatesFinder.newInstance(null);
            fail(String.format("Expected NullPointerException with message '%s'.", exp));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

    @Test
    public void plurallyGettingResultDeliversAlwaysSameObject() {
        final CandidatesFinder f = getFinderForClass(FloatValid.class);
        final VariableInitialisersAssociation r1 = f.find();
        final VariableInitialisersAssociation r2 = f.find();
        final VariableInitialisersAssociation r3 = f.find();
        assertThat(r1, is(equalTo(r2)));
        assertThat(r2, is(equalTo(r3)));
        assertThat(r1, is(equalTo(r3)));
    }

    @Test
    public void staticVariablesAreAlsoRecognized() {
        assertThat(StringStaticValid.class, hasNumberOfCandidates(1));
    }

}
