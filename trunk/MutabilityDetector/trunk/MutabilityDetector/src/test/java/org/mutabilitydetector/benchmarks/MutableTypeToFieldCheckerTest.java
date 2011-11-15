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
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestMatchers.hasNoReasons;
import static org.mutabilitydetector.TestMatchers.hasReasons;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.unusedAnalysisResult;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.ImmutableButHasUnmodifiedArrayAsField;
import org.mutabilitydetector.benchmarks.mutabletofield.ImmutableWhenArrayFieldIsStatic;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.FieldLocation;

public class MutableTypeToFieldCheckerTest {

    private IAnalysisSession mockSession;
    private MutableTypeToFieldChecker checker;
    private AnalysisResult result;
    private AnalysisResult unusedAnalysisResult = unusedAnalysisResult("some.class.Name", NOT_IMMUTABLE);

    @Before
    public void setUp() {
        mockSession = mock(IAnalysisSession.class);
        TypeStructureInformation info = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        checker = new MutableTypeToFieldChecker(mockSession, info);
    }

    @Test
    public void requestsMutableStatusOfPublishedField() throws Exception {
        when(mockSession.resultFor(MutableExample.class.getCanonicalName())).thenReturn(unusedAnalysisResult);
        runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        verify(mockSession).resultFor(MutableExample.class.getCanonicalName());
    }

    @Test
    public void failsCheckWhenMutableTypeIsAssignedToField() throws Exception {
        when(mockSession.resultFor(MutableExample.class.getCanonicalName())).thenReturn(unusedAnalysisResult);
        result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        assertThat(checker, hasReasons());
        assertNotImmutable(result);
    }

    @Test
    public void failsCheckIfAnyFieldsHaveMutableAssignedToThem() throws Exception {
        when(mockSession.resultFor(MutableExample.class.getCanonicalName())).thenReturn(unusedAnalysisResult);

        result = runChecker(checker, MutableByHavingMutableFieldAssigned.class);

        assertThat(checker, hasReasons());
        assertNotImmutable(result);
    }

    @Test
    public void instanceFieldWhichHasAMutatedArrayIsMutable() throws Exception {
        result = runChecker(checker, MutableByHavingArrayTypeAsField.class);
        assertNotImmutable(result);
    }

    @Ignore
    @Test
    public void instanceFieldWhichHasAFinalUnmodifiedArrayIsImmutable() throws Exception {
        result = runChecker(checker, ImmutableButHasUnmodifiedArrayAsField.class);
        assertImmutable(result);
    }

    @Test
    public void staticFieldWhichHasAMutatedArrayIsImmutable() throws Exception {
        result = runChecker(checker, ImmutableWhenArrayFieldIsStatic.class);
        assertImmutable(result);
    }

    @Test
    public void doesNotRaiseErrorForAbstractTypeSinceThisIsRaisedByAbstractTypeToFieldChecker() throws Exception {
        when(mockSession.resultFor(AbstractStringContainer.class.getName())).thenReturn(unusedAnalysisResult);
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        assertThat(checker, hasNoReasons());
        assertImmutable(result);
    }

    @Test
    public void codeLocationIsFieldLocation() throws Exception {
        when(mockSession.resultFor(MutableExample.class.getCanonicalName())).thenReturn(unusedAnalysisResult);
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
