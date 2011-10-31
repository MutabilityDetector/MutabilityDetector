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

import static org.mutabilitydetector.locations.ClassIdentifier.forClass;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.checkers.util.PrivateMethodInvocationChecker;
import org.mutabilitydetector.locations.Dotted;

public class PrivateMethodInvocationInformation implements AnalysisInformation {

    private final Map<Dotted, PrivateMethodInvocationChecker> checkerCache = new HashMap<Dotted, PrivateMethodInvocationChecker>();
    private final ISessionCheckerRunner sessionCheckerRunner;

    public PrivateMethodInvocationInformation(ISessionCheckerRunner sessionCheckerRunner) {
        this.sessionCheckerRunner = sessionCheckerRunner;

    }

    public boolean isOnlyCalledFromConstructor(MethodIdentifier forMethod) {
        PrivateMethodInvocationChecker checker = null;
        if (checkerCache.containsKey(forMethod.dottedClassName())) {
            checker = checkerCache.get(forMethod.dottedClassName());

        } else {
            checker = PrivateMethodInvocationChecker.newChecker();
            sessionCheckerRunner.run(checker, forClass(forMethod.dottedClassName()));
            checkerCache.put(forMethod.dottedClassName(), checker);
        }
        return result(checker, forMethod);
    }

    private boolean result(PrivateMethodInvocationChecker checker, MethodIdentifier forMethod) {
        return checker.isPrivateMethodCalledOnlyFromConstructor(forMethod.methodDescriptor());
    }

}
