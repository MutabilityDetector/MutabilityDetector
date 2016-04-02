package org.mutabilitydetector.benchmarks.cyclic;

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


import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.CyclicReferences;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.CodeLocationFactory;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collections;

import static org.mutabilitydetector.TestUtil.*;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

public class CyclicReferenceAnalysisOfBothImmutableTest {

    @Test
    public void immutableClassesWithCyclicReferencesAreAnalysedCorrectly() throws Exception {
        AnalysisSession session = TestUtil.testAnalysisSession();
        session.resultFor(Dotted.fromClass(ImmutableClassA.class));
    }

    @Test
    public void immutableClassWithFieldsWithCyclicReferencesAreAnalysedCorrectly() throws Exception {
        AnalysisSession session = TestUtil.testAnalysisSession();
        session.resultFor(Dotted.fromClass(CyclicReferenceClasses.class));
    }

    @Test
    public void mutableFieldCheckerHandlesCyclicReferences() throws Exception {
        AnalysisSession session = TestUtil.testAnalysisSession();
        TypeStructureInformation information = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        AsmMutabilityChecker mutableFieldChecker = new MutableTypeToFieldChecker(
                information, 
                new MutableTypeInformation(
                        session,
                        Configurations.NO_CONFIGURATION,
                        CyclicReferences.newEmptyMutableInstance()),
                testingVerifierFactory(),
                Collections.<Dotted>emptySet(),
                AnalysisInProgress.noAnalysisUnderway(), CodeLocationFactory.create());

        runChecker(mutableFieldChecker, ImmutableClassA.class);
    }

}
