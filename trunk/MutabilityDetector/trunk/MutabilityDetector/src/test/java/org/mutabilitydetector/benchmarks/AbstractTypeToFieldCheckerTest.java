/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.TestUtil.runChecker;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.benchmarks.abstracttofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.abstracttofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.abstracttofield.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.checkers.AbstractTypeToFieldChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.FieldLocation;

public class AbstractTypeToFieldCheckerTest {

    IMutabilityChecker checker;
    AnalysisResult result;

    @Before
    public void setUp() {
        SessionCheckerRunner runner = new SessionCheckerRunner(createWithCurrentClassPath(),
                createWithCurrentClasspath());
        TypeStructureInformation typeInfo = new TypeStructureInformation(runner);
        checker = new AbstractTypeToFieldChecker(typeInfo);
    }

    @Test
    public void testImmutableExamplePassesCheck() throws Exception {
        result = runChecker(checker, ImmutableExample.class);
        assertImmutable(result);
        assertEquals(result.reasons.size(), 0);
    }

    @Test
    public void testMutableByAssigningInterfaceTypeToFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByAssigningInterfaceToField.class);

        assertDefinitelyNotImmutable(result);
    }

    @Test
    public void testMutableByAssigningAbstractClassToFieldFailsCheck() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);
        assertDefinitelyNotImmutable(result);
    }

    @Test
    public void classLocationOfResultIsSet() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        assertThat(result.reasons.size(), is(1));

        CheckerReasonDetail reasonDetail = result.reasons.iterator().next();
        String typeName = reasonDetail.codeLocation().typeName();
        assertThat(typeName, is(MutableByAssigningAbstractTypeToField.class.getName()));
    }

    @Test
    public void reasonCreatedByCheckerIncludesMessagePointingToAbstractType() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);
        Class<?> abstractTypeAssigned = AbstractStringContainer.class;

        assertThat(result.reasons.size(), is(1));

        CheckerReasonDetail reasonDetail = result.reasons.iterator().next();
        assertThat(reasonDetail.message(), containsString(abstractTypeAssigned.getName()));
    }

    @Test
    public void reasonHasCodeLocationPointingAtFieldWhichIsOfAnAbstractType() throws Exception {
        result = runChecker(checker, MutableByAssigningAbstractTypeToField.class);

        FieldLocation fieldLocation = (FieldLocation) result.reasons.iterator().next().codeLocation();

        assertThat(fieldLocation.typeName(), is(MutableByAssigningAbstractTypeToField.class.getName()));
        assertThat(fieldLocation.fieldName(), is("nameContainer"));
    }

}
