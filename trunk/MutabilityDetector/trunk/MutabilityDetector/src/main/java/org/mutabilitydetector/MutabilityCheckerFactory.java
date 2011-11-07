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
package org.mutabilitydetector;

import static org.mutabilitydetector.checkers.AbstractTypeToFieldChecker.newAbstractTypeToFieldChecker;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.PRIVATE_METHOD_INVOCATION;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mutabilitydetector.benchmarks.finalfield.NonFinalFieldChecker;
import org.mutabilitydetector.checkers.FinalClassChecker;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;
import org.mutabilitydetector.checkers.MutableTypeToFieldChecker;
import org.mutabilitydetector.checkers.PublishedNonFinalFieldChecker;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;

public class MutabilityCheckerFactory implements IMutabilityCheckerFactory {

    @Override
    public Collection<IMutabilityChecker> createInstances(IAnalysisSession analysisSession) {
        AnalysisDatabase database = analysisSession.analysisDatabase();

        Collection<IMutabilityChecker> checkers = new ArrayList<IMutabilityChecker>();
        checkers.add(new FinalClassChecker());
        checkers.add(newAbstractTypeToFieldChecker(database.requestInformation(TYPE_STRUCTURE)));
        checkers.add(new NonFinalFieldChecker());
        checkers.add(new PublishedNonFinalFieldChecker());
        checkers.add(newSetterMethodChecker(database.requestInformation(PRIVATE_METHOD_INVOCATION)));
        checkers.add(new MutableTypeToFieldChecker(analysisSession, database.requestInformation(TYPE_STRUCTURE)));
        checkers.add(new InherentTypeMutabilityChecker());
        // checkers.add(new InheritedMutabilityChecker(analysisSession));
        // checkers.add(new NoCopyOfFieldChecker()); - or whatever it's going to be called.
        return Collections.unmodifiableCollection(checkers);
    }

}
