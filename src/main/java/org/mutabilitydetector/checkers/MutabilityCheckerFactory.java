package org.mutabilitydetector.checkers;

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



import static org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice.LAZY_INITIALISATION_ANALYSIS;
import static org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice.NAIVE_PUT_FIELD_ANALYSIS;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.PRIVATE_METHOD_INVOCATION;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.settermethod.SetterMethodChecker;

public final class MutabilityCheckerFactory {
    
    private final ReassignedFieldAnalysisChoice analysisChoice;

    public MutabilityCheckerFactory(ReassignedFieldAnalysisChoice analysisChoice) {
        this.analysisChoice = analysisChoice;
    }

    public Iterable<AsmMutabilityChecker> createInstances(AnalysisDatabase database, AsmVerifierFactory verifierFactory, MutableTypeInformation mutableTypeInformation) {
        Collection<AsmMutabilityChecker> checkers = new ArrayList<AsmMutabilityChecker>();
        checkers.add(new CanSubclassChecker());
        checkers.add(new PublishedNonFinalFieldChecker());
        
        if (analysisChoice == NAIVE_PUT_FIELD_ANALYSIS) {
            checkers.add(new NonFinalFieldChecker());
            checkers.add(OldSetterMethodChecker.newSetterMethodChecker(database.requestInformation(PRIVATE_METHOD_INVOCATION),
                                                                       verifierFactory));
        } else if (analysisChoice == LAZY_INITIALISATION_ANALYSIS) {
            checkers.add(SetterMethodChecker.newInstance(database.requestInformation(PRIVATE_METHOD_INVOCATION)));
        } else {
            throw new IllegalStateException();
        }
        
        checkers.add(new MutableTypeToFieldChecker(database.requestInformation(TYPE_STRUCTURE), 
                                                   mutableTypeInformation, 
                                                   verifierFactory));
        checkers.add(new InherentTypeMutabilityChecker());
        checkers.add(new ArrayFieldMutabilityChecker());
        checkers.add(new EscapedThisReferenceChecker());
        checkers.add(new CollectionWithMutableElementTypeToFieldChecker(mutableTypeInformation, verifierFactory));
        // checkers.add(new InheritedMutabilityChecker(analysisSession));
        // checkers.add(new NoCopyOfFieldChecker()); - or whatever it's going to be called.
        return Collections.unmodifiableCollection(checkers);
    }

    public static enum ReassignedFieldAnalysisChoice { 
        NAIVE_PUT_FIELD_ANALYSIS, 
        LAZY_INITIALISATION_ANALYSIS
    }
    
}
