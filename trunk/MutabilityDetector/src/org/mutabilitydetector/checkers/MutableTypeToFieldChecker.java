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

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.asmoverride.CustomClassLoadingSimpleVerifier;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;


public class MutableTypeToFieldChecker extends AbstractMutabilityChecker {

	private final IAnalysisSession analysisSession;

	public MutableTypeToFieldChecker(IAnalysisSession analysisSession) {
		this.analysisSession = analysisSession;
	}

	private String owner;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.owner = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return super.visitField(access, name, desc, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new AssignMutableTypeToFieldChecker(owner, access, name, desc, signature, exceptions);
	}

	class AssignMutableTypeToFieldChecker extends FieldAssignmentVisitor {

		private final String owner;

		public AssignMutableTypeToFieldChecker(String owner, int access, String name, String desc, String signature,
				String[] exceptions) {
			super(access, name, desc, signature, exceptions);
			this.owner = owner;
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
			Analyzer a = new Analyzer(new CustomClassLoadingSimpleVerifier());
			Frame[] frames;
			try {
				frames = a.analyze(owner, this);

				for (FieldInsnNode fieldInsnNode : fieldAssignments) {
					Frame assignmentFrame = frames[instructions.indexOf(fieldInsnNode)];
					int stackSlot = assignmentFrame.getStackSize() - 1;
					BasicValue stackValue = (BasicValue) assignmentFrame.getStack(stackSlot);
					if (stackValue == null || "Lnull;".equals(stackValue.getType().toString())) {
						continue;
					}
					checkIfClassIsMutable(fieldInsnNode.name, stackValue.getType());
				}
			} catch (AnalyzerException forwarded) {
				throw new RuntimeException(forwarded);
			}
		}

		private void checkIfClassIsMutable(String name, Type type) {
			int sort = type.getSort();
			if (sort != Type.OBJECT) {
				return;
			}
			String dottedClassName = dottedClassName(type);
			IsImmutable isImmutable = analysisSession.isImmutable(dottedClassName);
			if (!isImmutable.equals(DEFINITELY)) {
				reasons.add("Field [" + name + "] can have a mutable type (" + dottedClassName + ") "
						+ "assigned to it.");
				result = DEFINITELY_NOT;
			}
		}
	}

	static class FieldInfo {
		final String name;
		final Type type;

		public FieldInfo(String name, Type type) {
			this.name = name;
			this.type = type;
		}
	}

}
