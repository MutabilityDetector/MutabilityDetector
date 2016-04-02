package org.mutabilitydetector.unittesting.matchers.reasons;

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


import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.*;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.CyclicReferences;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.CodeLocationFactory;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestUtil.*;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.unittesting.AllowedReason.assumingFields;

@SuppressWarnings("unused")
public class AssumingArrayFieldsTest {


    private final MutableTypeInformation mutableTypeInfo = new MutableTypeInformation(
            testAnalysisSession(),
            OUT_OF_THE_BOX_CONFIGURATION,
            CyclicReferences.newEmptyMutableInstance());
    private final TypeStructureInformation typeStructureInfo = analysisDatabase().requestInformation(TYPE_STRUCTURE);
    private final Set<Dotted> immutableContainerClasses = Collections.emptySet();
    private final AnalysisInProgress analysisInProgress = AnalysisInProgress.noAnalysisUnderway();

    private final AsmMutabilityChecker mutableTypeToFieldChecker = new MutableTypeToFieldChecker(
            typeStructureInfo,
            mutableTypeInfo,
            testingVerifierFactory(),
            immutableContainerClasses,
            analysisInProgress, CodeLocationFactory.create());

    private final AsmMutabilityChecker arrayFieldChecker = new ArrayFieldMutabilityChecker();

    @Test
    public void matchesWhenGivenFieldNameIsLinkedToArrayFieldReason() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(arrayFieldChecker, ArrayFieldUsedSafely.class);

        assertThat(reason, assumingFields("myArrayField").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchWhenGivenIncorrectFieldName() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(arrayFieldChecker, ArrayFieldUsedSafely.class);

        assertThat(reason, not(assumingFields("myArrayFieldNOTCALLEDTHIS").areNotModifiedAndDoNotEscape()));
    }

    @Test
    public void matchesWhenArrayFieldIsConsideredAsAnAssignmentOfMutableTypeToField() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, ArrayFieldUsedSafely.class);

        assertThat(reason, assumingFields("myArrayField").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchForReasonWhereUnsafeToAssumeNotModifyingTheFieldLocally() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new PublishedNonFinalFieldChecker(), MutableForIrrelevantReason.class);

        assertThat(reason, not(assumingFields("reassignMe").areNotModifiedAndDoNotEscape()));
    }

    @Test
    public void doesNotMatchForReasonWhichDoesNotOriginateFromAField() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(new CanSubclassChecker(), CanSubclass.class);

        assertThat(reason, not(assumingFields("mutabilityIsNothingToDoWithThisField").areNotModifiedAndDoNotEscape()));
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
