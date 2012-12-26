package org.mutabilitydetector.unittesting.matchers.reasons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CanSubclassChecker;
import org.mutabilitydetector.checkers.CollectionWithMutableElementTypeToFieldChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.unittesting.MutabilityAssert;

@SuppressWarnings("unused")
public class AssumingFieldsTest {

    
    private final MutableTypeInformation mutableTypeInfo = new MutableTypeInformation(testAnalysisSession(), OUT_OF_THE_BOX_CONFIGURATION);
    private final TypeStructureInformation typeStructureInfo = analysisDatabase().requestInformation(TYPE_STRUCTURE);
    
    private final AsmMutabilityChecker mutableTypeToFieldChecker = new MutableTypeToFieldChecker(typeStructureInfo,
                                                                               mutableTypeInfo, 
                                                                               testingVerifierFactory());
    private final AsmMutabilityChecker mutableElementTypeChecker = new CollectionWithMutableElementTypeToFieldChecker(mutableTypeInfo, testingVerifierFactory());
    
    @Test
    public void matchesWhenGivenFieldNameIsLinkedToMutableTypeToFieldReason() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, MutableFieldUsedSafely.class);
        
        assertThat(reason, FieldAssumptions.named("myPrivateMap").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchWhenGivenIncorrectFieldName() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, MutableFieldUsedSafely.class);
        
        assertThat(reason, not(FieldAssumptions.named("myPrivateMapNOTCALLEDTHIS").areNotModifiedAndDoNotEscape()));
    }
    
    @Test
    public void matchesWhenFieldIsACollectionTypeWithAMutableElementType() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableElementTypeChecker, UsesMutableElementOfCollectionSafely.class);
        
        assertThat(reason, FieldAssumptions.named("dates").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchForReasonWhereUnsafeToAssumeNotModifyingTheFieldLocally() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new PublishedNonFinalFieldChecker(), MutableForIrrelevantReason.class);
        
        assertThat(reason, not(FieldAssumptions.named("reassignMe").areNotModifiedAndDoNotEscape()));
    }

    @Test
    public void doesNotMatchForReasonWhichDoesNotOriginateFromAField() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new CanSubclassChecker(), CanSubclass.class);
        
        assertThat(reason, not(FieldAssumptions.named("mutabilityIsNothingToDoWithThisField").areNotModifiedAndDoNotEscape()));
    }

    private MutableReasonDetail getOnlyReasonFromRunningChecker(AsmMutabilityChecker mutabilityChecker, Class<?> toAnalyse) {
        AnalysisResult result = TestUtil.runChecker(mutabilityChecker, toAnalyse);
        assertThat(result.isImmutable, is(NOT_IMMUTABLE));
        assertThat(result.reasons, Matchers.hasSize(1));
        
        return result.reasons.iterator().next();
    }
    
    
    private static final class MutableFieldUsedSafely {
        private final Map<String, Long> myPrivateMap;
        
        public MutableFieldUsedSafely() {
            this.myPrivateMap = new HashMap<String, Long>();
            myPrivateMap.put("a", 1L);
            myPrivateMap.put("b", 2L);
            myPrivateMap.put("c", 3L);
        }
        
        public Long getA() {
            return myPrivateMap.get("a");

        }
    }
    
    private static final class UsesMutableElementOfCollectionSafely {
        private final List<Date> dates;

        public UsesMutableElementOfCollectionSafely(List<Date> dates) {
            this.dates = Collections.unmodifiableList(new ArrayList<Date>(dates));
        }
        
        public Long getFirstTime() {
            return dates.get(0).getTime();
        }
        
    }
    
    private static final class MutableForIrrelevantReason {
        public String reassignMe;
    }
    
    protected static class CanSubclass {
        private final String mutabilityIsNothingToDoWithThisField = "unchangeable";
    }
}
