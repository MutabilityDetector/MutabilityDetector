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
package org.mutabilitydetector;

import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.PRIVATE_METHOD_INVOCATION;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.ArrayFieldMutabilityChecker;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CanSubclassChecker;
import org.mutabilitydetector.checkers.EscapedThisReferenceChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.NonFinalFieldChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;

public final class MutabilityCheckerFactory {

    public Iterable<AsmMutabilityChecker> createInstances(AnalysisSession analysisSession, AnalysisDatabase database, AsmVerifierFactory verifierFactory) {
        Collection<AsmMutabilityChecker> checkers = new ArrayList<AsmMutabilityChecker>();
        checkers.add(new CanSubclassChecker());
        checkers.add(new NonFinalFieldChecker());
        checkers.add(new PublishedNonFinalFieldChecker());
        checkers.add(newSetterMethodChecker(database.requestInformation(PRIVATE_METHOD_INVOCATION), verifierFactory));
        checkers.add(new MutableTypeToFieldChecker(database.requestInformation(TYPE_STRUCTURE), 
                                                   new MutableTypeInformation(analysisSession), 
                                                   verifierFactory));
        checkers.add(new InherentTypeMutabilityChecker());
        checkers.add(new ArrayFieldMutabilityChecker());
        checkers.add(new EscapedThisReferenceChecker());
        // checkers.add(new InheritedMutabilityChecker(analysisSession));
        // checkers.add(new NoCopyOfFieldChecker()); - or whatever it's going to be called.
        return Collections.unmodifiableCollection(checkers);
    }

}
