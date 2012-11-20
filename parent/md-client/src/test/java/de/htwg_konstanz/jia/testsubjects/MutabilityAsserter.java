/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.FieldLocation;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 19.11.2012
 */
public abstract class MutabilityAsserter {

    protected final AnalysisResult analysisResult;

    public MutabilityAsserter(final Class<?> classToAnalyse) {
        super();
        analysisResult = AnalysisSessionHolder.analysisResultFor(classToAnalyse);
    }

    public void assertIsMutable() {
        defaultAssertIsMutable(analysisResult);
    }

    protected final void defaultAssertIsMutable(final AnalysisResult analysisResult) {
        assertEquals(IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);
    }

    public void assertOneReasonIsThat(final Matcher<MutableReasonDetail> firstMatcher,
            final Matcher<?>... furtherMatchers) {
        @SuppressWarnings("unchecked")
        final Matcher<MutableReasonDetail>[] allMatchers = (Matcher<MutableReasonDetail>[]) ArrayUtils.add(
                furtherMatchers, firstMatcher);
        assertThat(analysisResult.reasons, hasItem(allOf(allMatchers)));
    }

    public <T> void assertTheReasonIsThat(final Matcher<MutableReasonDetail> firstMatcher,
            final Matcher<?>... furtherMatchers) {
        @SuppressWarnings("unchecked")
        final Matcher<MutableReasonDetail>[] allMatchers = (Matcher<MutableReasonDetail>[]) ArrayUtils.add(
                furtherMatchers, firstMatcher);
        assertThat(analysisResult.reasons, everyItem(allOf(allMatchers)));
    }

    public Matcher<MutableReasonDetail> fieldCanBeReassigned(final String fieldName, final String methodName) {
        return defaultFieldCanBeReassigned(fieldName, methodName);
    }

    protected final Matcher<MutableReasonDetail> defaultFieldCanBeReassigned(String fieldName, String methodName) {
        final String expectedMessage = format("Field [%s] can be reassigned within method [%s]", fieldName, methodName);
        final Matcher<MutableReasonDetail> result = new TypeSafeMatcher<MutableReasonDetail>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText("a 'MutableReasonDetail' with\n");
                description.appendText("  reason 'FIELD_CAN_BE_REASSIGNED'\n");
                description.appendText("  and message: '").appendText(expectedMessage).appendText("'.");
            }
    
