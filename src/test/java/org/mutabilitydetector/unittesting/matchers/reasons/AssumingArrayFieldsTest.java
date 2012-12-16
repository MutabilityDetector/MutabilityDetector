package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mutabilitydetector.ConfigurationBuilder.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.ArrayFieldMutabilityChecker;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CanSubclassChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;

@SuppressWarnings("unused")
public class AssumingArrayFieldsTest {

    
    private final MutableTypeInformation mutableTypeInfo = new MutableTypeInformation(testAnalysisSession(), OUT_OF_THE_BOX_CONFIGURATION);
    private final TypeStructureInformation typeStructureInfo = analysisDatabase().requestInformation(TYPE_STRUCTURE);
    
    private final AsmMutabilityChecker mutableTypeToFieldChecker = new MutableTypeToFieldChecker(typeStructureInfo,
                                                                               mutableTypeInfo, 
                                                                               testingVerifierFactory());
    
    private final AsmMutabilityChecker arrayFieldChecker = new ArrayFieldMutabilityChecker();
    
    @Test
    public void matchesWhenGivenFieldNameIsLinkedToArrayFieldReason() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(arrayFieldChecker, ArrayFieldUsedSafely.class);
        
        assertThat(reason, AssumingArrayFields.named("myArrayField").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchWhenGivenIncorrectFieldName() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(arrayFieldChecker, ArrayFieldUsedSafely.class);
        
        assertThat(reason, not(AssumingArrayFields.named("myArrayFieldNOTCALLEDTHIS").areNotModifiedAndDoNotEscape()));
    }
    
    @Test
    public void matchesWhenArrayFieldIsConsideredAsAnAssignmentOfMutableTypeToField() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, ArrayFieldUsedSafely.class);
        
        assertThat(reason, AssumingArrayFields.named("myArrayField").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchForReasonWhereUnsafeToAssumeNotModifyingTheFieldLocally() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new PublishedNonFinalFieldChecker(), MutableForIrrelevantReason.class);
        
        assertThat(reason, not(AssumingArrayFields.named("reassignMe").areNotModifiedAndDoNotEscape()));
    }

    @Test
    public void doesNotMatchForReasonWhichDoesNotOriginateFromAField() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new CanSubclassChecker(), CanSubclass.class);
        
        assertThat(reason, not(AssumingArrayFields.named("mutabilityIsNothingToDoWithThisField").areNotModifiedAndDoNotEscape()));
    }

    private MutableReasonDetail getOnlyReasonFromRunningChecker(AsmMutabilityChecker mutabilityChecker, Class<?> toAnalyse) {
        AnalysisResult result = TestUtil.runChecker(mutabilityChecker, toAnalyse);
        assertThat(result.isImmutable, is(NOT_IMMUTABLE));
        assertThat(result.reasons, Matchers.hasSize(1));
        
        return result.reasons.iterator().next();
    }
    
    
    private static final class ArrayFieldUsedSafely {
        private final int[] myArrayField = new int[] { 1, 2 };
        
        public int getFirst() {
            return myArrayField[0];
        }
    }
    
    private static final class MutableForIrrelevantReason {
        public String reassignMe;
    }
    
    protected static class CanSubclass {
        private final String mutabilityIsNothingToDoWithThisField = "unchangeable";
    }
    
}
