/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.info;

import static org.mutabilitydetector.checkers.util.TypeStructureInformationChecker.newChecker;
import static org.mutabilitydetector.locations.ClassIdentifier.forClass;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.checkers.util.TypeStructureInformationChecker;
import org.mutabilitydetector.locations.Dotted;

public class TypeStructureInformation implements AnalysisInformation {

    private final ISessionCheckerRunner sessionCheckerRunner;
    private final Map<Dotted, Boolean> isAbstractMap = new HashMap<Dotted, Boolean>();
    private final Map<Dotted, Boolean> isInterfaceMap = new HashMap<Dotted, Boolean>();

    public TypeStructureInformation(ISessionCheckerRunner sessionCheckerRunner) {
        this.sessionCheckerRunner = sessionCheckerRunner;
    }

    private void runCheckerAndPopulateResultMaps(Dotted className) {
        TypeStructureInformationChecker checker = newChecker(className);
        sessionCheckerRunner.run(checker, forClass(className));

        isAbstractMap.put(className, checker.isAbstract());
        isInterfaceMap.put(className, checker.isInterface());
    }

    private Boolean getResultFrom(Dotted className, Map<Dotted, Boolean> resultMap) {
        Boolean result = false;
        if (resultMap.containsKey(className)) {
            result = resultMap.get(className);
        } else {
            runCheckerAndPopulateResultMaps(className);
            result = resultMap.get(className);
        }
        return result;
    }

    public boolean isTypeAbstract(Dotted className) {
        return getResultFrom(className, isAbstractMap);
    }

    public boolean isTypeInterface(Dotted className) {
        return getResultFrom(className, isInterfaceMap);
    }
}
