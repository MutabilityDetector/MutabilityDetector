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
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.DefaultConfiguration.NO_CONFIGURATION;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testAnalysisSession;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.TestUtil.unusedAnalysisResult;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
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
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom.ListFieldFromUnmodifiableArrayAsList;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom.SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom.SafelyCopiedMapGenericOnMutableTypeForKey;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom.StoresCopiedCollectionAsObjectAndIterable;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom.StoresCopiedCollectionIntoLocalVariableBeforeWrapping;
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
                new MutableTypeInformation(testAnalysisSession(), NO_CONFIGURATION), 
                testingVerifierFactory());
    }


    @Before
    public void setUpWithMockSession() {
        session = mock(AnalysisSession.class);
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        checkerWithMockedSession = new MutableTypeToFieldChecker(
                info, 
                new MutableTypeInformation(session, NO_CONFIGURATION), 
                testingVerifierFactory());
    }
    

    @Before
    public void setUpWithRealSession() {
        SessionCheckerRunner runner = new SessionCheckerRunner(createWithCurrentClassPath(), createWithCurrentClasspath());
        TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
        checkerWithRealSession = new MutableTypeToFieldChecker(
                typeInfo, 
                new MutableTypeInformation(testAnalysisSession(), NO_CONFIGURATION), 
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
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(unusedAnalysisResult);
        
        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        assertThat(checkerWithMockedSession, hasReasons());
        assertThat(result, areNotImmutable());
    }

    @Test
    public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(unusedAnalysisResult);

        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);

        assertThat(checkerWithMockedSession, hasReasons());
        assertThat(result, areNotImmutable());
        
    }
    
    @Test
    public void isMutableWhenCircularReferenceCheckingForFieldBeingMutable() throws Exception {
        when(session.getResults()).thenReturn(Collections.<AnalysisResult>emptyList());
        when(session.resultFor(mutableExample)).thenReturn(null);
        
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        MutableTypeInformation mutableTypeInfo = new MutableTypeInformation(session, NO_CONFIGURATION);
        checkerWithMockedSession = new MutableTypeToFieldChecker(info, mutableTypeInfo, testingVerifierFactory());
        

        result = runChecker(checkerWithMockedSession, MutableByHavingMutableFieldAssigned.class);
        assertThat(result, areNotImmutable());
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
    public void safelyWrappedListsAreStillMutableIfTheTypeOfListElementsIsMutable() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();
        
        result = runChecker(checkerWithRealSession, SafelyCopiedMapGenericOnMutableTypeForKey.class);
        assertThat(result, areNotImmutable());
        
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        
        assertEquals(COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE, reasonDetail.reason());
    }

    @Test
    public void descriptionOfCollectionWithMutableElementType() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();
        
        result = runChecker(checkerWithRealSession, SafelyCopiedMapGenericOnMutableTypeForKey.class);
        assertThat(result, areNotImmutable());
        
        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        
        assertThat(reasonDetail.message(), 
                is("Field can have collection with mutable element type " +
                        "(java.util.Map<java.util.Date, org.mutabilitydetector.benchmarks.ImmutableExample>) assigned to it."));
    }

    @Test
    public void safelyWrappedListsAreStillMutableIfTheTypeOfListElementsIsMutable_worksWhenCollectionFieldIsOneOfMany() throws Exception {
        checkerWithRealSession = checkerWithRealAnalysisSession();
        
        result = runChecker(checkerWithRealSession, SafelyCopiedMapGenericOnImmutableTypeForKey_ManyFields.class);
        assertThat(result, areImmutable());
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
