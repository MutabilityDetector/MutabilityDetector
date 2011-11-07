package org.mutabilitydetector.benchmarks.finalfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.NonFinalFieldChecker;

public class NonFinalFieldsCheckerTest {

    
    private final IMutabilityChecker checker = new NonFinalFieldChecker();


    @Test
    public void remainsImmutableWhenFieldIsFinal() throws Exception {
        TestUtil.runChecker(checker, ImmutableExample.class);
        assertEquals(IsImmutable.IMMUTABLE, checker.result());
    }
    
    
    @Test
    public void isEffectivelyImmutableWhenTheFieldIsNotDeclaredFinal() throws Exception {
        TestUtil.runChecker(checker, HasNonFinalField.class);
        assertEquals(IsImmutable.EFFECTIVELY_IMMUTABLE, checker.result());
    }
}
