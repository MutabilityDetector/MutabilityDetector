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

package org.mutabilitydetector.checkers.info;

import static org.mutabilitydetector.checkers.util.TypeStructureInformationChecker.newChecker;
import static org.mutabilitydetector.locations.ClassIdentifier.forClass;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AsmSessionCheckerRunner;
import org.mutabilitydetector.checkers.util.TypeStructureInformationChecker;
import org.mutabilitydetector.locations.Dotted;

public final class TypeStructureInformation implements AnalysisInformation {

    private final AsmSessionCheckerRunner sessionCheckerRunner;
    private final Map<Dotted, Boolean> isAbstractMap = new HashMap<Dotted, Boolean>();
    private final Map<Dotted, Boolean> isInterfaceMap = new HashMap<Dotted, Boolean>();

    public TypeStructureInformation(AsmSessionCheckerRunner sessionCheckerRunner) {
        this.sessionCheckerRunner = sessionCheckerRunner;
    }

    private void runCheckerAndPopulateResultMaps(Dotted className) {
        TypeStructureInformationChecker checker = newChecker(className);
        sessionCheckerRunner.run(checker, forClass(className));

        isAbstractMap.put(className, checker.isAbstract());
        isInterfaceMap.put(className, checker.isInterface());
    }

    private Boolean getResultFrom(Dotted className, Map<Dotted, Boolean> resultMap) {
        if (!resultMap.containsKey(className)) {
            runCheckerAndPopulateResultMaps(className);
        }
        return resultMap.get(className);
    }

    public boolean isTypeAbstract(Dotted className) {
        return getResultFrom(className, isAbstractMap);
    }

    public boolean isTypeInterface(Dotted className) {
        return getResultFrom(className, isInterfaceMap);
    }
}
