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


import java.lang.reflect.Modifier;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.asmoverride.CustomClassLoadingSimpleVerifier;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;



public class AbstractTypeToFieldChecker extends AbstractMutabilityChecker {

	String owner;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		owner = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new AssignAbstractTypeVisitor(access, name, desc, signature, exceptions);
	}
	
	class AssignAbstractTypeVisitor extends FieldAssignmentVisitor {
		
		public AssignAbstractTypeVisitor(int access, String name, String desc, String signature, String[] exceptions) {
			super(access, name, desc, signature, exceptions);
		}

		@Override
		public void visitEnd() {
			super.visitEnd();
			if (!fieldAssignments.isEmpty()) {
		
				try {
					
					Analyzer a = new Analyzer(new CustomClassLoadingSimpleVerifier());
					Frame[] frames = a.analyze(owner, this);
		
					for (FieldInsnNode fieldInsnNode : fieldAssignments) {
						Frame assignmentFrame = frames[instructions.indexOf(fieldInsnNode)];
						int stackSlot = assignmentFrame.getStackSize() - 1;
						BasicValue stackValue = (BasicValue) assignmentFrame.getStack(stackSlot);
						if (stackValue == null || "Lnull;".equals(stackValue.getType().toString())) {
							continue;
						}
						checkIfClassIsAbstract(fieldInsnNode.name, stackValue.getType());
					}
		
				} catch (AnalyzerException forwarded) {
					throw new RuntimeException(forwarded);
				}
			}
		}
		
		void checkIfClassIsAbstract(String name, Type objectType) {
			int sort = objectType.getSort();
			if(sort != Type.OBJECT) {
				return;
			}
			String dottedClassName = dottedClassName(objectType);
			Class<?> assignedClass = null;
			try {
				assignedClass = getClass().getClassLoader().loadClass(dottedClassName);
				if (assignedClass.isInterface() || Modifier.isAbstract(assignedClass.getModifiers())) {
					addResult("Field [" + name + "] can have an abstract type (" + dottedClassName + ") assigned to it.", 
							null, MutabilityReason.ABSTRACT_TYPE_TO_FIELD);
					result = IsImmutable.DEFINITELY_NOT;
				}
			} catch (ClassNotFoundException e) {
				addResult("Cannot analyse [" + dottedClassName + "] because the class cannot be loaded.", 
						null, MutabilityReason.CANNOT_ANALYSE);
			}
		}

	}
}
