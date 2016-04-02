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


import com.google.common.collect.ImmutableSet;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.AnalysisInProgress;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.settermethod.SetterMethodChecker;
import org.mutabilitydetector.locations.CodeLocationFactory;
import org.mutabilitydetector.locations.Dotted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice.LAZY_INITIALISATION_ANALYSIS;
import static org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice.NAIVE_PUT_FIELD_ANALYSIS;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.*;

public final class MutabilityCheckerFactory {

    private final ReassignedFieldAnalysisChoice analysisChoice;
    private final Set<Dotted> immutableContainerClasses;

    public MutabilityCheckerFactory(ReassignedFieldAnalysisChoice analysisChoice, Set<Dotted> immutableContainerClasses) {
        this.analysisChoice = analysisChoice;
        this.immutableContainerClasses = immutableContainerClasses;
    }

    public Iterable<AsmMutabilityChecker> createInstances(
            AnalysisDatabase database,
            AsmVerifierFactory verifierFactory,
            MutableTypeInformation mutableTypeInformation,
            AnalysisInProgress analysisInProgress) {
        CodeLocationFactory codeLocationFactory = CodeLocationFactory.createWithLineNumbersInfo(database.requestInformation(LINE_NUMBERS));
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

        checkers.add(new MutableTypeToFieldChecker(
                database.requestInformation(TYPE_STRUCTURE),
                mutableTypeInformation,
                verifierFactory,
                immutableContainerClasses,
                analysisInProgress,
                codeLocationFactory));

        checkers.add(new InherentTypeMutabilityChecker());
        checkers.add(new ArrayFieldMutabilityChecker(codeLocationFactory));
        checkers.add(new EscapedThisReferenceChecker());
        checkers.add(new CollectionWithMutableElementTypeToFieldChecker(
                mutableTypeInformation,
                verifierFactory,
                ImmutableSet.copyOf(immutableContainerClasses),
                analysisInProgress));
        // checkers.add(new InheritedMutabilityChecker(analysisSession));
        // checkers.add(new NoCopyOfFieldChecker()); - or whatever it's going to be called.
        return Collections.unmodifiableCollection(checkers);
    }

    public enum ReassignedFieldAnalysisChoice {
        NAIVE_PUT_FIELD_ANALYSIS,
        LAZY_INITIALISATION_ANALYSIS
    }

}
