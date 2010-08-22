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


import static java.lang.Thread.currentThread;

import java.lang.reflect.Modifier;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
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
		return new AssignAbstractTypeVisitor(owner, access, name, desc, signature, exceptions);
	}
	
	class AssignAbstractTypeVisitor extends FieldAssignmentVisitor {
		
		public AssignAbstractTypeVisitor(String owner, int access, String name, String desc, String signature, String[] exceptions) {
			super(owner, access, name, desc, signature, exceptions);
		}

		@Override
		protected void visitFieldAssignmentFrame(Frame assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
			if (isInvalidStackValue(stackValue)) {
				return;
			}
			checkIfClassIsAbstract(fieldInsnNode.name, stackValue.getType());
		}

		void checkIfClassIsAbstract(String name, Type objectType) {
			int sort = objectType.getSort();
			if(sort != Type.OBJECT) {
				return;
			}
			String dottedClassName = dottedClassName(objectType);
			Class<?> assignedClass = null;
			try {
				assignedClass = currentThread().getContextClassLoader().loadClass(dottedClassName);
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
