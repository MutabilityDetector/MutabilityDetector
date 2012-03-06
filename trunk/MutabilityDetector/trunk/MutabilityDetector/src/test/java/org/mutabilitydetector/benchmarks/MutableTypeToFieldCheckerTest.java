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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.IAnalysisSession.RequestedAnalysis.complete;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestMatchers.hasNoReasons;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testingAnalysisClassLoader;
import static org.mutabilitydetector.TestUtil.unusedAnalysisResult;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableButHasUnmodifiedArrayAsField;
import org.mutabilitydetector.benchmarks.mutabletofield.array.ImmutableWhenArrayFieldIsStatic;
import org.mutabilitydetector.benchmarks.mutabletofield.array.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.locations.FieldLocation;

public class MutableTypeToFieldCheckerTest {

    @Rule public MethodRule rule = new IncorrectAnalysisRule();
    
    private IAnalysisSession session;
    private MutableTypeToFieldChecker checker;
    private AnalysisResult result;
    private AnalysisResult unusedAnalysisResult = unusedAnalysisResult("some.class.Name", NOT_IMMUTABLE);

    @Before
    public void setUp() {
        session = mock(IAnalysisSession.class);
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        checker = new MutableTypeToFieldChecker(info, new MutableTypeInformation(session), testingAnalysisClassLoader());
    }

    @Test
    public void requestsMutableStatusOfPublishedField() throws Exception {
        when(session.resultFor(MutableExample.class.getCanonicalName())).thenReturn(complete(unusedAnalysisResult));
        runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        verify(session).resultFor(MutableExample.class.getCanonicalName());
    }

    @Test
    public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
        when(session.resultFor(MutableExample.class.getCanonicalName())).thenReturn(complete(unusedAnalysisResult));
        result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        assertThat(checker, hasReasons());
        assertThat(result, areNotImmutable());
    }

    @Test
    public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
        when(session.resultFor(MutableExample.class.getCanonicalName())).thenReturn(complete(unusedAnalysisResult));

        result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        assertThat(checker, hasReasons());
        assertThat(result, areNotImmutable());
        
    }
    
    @Test
    public void isMutableWhenCircularReferenceCheckingForFieldBeingMutable() throws Exception {
        when(session.resultFor(MutableExample.class.getName())).thenReturn(RequestedAnalysis.incomplete());
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        MutableTypeInformation mutableTypeInfo = new MutableTypeInformation(session);
        checker = new MutableTypeToFieldChecker(info, mutableTypeInfo, testingAnalysisClassLoader());
        

        result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);
        assertThat(result, areNotImmutable());
    }
    
    @Test
    public void instanceFieldWhichHasAMutatedArrayIsMutable() throws Exception {
        result = runChecker(checker, MutableByHavingArrayTypeAsField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    @FalsePositive("Array field is final, is never published or modified.")
    public void instanceFieldWhichHasAFinalUnmodifiedArrayIsImmutable() throws Exception {
        result = runChecker(checker, ImmutableButHasUnmodifiedArrayAsField.class);
        assertThat(result, areImmutable());
    }

    @Test
    public void staticFieldWhichHasAMutatedArrayIsImmutable() throws Exception {
        result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);
        assertThat(result, areImmutable());
    }

    @Test
    public void doesNotRaiseErrorForAbstractTypeSinceThisIsRaisedByAbstractTypeToFieldChecker() throws Exception {
        when(session.resultFor(AbstractStringContainer.class.getName())).thenReturn(complete(unusedAnalysisResult));
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        assertThat(checker, hasNoReasons());
        assertThat(result, areImmutable());
    }

    @Test
    public void codeLocationIsFieldLocation() throws Exception {
        when(session.resultFor(MutableExample.class.getCanonicalName())).thenReturn(complete(unusedAnalysisResult));
        runChecker(checker, MutableByHavingMutableFieldAssigned.class);
        FieldLocation codeLocation = (FieldLocation) checker.reasons().iterator().next().codeLocation();

        assertThat(codeLocation.typeName(), is(MutableByHavingMutableFieldAssigned.class.getName()));
        assertThat(codeLocation.fieldName(), is("mutableField"));
    }

    @Test
    public void codeLocationIsFieldLocationForArrayField() throws Exception {
        runChecker(checker, MutableByHavingArrayTypeAsField.class);
        FieldLocation codeLocation = (FieldLocation) checker.reasons().iterator().next().codeLocation();

        assertThat(codeLocation.typeName(), is(MutableByHavingArrayTypeAsField.class.getName()));
        assertThat(codeLocation.fieldName(), is("names"));
    }

}
