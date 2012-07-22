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
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.AbstractTypeToFieldChecker.newAbstractTypeToFieldChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.benchmarks.mutabletofield.WrapWithUnmodifiableListWithoutCopyingFirst;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.FieldLocation;

public class AbstractTypeToFieldCheckerTest {

    AsmMutabilityChecker checker;
    AnalysisResult result;

    @Before
    public void setUp() {
        SessionCheckerRunner runner = new SessionCheckerRunner(createWithCurrentClassPath(),
                createWithCurrentClasspath());
        TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
        checker = newAbstractTypeToFieldChecker(typeInfo, TestUtil.testingVerifierFactory());
    }

    @Test
    public void testImmutableExamplePassesCheck() throws Exception {
        result = runChecker(checker, ImmutableExample.class);
        assertThat(result, areImmutable());
        assertEquals(result.reasons.size(), 0);
    }

    @Test
    public void testMutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByAssigningInterfaceToField.class);

        assertThat(result, areNotImmutable());
    }

    @Test
    public void testMutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);
        assertThat(result, areNotImmutable());
    }

    @Test
    public void classLocationOfResultIsSet() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        assertThat(result.reasons.size(), is(1));

        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        String typeName = reasonDetail.codeLocation().typeName();
        assertThat(typeName, is(MutableByAssigningAbstractTypeToField.class.getName()));
    }

    @Test
    public void reasonCreatedByCheckerIncludesMessagePointingToAbstractType() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);
        Class<?> abstractTypeAssigned = AbstractStringContainer.class;

        assertThat(result.reasons.size(), is(1));

        MutableReasonDetail reasonDetail = result.reasons.iterator().next();
        assertThat(reasonDetail.message(), containsString(abstractTypeAssigned.getName()));
    }

    @Test
    public void reasonHasCodeLocationPointingAtFieldWhichIsOfAnAbstractType() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        FieldLocation fieldLocation = (FieldLocation) result.reasons.iterator().next().codeLocation();

        assertThat(fieldLocation.typeName(), is(MutableByAssigningAbstractTypeToField.class.getName()));
        assertThat(fieldLocation.fieldName(), is("nameContainer"));
    }
    
    @Ignore
    @Test
    public void allowsCopyingAndWrappingInUmodifiableCollectionTypeIdiom() throws Exception {
        assertThat(runChecker(checker, CopyListIntoNewArrayListAndUnmodifiableListIdiom.class), 
                   areImmutable());
    }

    @Test
    public void raisesAnErrorIfWrappedInUnmodifiableCollectionTypeButIsNotCopiedFirst() throws Exception {
        assertThat(runChecker(checker, WrapWithUnmodifiableListWithoutCopyingFirst.class), 
                areNotImmutable());
    }

}
