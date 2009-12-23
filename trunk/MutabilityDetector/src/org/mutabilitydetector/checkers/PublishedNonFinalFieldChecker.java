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


import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.objectweb.asm.FieldVisitor;

public class PublishedNonFinalFieldChecker extends AbstractMutabilityChecker {

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (!isPrivate(access)) {
			if (!isFinal(access)) {
				reasons.add("Field [" + name + "] is visible outwith this class, and is not declared final.");
				result = DEFINITELY_NOT;
			}
		}
		return super.visitField(access, name, desc, signature, value);
	}
}
