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
package org.mutabilitydetector.benchmarks.circular;

import static org.mutabilitydetector.DefaultConfiguration.NO_CONFIGURATION;
import static org.mutabilitydetector.TestUtil.analysisDatabase;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.TestUtil.testingVerifierFactory;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.Dotted;

public class CircularReferenceAnalysisOfBothImmutableTest {

    @Test
    public void immutableClassesWithCircularReferencesAreAnalysedCorrectly() throws Exception {
        AnalysisSession session = createWithCurrentClassPath();
        session.resultFor(Dotted.fromClass(ImmutableClassA.class));
    }

    @Test
    public void immutableClassWithFieldsWithCircularReferencesAreAnalysedCorrectly() throws Exception {
        AnalysisSession session = createWithCurrentClassPath();
        session.resultFor(Dotted.fromClass(CircularReferenceClasses.class));
    }

    @Test
    public void mutableFieldCheckerHandlesCircularReferences() throws Exception {
        AnalysisSession session = createWithCurrentClassPath();
        TypeStructureInformation information = analysisDatabase().requestInformation(TYPE_STRUCTURE);
        AsmMutabilityChecker mutableFieldChecker = new MutableTypeToFieldChecker(
                information, 
                new MutableTypeInformation(session, NO_CONFIGURATION), 
                testingVerifierFactory());

        runChecker(mutableFieldChecker, ImmutableClassA.class);
    }

}
