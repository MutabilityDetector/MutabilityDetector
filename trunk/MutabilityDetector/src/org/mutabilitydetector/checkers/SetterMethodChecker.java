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

import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.visitor.MethodVisitorAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This class checks, for each field, that there is no method available which
 * can change the reference of the field.
 * 
 * The check will pass iff there is no method available to change a reference
 * for ANY field.
 * 
 * @author graham
 * 
 */
public class SetterMethodChecker extends AbstractMutabilityChecker {

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new SetterMethodVisitor(name);
	}

	class SetterMethodVisitor extends MethodVisitorAdapter {

		private final String methodName;

		public SetterMethodVisitor(String name) {
			this.methodName = name;
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if("<init>".equals(this.methodName)) return; // We're not concerned with the constructor 
			
			if(opcode == Opcodes.PUTFIELD) {
				reasons.add("Field [" + name + "] can be reassigned within method [" + this.methodName + "]");
				result = IsImmutable.DEFINITELY_NOT;
			}
		}
		
	}

}
