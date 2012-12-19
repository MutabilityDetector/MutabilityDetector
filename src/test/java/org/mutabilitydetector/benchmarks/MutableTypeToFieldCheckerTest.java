/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.TestUtil.unusedAnalysisResult;
import static org.mutabilitydetector.TestUtil.unusedCodeLocation;
import static org.mutabilitydetector.checkers.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.ListFieldFromUnmodifiableArrayAsList;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.StoresCopiedCollectionAsObjectAndIterable;
import org.mutabilitydetector.benchmarks.mutabletofield.CollectionFields.StoresCopiedCollectionIntoLocalVariableBeforeWrapping;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.benchmarks.mutabletofield.WrapWithUnmodifiableListWithoutCopyingFirst;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableButHasUnmodifiedArrayAsField;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableWhenArrayFieldIsStatic;
import org.mutabilitydetector.benchmarks.mutabletofield.array.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;

public class MutableTypeToFieldCheckerTest {

    @Rule public MethodRule rule = new IncorrectAnalysisRule();
    
    private AnalysisSession session;
    private AsmMutabilityChecker checkerWithMockedSession;
    private AsmMutabilityChecker checkerWithRealSession;
    
    private AnalysisResult result;
    private AnalysisResult unusedAnalysisResult = unusedAnalysisResult("some.class.Name", NOT_IMMUTABLE);
    
    private Dotted mutableExample = Dotted.fromClass(MutableExample.class);

    private MutableTypeToFieldChecker checkerWithRealAnalysisSession() {
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        return new MutableTypeToFieldChecker(
                info, 
                new MutableTypeInformation(testAnalysisSession(), ConfigurationBuilder.NO_CONFIGURATION), 
                testingVerifierFactory());
    }


    @Before
    public void setUpWithMockSession() {
        session = mock(AnalysisSession.class);
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        checkerWithMockedSession = new MutableTypeToFieldChecker(
                info, 
                new MutableTypeInformation(session, ConfigurationBuilder.NO_CONFIGURATION), 
                testingVerifierFactory());
    }
    

    @Before
    public void setUpWithRealSession() {
        SessionCheckerRunner runner = new SessionCheckerRunner(TestUtil.testAnalysisSession(), createWithCurrentClasspath(FAIL_FAST));
        TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
        checkerWithRealSession = new MutableTypeToFieldChecker(
                typeInfo, 
                new MutableTypeInformation(testAnalysisSession(), ConfigurationBuilder.NO_CONFIGURATION), 
                testingVerifierFactory());
    }

    @Test
    public void requestsMutableStatusOfPublishedField() throws Exception {
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(unusedAnalysisResult);
        
        runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        verify(session).resultFor(mutableExample);
    }

    @Test
    public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
        String unusedMessage = "";
        AnalysisResult mutableResult = AnalysisResult.analysisResult(
                "", 
                NOT_IMMUTABLE, 
                newMutableReasonDetail(unusedMessage, unusedCodeLocation(), ABSTRACT_COLLECTION_TYPE_TO_FIELD));

        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(mutableResult);
        
        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        assertThat(result, areNotImmutable());
        assertThat(checkerWithMockedSession, hasReasons(MUTABLE_TYPE_TO_FIELD));
    }

    @Test
    public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(unusedAnalysisResult);

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
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(unusedAnalysisResult);
        
        runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);
        FieldLocation codeLocation = (FieldLocation) checkerWithMockedSession.reasons().iterator().next().codeLocation();

        assertThat(codeLocation.typeName(), is(MutableByHavingMutableFieldAssigned.class.getName()));
        assertThat(codeLocation.fieldName(), is("mutableField"));
    }

    @Test
    public void codeLocationIsFieldLocationForArrayField() throws Exception {
        runChecker(checkerWithMockedSession, MutableByHavingArrayTypeAsField.class);
        FieldLocation codeLocation = (FieldLocation) checkerWithMockedSession.reasons().iterator().next().codeLocation();

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
        assertThat(reasonDetail.message(), is("Attempts to wrap mutable collection type without safely performing a copy first."));
    }
    
    @Test
    public void doesNotAllowStoringCopiedCollectionIntoLocalVariableThatCouldEscape() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();
        
        result = runChecker(checkerWithRealSession, StoresCopiedCollectionIntoLocalVariableBeforeWrapping.class);
        
        assertThat(result, areNotImmutable());
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        
        assertEquals(ABSTRACT_COLLECTION_TYPE_TO_FIELD, reasonDetail.reason());
        assertThat(reasonDetail.message(), is("Attempts to wrap mutable collection type without safely performing a copy first."));
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
        assertThat(reasonDetail.message(), is("Attempts to wrap mutable collection type without safely performing a copy first."));
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

}
