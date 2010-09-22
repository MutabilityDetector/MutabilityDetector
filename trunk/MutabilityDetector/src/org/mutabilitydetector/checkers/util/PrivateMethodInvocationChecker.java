/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.util;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

public class PrivateMethodInvocationChecker extends AbstractMutabilityChecker {

	public static PrivateMethodInvocationChecker newInstance() {
		return new PrivateMethodInvocationChecker();
	}
	
	private Map<String, Boolean> privateMethodCalledFromConstructorMap = new HashMap<String, Boolean>();
	
	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		privateMethodCalledFromConstructorMap.put(makeMethodDescriptor(name, desc), true);
		return new MethodInvocationVisitor(access, name, desc, signature, exceptions);
	}

	private String makeMethodDescriptor(String name, String desc) {
		return format("%s:%s", name, desc);
	}

	public boolean isPrivateMethodCalledOnlyFromConstructor(String methodDescriptor) {
		return privateMethodCalledFromConstructorMap.get(methodDescriptor);
	}
	
	private class MethodInvocationVisitor extends MethodNode {

		public MethodInvocationVisitor(int access, String name, String desc, String signature, String[] exceptions) {
			super(access, name, desc, signature, exceptions);
		}
		
		@Override public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if("<init>".equals(this.name)) {
				return;
			}
			
			privateMethodCalledFromConstructorMap.put(makeMethodDescriptor(name, desc), false);
		}
	}

}
