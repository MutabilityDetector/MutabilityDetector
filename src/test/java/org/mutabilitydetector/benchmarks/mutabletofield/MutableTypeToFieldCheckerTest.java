package org.mutabilitydetector.benchmarks.mutabletofield;

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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mutabilitydetector.*;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.WrapsCollectionUsingNonWhitelistedMethod;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.ListFieldFromUnmodifiableArrayAsList;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.StoresCopiedCollectionAsObjectAndIterable;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.StoresCopiedCollectionIntoLocalVariableBeforeWrapping;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableButHasUnmodifiedArrayAsField;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableWhenArrayFieldIsStatic;
import org.mutabilitydetector.benchmarks.mutabletofield.array.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.*;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mutabilitydetector.Configurations.NO_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.*;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.checkers.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

public class MutableTypeToFieldCheckerTest {

    @Rule public MethodRule rule = new IncorrectAnalysisRule();

    private AnalysisSession session;
    private AsmMutabilityChecker checkerWithMockedSession;
    private AsmMutabilityChecker checkerWithRealSession;

    private AnalysisResult result;
    private final AnalysisResult unusedAnalysisResult = unusedAnalysisResult(dotted("some.class.Name"), NOT_IMMUTABLE);

    private final Dotted mutableExample = Dotted.fromClass(MutableExample.class);
    private final Set<Dotted> immutableContainerClasses = Collections.emptySet();

    private MutableTypeToFieldChecker checkerWithRealAnalysisSession() {
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        return new MutableTypeToFieldChecker(
                info,
                new MutableTypeInformation(
                        testAnalysisSession(),
                        NO_CONFIGURATION,
                        CyclicReferences.newEmptyMutableInstance()),
                testingVerifierFactory(),
                immutableContainerClasses,
                AnalysisInProgress.noAnalysisUnderway());
    }


    @Before
    public void setUpWithMockSession() {
        session = mock(AnalysisSession.class);
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        checkerWithMockedSession = new MutableTypeToFieldChecker(
                info,
                new MutableTypeInformation(session, NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance()),
                testingVerifierFactory(),
                immutableContainerClasses,
                AnalysisInProgress.noAnalysisUnderway());
    }


    @Before
    public void setUpWithRealSession() {
        InformationRetrievalRunner runner = new InformationRetrievalRunner(TestUtil.testAnalysisSession(), createWithCurrentClasspath(FAIL_FAST));
        TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
        checkerWithRealSession = new MutableTypeToFieldChecker(
                typeInfo,
                new MutableTypeInformation(testAnalysisSession(), NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance()),
                testingVerifierFactory(),
                immutableContainerClasses,
                AnalysisInProgress.noAnalysisUnderway());
    }

    @Test
    public void requestsMutableStatusOfPublishedField() throws Exception {
        AnalysisInProgress analysisInProgressWhenRequestingMutabilityOfField = analysisInProgressIncludes(MutableByHavingMutableFieldAssigned.class);

        when(session.processTransitiveAnalysis(
                mutableExample,
                analysisInProgressWhenRequestingMutabilityOfField))
                .thenReturn(unusedAnalysisResult);

        runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        verify(session).processTransitiveAnalysis(mutableExample, analysisInProgressWhenRequestingMutabilityOfField);
    }

    private AnalysisInProgress analysisInProgressIncludes(Class<MutableByHavingMutableFieldAssigned> analyzing) {
        return AnalysisInProgress.noAnalysisUnderway().analysisStartedFor(Dotted.fromClass(analyzing));
    }

    @Test
    public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
        AnalysisInProgress analysisInProgressWhenRequestingMutabilityOfField = analysisInProgressIncludes(MutableByHavingMutableFieldAssigned.class);

        String unusedMessage = "";
        AnalysisResult mutableResult = AnalysisResult.analysisResult(
                dotted("a.b.Whatever"),
                NOT_IMMUTABLE,
                newMutableReasonDetail(unusedMessage, unusedCodeLocation(), ABSTRACT_COLLECTION_TYPE_TO_FIELD));

