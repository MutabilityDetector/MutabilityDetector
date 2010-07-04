/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.MutabilityReason;
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
			switch(sort) {
			case Type.OBJECT:
				String dottedClassName = dottedClassName(type);
				IsImmutable isImmutable = analysisSession.isImmutable(dottedClassName);
				if (!isImmutable.equals(DEFINITELY)) {
					addResult("Field [" + name + "] can have a mutable type (" + dottedClassName + ") "
							+ "assigned to it.", null, MutabilityReason.MUTABLE_TYPE_TO_FIELD);
					result = DEFINITELY_NOT;
				}
				break;
			case Type.ARRAY:
				addResult("Field [" + name + "] can have a mutable type (a primitive array) "
						+ "assigned to it.", null, MutabilityReason.MUTABLE_TYPE_TO_FIELD);
				result = DEFINITELY_NOT;
				break;
			default:
				return;
			}
		}
	}
}
