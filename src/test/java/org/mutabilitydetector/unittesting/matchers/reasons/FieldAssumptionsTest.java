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


import com.google.common.collect.ImmutableSet;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.*;
import org.mutabilitydetector.checkers.info.*;
import org.mutabilitydetector.checkers.settermethod.SetterMethodChecker;
import org.mutabilitydetector.locations.CodeLocationFactory;
import org.mutabilitydetector.locations.Dotted;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestUtil.*;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.unittesting.AllowedReason.assumingFields;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

@SuppressWarnings("unused")
public class FieldAssumptionsTest {

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

    private final AsmMutabilityChecker mutableElementTypeChecker = new CollectionWithMutableElementTypeToFieldChecker(
            mutableTypeInfo,
            testingVerifierFactory(),
            ImmutableSet.<Dotted>of(),
            analysisInProgress);

    private final PrivateMethodInvocationInformation privateMethodInvocationInfo = TestUtil.analysisDatabase().requestInformation(AnalysisDatabase.PRIVATE_METHOD_INVOCATION);
    private final AsmMutabilityChecker setterMethodChecker = SetterMethodChecker.newInstance(privateMethodInvocationInfo);
    private final AsmMutabilityChecker nonFinalFieldChecker = new NonFinalFieldChecker();

    @Test
    public void matchesWhenGivenFieldNameIsLinkedToMutableTypeToFieldReason() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, MutableFieldUsedSafely.class);

        assertThat(reason, assumingFields("myPrivateMap").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void doesNotMatchWhenGivenIncorrectFieldName() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableTypeToFieldChecker, MutableFieldUsedSafely.class);

        assertThat(reason, not(assumingFields("myPrivateMapNOTCALLEDTHIS").areNotModifiedAndDoNotEscape()));
    }

    @Test
    public void matchesWhenFieldIsACollectionTypeWithAMutableElementType() throws Exception {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(mutableElementTypeChecker, UsesMutableElementOfCollectionSafely.class);

        assertThat(reason, assumingFields("dates").areNotModifiedAndDoNotEscape());
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

    @Test
    public void allowsReassigningFieldsAsPartOfInternalCachingStrategy() {
        MutableReasonDetail reason = getOnlyReasonFromRunningChecker(setterMethodChecker, ReassignsForCaching.class);
        assertThat(reason, assumingFields("first3Chars").areModifiedAsPartOfAnUnobservableCachingStrategy());
    }

    @Test
    public void allowsNonFinalFieldsAsPartOfInternalCachingStrategy() throws Exception {
        AnalysisResult result = TestUtil.runChecker(nonFinalFieldChecker, ReassignsForCaching.class);
        assertThat(result.isImmutable, is(IsImmutable.EFFECTIVELY_IMMUTABLE));
        assertThat(result.reasons, Matchers.hasSize(1));
        MutableReasonDetail reason = result.reasons.iterator().next();

        assertThat(reason, assumingFields("first3Chars").areModifiedAsPartOfAnUnobservableCachingStrategy());
    }

    @Test
    public void isImmutable() throws Exception {
        assertImmutable(FieldAssumptions.class);
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

    private static final class ReassignsForCaching {
        public final String someString;
        private String first3Chars;
        public ReassignsForCaching(String someString, String first3Chars) {
            this.someString = someString;
        }

        public String getFirst3Chars() {
            if (first3Chars == null) {
                first3Chars = someString.substring(0, 3);
            }
            return first3Chars;
        }
    }
}
