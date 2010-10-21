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
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class PrivateMethodInvocationChecker extends AbstractMutabilityChecker {

	/**
	 * @see #newChecker()
	 */
	private PrivateMethodInvocationChecker() {}
	
	public static PrivateMethodInvocationChecker newChecker() {
		return new PrivateMethodInvocationChecker();
	}
	
	private Map<MethodIdentifier, Boolean> privateMethodCalledFromConstructorMap = new HashMap<MethodIdentifier, Boolean>();
	
	@Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		super.visitMethod(access, name, desc, signature, exceptions);
		
		boolean isPrivate = method(access).is(Opcodes.ACC_PRIVATE);
		String methodDescriptor = makeMethodDescriptor(name, desc);
		privateMethodCalledFromConstructorMap.put(makeMethodIdentifier(methodDescriptor), isPrivate);
		
		return new MethodInvocationVisitor(access, name, desc, signature, exceptions);
	}

	private String makeMethodDescriptor(String methodName, String methodDesc) {
		return format("%s:%s", methodName, methodDesc);
	}
	private MethodIdentifier makeMethodIdentifier(String desc) {
		return forMethod(slashed(ownerClass), desc);
	}

	public boolean isPrivateMethodCalledOnlyFromConstructor(String methodDescriptor) {
		MethodIdentifier identifier = makeMethodIdentifier(methodDescriptor);
		if(privateMethodCalledFromConstructorMap.containsKey(identifier)) {
			return privateMethodCalledFromConstructorMap.get(identifier);
		}
		
		String message = format("Could not find method descriptor %s. Available descriptors are: %n%s", 
				identifier, privateMethodCalledFromConstructorMap.keySet().toString());
		throw new MutabilityAnalysisException(message);
	}
	
	private class MethodInvocationVisitor extends MethodNode {

		public MethodInvocationVisitor(int access, String name, String desc, String signature, String[] exceptions) {
			super(access, name, desc, signature, exceptions);
		}
		
		@Override public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if("<init>".equals(this.name)) {
				return;
			}
			
			MethodIdentifier identifier = makeMethodIdentifier(makeMethodDescriptor(name, desc));
			privateMethodCalledFromConstructorMap.put(identifier, false);
		}
	}

}
