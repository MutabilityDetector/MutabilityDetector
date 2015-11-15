package org.mutabilitydetector.benchmarks;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.locations.Dotted.fromClass;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import com.google.common.collect.ImmutableSet;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.HasImmutableContainerOfGenericType;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.HasImmutableContainerOfImmutableType;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.HasImmutableContainerOfMutableType;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.ImmutableContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.NestedGenericTypes;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.SafelyCopiedMapGenericOnMutableTypeForKey;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.SafelyCopiedMap_UsesGenericTypeOfClass;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CollectionWithMutableElementTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.CyclicReferences;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.locations.Dotted;

public class CollectionWithMutableElementTypeToFieldCheckerTest {

    private final MutableTypeInformation mutableTypeInfo =
            new MutableTypeInformation(
                    testAnalysisSession(),
                    Configurations.NO_CONFIGURATION,
                    CyclicReferences.newEmptyMutableInstance());

    private final AsmMutabilityChecker checker =
            new CollectionWithMutableElementTypeToFieldChecker(
                    mutableTypeInfo,
                    testingVerifierFactory(),
                    ImmutableSet.<Dotted>of(),
                    AnalysisInProgress.noAnalysisUnderway());

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
    public void raisesErrorWhenCollectionFieldHasElementTypeUsingGenericTypeOfClass() {
        AnalysisResult result = runChecker(checker, SafelyCopiedMap_UsesGenericTypeOfClass.class);
        assertThat(result, areNotImmutable());
        assertThat(checker, hasReasons(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE));
        assertThat(checker.reasons().iterator().next().message(), containsString("<org.mutabilitydetector.benchmarks.ImmutableExample, SOME_GENERIC_TYPE>"));
    }

    @Test
    public void raisesErrorForHardcodedImmutableContainerTypeThatIsGenericWithMutableType() {
        AsmMutabilityChecker checker = checkerWithHardcodedAsImmutable(ImmutableContainer.class);

        AnalysisResult result = runChecker(checker, HasImmutableContainerOfMutableType.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void raisesErrorForHardcodedImmutableContainerTypeThatIsGenericWithVariableGenericParameter() {
        AsmMutabilityChecker checker = checkerWithHardcodedAsImmutable(ImmutableContainer.class);

        AnalysisResult result = runChecker(checker, HasImmutableContainerOfGenericType.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void doesNotRaiseErrorForHardcodedImmutableContainerTypeThatIsGenericWithImmutableType() {
        AsmMutabilityChecker checker = checkerWithHardcodedAsImmutable(ImmutableContainer.class);

        AnalysisResult result = runChecker(checker, HasImmutableContainerOfImmutableType.class);
        assertThat(result, areImmutable());
    }

    private AsmMutabilityChecker checkerWithHardcodedAsImmutable(Class<ImmutableContainer> containerClass) {
        return new CollectionWithMutableElementTypeToFieldChecker(
                    mutableTypeInfo,
                    testingVerifierFactory(),
                    ImmutableSet.of(fromClass(containerClass)),
                    AnalysisInProgress.noAnalysisUnderway());
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
