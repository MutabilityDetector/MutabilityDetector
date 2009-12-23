/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.checkers;

import java.util.Collection;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.objectweb.asm.ClassVisitor;


public interface IMutabilityChecker extends ClassVisitor {

	public Collection<String> reasons();

	public IsImmutable result();
	
}
