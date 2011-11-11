package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.locations.CodeLocation;

public class ProvidedOtherClassTest {
    private static CodeLocation<?> unusedClassLocation = null;
    private Matcher<CheckerReasonDetail> matcher;

    @Test
    public void matchesWhenReasonIsAssigningAbstractTypeWithGivenClassNameToField() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("Field can have an abstract type (some.mutable.class) assigned to it.",
                                                             unusedClassLocation,
                                                             ABSTRACT_TYPE_TO_FIELD);

        matcher = ProvidedOtherClass.provided(dotted("some.mutable.class")).isAlsoImmutable();

        assertTrue(matcher.matches(reason));
    }
    
    @Test
    public void matchesWhenReasonIsAssigningMutableTypeWithGivenClassNameToField() throws Exception {
        CheckerReasonDetail reason = new CheckerReasonDetail("Field can have an abstract type (some.mutable.class) assigned to it.",
                                                             unusedClassLocation,
                                                             MUTABLE_TYPE_TO_FIELD);

        matcher = ProvidedOtherClass.provided(dotted("some.mutable.class")).isAlsoImmutable();

        assertTrue(matcher.matches(reason));
    }

    @Test
    public void doesNotMatchWhenThereDifferentAbstractTypeAssignedToField() {
        CheckerReasonDetail notAllowed = new CheckerReasonDetail("Field can have an abstract type (some.othermutable.class) assigned to it.",
                                                                 unusedClassLocation,
                                                                 ABSTRACT_TYPE_TO_FIELD);
        matcher = ProvidedOtherClass.provided(dotted("some.mutable.class")).isAlsoImmutable();

        assertFalse(matcher.matches(notAllowed));
    }
    
    @Test
    public void doesNotMatchesWhenNameOfOtherTypeAssignedIsNotExactlyEqual() {
        CheckerReasonDetail notAllowed = new CheckerReasonDetail("Field can have an abstract type (some.mutable.class.with.similar.but.different.name) assigned to it.",
                                                                 unusedClassLocation,
                                                                 ABSTRACT_TYPE_TO_FIELD);
        matcher = ProvidedOtherClass.provided(dotted("some.mutable.class")).isAlsoImmutable();

        assertFalse(matcher.matches(notAllowed));
    }


}