        when(session.processTransitiveAnalysis(mutableExample, analysisInProgressWhenRequestingMutabilityOfField)).thenReturn(mutableResult);

        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        assertThat(result, areNotImmutable());
        assertThat(checkerWithMockedSession, hasReasons(MUTABLE_TYPE_TO_FIELD));
    }

    @Test
    public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
        AnalysisInProgress analysisInProgressWhenRequestingMutabilityOfField = analysisInProgressIncludes(MutableByHavingMutableFieldAssigned.class);

        when(session.processTransitiveAnalysis(mutableExample, analysisInProgressWhenRequestingMutabilityOfField)).thenReturn(unusedAnalysisResult);

        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        assertThat(result, areNotImmutable());
        assertThat(checkerWithMockedSession, hasReasons(MUTABLE_TYPE_TO_FIELD));
    }

    @Test
    public void instanceFieldWhichHasAMutatedArrayIsMutable() throws Exception {
        result = runChecker(checkerWithMockedSession, MutableByHavingArrayTypeAsField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    @FalsePositive("Array field is final, is never published or modified.")
    public void instanceFieldWhichHasAFinalUnmodifiedArrayIsImmutable() throws Exception {
        result = runChecker(checkerWithMockedSession, ImmutableButHasUnmodifiedArrayAsField.class);
        assertThat(result, areImmutable());
    }

    @Test
    public void staticFieldWhichHasAMutatedArrayIsImmutable() throws Exception {
        result = runChecker(checkerWithMockedSession, ImmutableWhenArrayFieldIsStatic.class);
        assertThat(result, areImmutable());
    }

    @Test
    public void codeLocationIsFieldLocation() throws Exception {
        when(session.processTransitiveAnalysis(eq(mutableExample), any(AnalysisInProgress.class))).thenReturn(unusedAnalysisResult);

        runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);
        FieldLocation codeLocation = (FieldLocation) checkerWithMockedSession.checkerResult().reasons.iterator().next().codeLocation();

        assertThat(codeLocation.typeName(), is(MutableByHavingMutableFieldAssigned.class.getName()));
        assertThat(codeLocation.fieldName(), is("mutableField"));
    }

    @Test
    public void codeLocationIsFieldLocationForArrayField() throws Exception {
        runChecker(checkerWithMockedSession, MutableByHavingArrayTypeAsField.class);
        FieldLocation codeLocation = (FieldLocation) checkerWithMockedSession.checkerResult().reasons.iterator().next().codeLocation();

        assertThat(codeLocation.typeName(), is(MutableByHavingArrayTypeAsField.class.getName()));
        assertThat(codeLocation.fieldName(), is("names"));
    }

    @Test
    public void allowsCopyingAndWrappingInUmodifiableCollectionTypeIdiom() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        assertThat(runChecker(checkerWithRealSession, CopyListIntoNewArrayListAndUnmodifiableListIdiom.class),
                   areImmutable());
    }

    @Test
    public void raisesAnErrorIfWrappedInUnmodifiableCollectionTypeButIsNotCopiedFirst() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        result = runChecker(checkerWithRealSession, WrapWithUnmodifiableListWithoutCopyingFirst.class);

        assertThat(result, areNotImmutable());
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();

        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), startsWith("Attempts to wrap mutable collection type without safely performing a copy first."));
    }

    @Test
    public void providesHintIfWrappedInUnmodifiableCollectionTypeButIsNotCopiedFirst() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        result = runChecker(checkerWithRealSession, WrapWithUnmodifiableListWithoutCopyingFirst.class);

        assertThat(result, areNotImmutable());
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();

        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), is("Attempts to wrap mutable collection type without safely performing a copy first. You can use this expression: Collections.unmodifiableList(new ArrayList<ImmutableExample>(unmodifiable))"));
    }

    @Test
    public void raisesAnErrorIfWrappedInUnmodifiableCollectionTypeUsingANonWhitelistedMethod() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        result = runChecker(checkerWithRealSession, WrapsCollectionUsingNonWhitelistedMethod.class);

        assertThat(result, areNotImmutable());
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();

        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), Matchers.startsWith("Field is not a wrapped collection type."));
    }

    @Test
    public void doesNotAllowStoringCopiedCollectionIntoLocalVariableThatCouldEscape() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        result = runChecker(checkerWithRealSession, StoresCopiedCollectionIntoLocalVariableBeforeWrapping.class);

        assertThat(result, areNotImmutable());
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();

        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), startsWith("Attempts to wrap mutable collection type without safely performing a copy first."));
    }

    @Test
    public void allowsStoringASafelyCopiedAndWrappedCollectionIntoFieldOfMoreAbstractType() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        AnalysisResult runChecker = runChecker(checkerWithRealSession, StoresCopiedCollectionAsObjectAndIterable.class);
        assertThat(runChecker, areImmutable());
    }

    @Test
    public void arraysAsListCopiedIntoUnmodifiableIsNotImmutable() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();

        result = runChecker(checkerWithRealSession, ListFieldFromUnmodifiableArrayAsList.class);
        assertThat(result, areNotImmutable());

        MutableReasonDetail reasonDetail = result.reasons.iterator().next();

        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), startsWith("Attempts to wrap mutable collection type without safely performing a copy first."));
    }

    @Test
    public void immutableExamplePassesCheck() throws Exception {
        result = runChecker(checkerWithRealSession, ImmutableExample.class);
        assertThat(result, areImmutable());
        assertEquals(result.reasons.size(), 0);
    }

    @Test
    public void mutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
        result = runChecker(checkerWithRealSession, MutableByAssigningInterfaceToField.class);

        assertThat(result, areNotImmutable());
    }

    @Test
    public void mutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
        result = runChecker(checkerWithRealSession, MutableByAssigningAbstractTypeToField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void classLocationOfResultIsSet() throws Exception {
        result = runChecker(checkerWithRealSession, MutableByAssigningAbstractTypeToField.class);

        assertThat(result.reasons.size(), is(1));

        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        String typeName = reasonDetail.codeLocation().typeName();
        assertThat(typeName, is(MutableByAssigningAbstractTypeToField.class.getName()));
    }

    @Test
    public void reasonCreatedByCheckerIncludesMessagePointingToAbstractType() throws Exception {
        result = runChecker(checkerWithRealSession, MutableByAssigningAbstractTypeToField.class);
        Class<?> abstractTypeAssigned = AbstractStringContainer.class;

        assertThat(result.reasons.size(), is(1));

        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        assertThat(reasonDetail.message(), containsString(abstractTypeAssigned.getName()));
    }

    @Test
    public void reasonHasCodeLocationPointingAtFieldWhichIsOfAnAbstractType() throws Exception {
        result = runChecker(checkerWithRealSession, MutableByAssigningAbstractTypeToField.class);

        FieldLocation fieldLocation = (FieldLocation) result.reasons.iterator().next().codeLocation();

        assertThat(fieldLocation.typeName(), is(MutableByAssigningAbstractTypeToField.class.getName()));
        assertThat(fieldLocation.fieldName(), is("nameContainer"));
    }

    @Test
    public void doesNotConsiderAClassHardcodedAsAnImmutableContainerClassMutable() throws Exception {
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        MutableTypeToFieldChecker checker = new MutableTypeToFieldChecker(
                info,
                new MutableTypeInformation(testAnalysisSession(), NO_CONFIGURATION, CyclicReferences.newEmptyMutableInstance()),
                testingVerifierFactory(),
                ImmutableSet.of(Dotted.fromClass(AbstractStringContainer.class)),
                AnalysisInProgress.noAnalysisUnderway());

        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        assertThat(result.isImmutable, is(IsImmutable.IMMUTABLE));
    }

}
