package org.mutabilitydetector.benchmarks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.NestedGenericTypes;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.SafelyCopiedMapGenericOnMutableTypeForKey;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CollectionWithMutableElementTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;

public class CollectionWithMutableElementTypeToFieldCheckerTest {

    private MutableTypeInformation mutableTypeInfo = 
            new MutableTypeInformation(testAnalysisSession(), Configurations.NO_CONFIGURATION);
    
    private final AsmMutabilityChecker checker = 
            new CollectionWithMutableElementTypeToFieldChecker(mutableTypeInfo, testingVerifierFactory());
    
    @Test
    public void safelyWrappedCollectionsAreStillMutableIfTheTypeOfListElementsIsMutable() throws Exception {
        AnalysisResult result = runChecker(checker, SafelyCopiedMapGenericOnMutableTypeForKey.class);
        assertThat(result, areNotImmutable());
        assertThat(checker, hasReasons(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE));
    }

    @Test
    public void doesNotRaiseErrorWhenElementTypeIsImmutable() throws Exception {
        AnalysisResult result = runChecker(checker, SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields.class);
        assertThat(result, areImmutable());
        assertThat(checker.reasons(), Matchers.<MutableReasonDetail>empty());
    }

    @Test
    public void supportsNestedGenericTypes() throws Exception {
        AnalysisResult result = runChecker(checker, NestedGenericTypes.class);
        assertThat(result, areNotImmutable());
        assertThat(checker, hasReasons(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE));
    }
    
    @Test
    public void descriptionOfCollectionWithMutableElementType() throws Exception {
        AnalysisResult result = runChecker(checker, SafelyCopiedMapGenericOnMutableTypeForKey.class);
        assertThat(result, areNotImmutable());
        
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        
        assertThat(reasonDetail.message(), 
                is("Field can have collection with mutable element type " +
                        "(java.util.Map<java.util.Date, org.mutabilitydetector.benchmarks.ImmutableExample>) assigned to it."));
    }
    
}
