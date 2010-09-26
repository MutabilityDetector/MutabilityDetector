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

import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.checkers.util.PrivateMethodInvocationChecker;

public class PrivateMethodInvocationInfo implements AnalysisInformation {

	private final CheckerRunner checkerRunner;

	public PrivateMethodInvocationInfo(CheckerRunner checkerRunner) {
		this.checkerRunner = checkerRunner;
		
	}

	public boolean isOnlyCalledFromConstructor(MethodIdentifier forMethod) {
		PrivateMethodInvocationChecker checker = PrivateMethodInvocationChecker.newInstance();
		Class<?> toCheck = getClassToCheck(forMethod.dottedClassName());
		
		checkerRunner.run(checker, toCheck);
		
		return checker.isPrivateMethodCalledOnlyFromConstructor(forMethod.methodDescriptor());
	}

	private Class<?> getClassToCheck(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load " + className + ".", e);
		}
	}

}