            @Override
            protected boolean matchesSafely(final MutableReasonDetail mutableReasonDetail) {
                return MutabilityReason.FIELD_CAN_BE_REASSIGNED == mutableReasonDetail.reason()
                        && expectedMessage.equals(mutableReasonDetail.message());
            }
        };
        return result;
    }

    public Matcher<MutableReasonDetail> field(final String fieldName) {
        return new TypeSafeMatcher<MutableReasonDetail>() {
            @Override
            public void describeTo(final Description desc) {
                desc.appendText("a field with name '").appendValue(fieldName).appendText("'");
            }

            @Override
            protected boolean matchesSafely(final MutableReasonDetail mutableReasonDetail) {
                return fieldNameIsExpected(mutableReasonDetail.codeLocation());
            }

            private boolean fieldNameIsExpected(final CodeLocation<?> codeLocation) {
                final FieldLocation fieldLocation = (FieldLocation) codeLocation;
                return fieldName.equals(fieldLocation.fieldName());
            }
        };
    }

    public Matcher<MutableReasonDetail> analysedClass() {
        return new TypeSafeMatcher<MutableReasonDetail>() {
            @Override
            public void describeTo(final Description desc) {
                desc.appendText("a class with name '").appendValue(analysisResult.dottedClassName).appendText("'");
            }

            @Override
            protected boolean matchesSafely(final MutableReasonDetail mutableReasonDetail) {
                return classNameIsExpected(mutableReasonDetail.codeLocation());
            }

            private boolean classNameIsExpected(final CodeLocation<?> codeLocation) {
                final String expectedTypeName = analysisResult.dottedClassName;
                final ClassLocation classLocation = (ClassLocation) codeLocation;
                return expectedTypeName.equals(classLocation.typeName());
            }
        };
    }

    public Matcher<MutableReasonDetail> hasMutableType(final Class<?> mutableFieldType) {
        return new TypeSafeMatcher<MutableReasonDetail>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText(" a 'MutableReasonDetail' with");
                description.appendText(" reason 'MUTABLE_TYPE_TO_FIELD'");
                description.appendText(" because the field is of type '");
                description.appendValue(mutableFieldType.getName()).appendText("'.");
            }

            @Override
            protected boolean matchesSafely(final MutableReasonDetail mutableReasonDetail) {
                return isExpectedReason(mutableReasonDetail.reason())
                        && isExpectedMessage(mutableReasonDetail.message());
            }

            private boolean isExpectedReason(final Reason reason) {
                return MutabilityReason.MUTABLE_TYPE_TO_FIELD == reason;
            }

            private boolean isExpectedMessage(final String actualMessage) {
                final String messageTemplate = "Field can have a mutable type (%s) assigned to it.";
                final String expectedMessage = format(messageTemplate, mutableFieldType.getName());                
                return expectedMessage.equals(actualMessage);
            }
        };
    }

    public Matcher<MutableReasonDetail> leaksItsThisReference() {
        return new TypeSafeMatcher<MutableReasonDetail>() {
            @Override
            public void describeTo(final Description desc) {
                desc.appendText(" a 'MutableReasonDetail' with");
                desc.appendText(" reason 'ESCAPED_THIS_REFERENCE'");
                desc.appendText(" because the 'this'-reference escapes the constructor.");
            }

            @Override
            protected boolean matchesSafely(final MutableReasonDetail mutableReasonDetail) {
                return isExpectedReason(mutableReasonDetail.reason())
                        && isExpectedMessage(mutableReasonDetail.message());
            }

            private boolean isExpectedReason(final Reason reason) {
                return MutabilityReason.ESCAPED_THIS_REFERENCE == reason;
            }

            private boolean isExpectedMessage(final String actualMessage) {
                return "The 'this' reference is passed outwith the constructor.".equals(actualMessage);
            }
        };
    }

    public void assertNumberOfReasonsIs(final int numberOfReasons) {
        defaultAssertNumberOfReasonsIs(analysisResult, numberOfReasons);
    }

    protected final void defaultAssertNumberOfReasonsIs(final AnalysisResult analysisResult,
            final int numberOfReasons) {
        final String message = createNumberOfReasonsMessage(numberOfReasons);
        final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        assertEquals(message, numberOfReasons, reasons.size());
    }

    private String createNumberOfReasonsMessage(final int numberOfReasons) {
        final String messageTemplate = "%d reason%s expected.";
        final String message;
        if (1 < numberOfReasons) {
            message = format(messageTemplate, numberOfReasons, 's');
        } else {
            message = format(messageTemplate, numberOfReasons, "");
        }
        return message;
    }

    public void assertAppropriateReasons(final Reason firstReason, final Reason... furtherReasons) {
        defaultAssertAppropriateReasons(analysisResult, firstReason, furtherReasons);
    }

    protected void defaultAssertAppropriateReasons(final AnalysisResult analysisResult,
            final Reason firstReason, final Reason... furtherReasons) {
        final int numberOfExpectedReasons = 1 + furtherReasons.length;
        assertNumberOfReasonsIs(numberOfExpectedReasons);
        final List<Reason> expectedReasons = joinReasons(firstReason, numberOfExpectedReasons, furtherReasons);
        final List<Reason> actualReasons = extractReasons(analysisResult.reasons);
        assertTrue("Only the expected reasons should have occurred.", actualReasons.containsAll(expectedReasons));
    }

    private List<Reason> joinReasons(final Reason firstReason, final int numberOfExpectedReasons,
            final Reason... furtherReasons) {
        final List<Reason> result = new ArrayList<Reason>(numberOfExpectedReasons);
        result.add(firstReason);
        for (final Reason furtherReason : furtherReasons) {
            result.add(furtherReason);
        }
        return result;
    }

    private List<Reason> extractReasons(final Collection<MutableReasonDetail> reasons) {
        final List<Reason> result = new ArrayList<Reason>(reasons.size());
        for (final MutableReasonDetail mutableReasonDetail : reasons) {
            result.add(mutableReasonDetail.reason());
        }        
        return result;
    }

}
