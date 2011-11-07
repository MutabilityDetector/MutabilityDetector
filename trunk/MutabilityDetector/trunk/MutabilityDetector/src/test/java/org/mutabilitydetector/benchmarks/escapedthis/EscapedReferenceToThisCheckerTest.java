package org.mutabilitydetector.benchmarks.escapedthis;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.escapedthis.Safe.PassesThisReferenceAfterConstruction;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.ThisPassedToOtherObject;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.ThisPassedToOtherObjectAsOneOfManyParameters;
import org.mutabilitydetector.checkers.EscapedThisReferenceChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;

public class EscapedReferenceToThisCheckerTest {

    private final IMutabilityChecker checker = new EscapedThisReferenceChecker();

    @Test
    public void immutableExampleIsNotRenderedMutable() throws Exception {
        TestUtil.runChecker(checker, ImmutableExample.class);
        assertEquals(IsImmutable.IMMUTABLE, checker.result());
    }
    
    @Test
    public void thisReferenceEscapingAfterConstructionDoesNotRenderClassMutable() throws Exception {
        TestUtil.runChecker(checker, PassesThisReferenceAfterConstruction.class);
        assertEquals(IsImmutable.IMMUTABLE, checker.result());
    }
    
    @Test
    public void thisReferencePassedToConstructorOfOtherObjectRendersClassMutable() throws Exception {
        TestUtil.runChecker(checker, ThisPassedToOtherObject.class);
        assertEquals(IsImmutable.NOT_IMMUTABLE, checker.result());
        assertEquals(reasonDetailFor(ThisPassedToOtherObject.class), checker.reasons().iterator().next());
    }

    
    @Test
    public void thisReferencePassedToConstructorOfOtherObjectAtAnyPointInParameterListRendersClassMutable() throws Exception {
        TestUtil.runChecker(checker, ThisPassedToOtherObjectAsOneOfManyParameters.class);
        assertEquals(IsImmutable.NOT_IMMUTABLE, checker.result());
        assertEquals(reasonDetailFor(ThisPassedToOtherObjectAsOneOfManyParameters.class), checker.reasons().iterator().next());
    }

    private CheckerReasonDetail reasonDetailFor(Class<?> clazz) {
        return new CheckerReasonDetail("The 'this' reference is passed outwith the constructor.", 
                ClassLocation.fromDotted(Dotted.fromClass(clazz)),
                MutabilityReason.ESCAPED_THIS_REFERENCE);
    }
    
}
