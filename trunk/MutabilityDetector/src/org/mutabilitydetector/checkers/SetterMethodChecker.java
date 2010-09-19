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

import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.ArrayList;
import java.util.List;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;


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
	
	private String ownerName;

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		this.ownerName = name;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		System.out.printf("Method: %d, %s, %s, %s%n", access, name, desc, signature);
		return new SetterAssignmentVisitor(ownerName, access, name, desc, signature, exceptions);
	}
	
	

	class SetterAssignmentVisitor extends FieldAssignmentVisitor {

		private List<Integer> varInstructionIndices = new ArrayList<Integer>();

		public SetterAssignmentVisitor(String ownerName, int access, String name, String desc, String signature, String[] exceptions) {
			super(ownerName, access, name, desc, signature, exceptions);
		}
		
		protected void visitFieldAssignmentFrame(Frame assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
			System.out.printf("\tField Assignment: assigning to %s%n", fieldInsnNode.name);
			if (isConstructor() || isInvalidStackValue(stackValue)) {
				return;
			}
			
			if(method(access).is(ACC_STATIC)) {
				detectInStaticMethod(fieldInsnNode);
			} else {
				detectInInstanceMethod(fieldInsnNode, stackValue);
			}
			
		}

		private void detectInStaticMethod(FieldInsnNode fieldInsnNode) {
			String ownerOfReassignedField = fieldInsnNode.owner;
			System.out.printf("Assigning to %s in static method%n", ownerOfReassignedField);
			if(reassignedIsThisType(ownerOfReassignedField) && assignmentIsNotOnAParameter(fieldInsnNode)) {
				setIsImmutableResult(fieldInsnNode.name);
			}
		}

		private boolean assignmentIsNotOnAParameter(FieldInsnNode fieldInsnNode) {
			/*
			 * This is a temporary hack/workaround. It's quite difficult to tell
			 * for sure if the owner of the reassigned field is a parameter. But
			 * if the type is not included in the parameter list, we can guess
			 * it's not (though it still may be).
			 */
			boolean reassignmentIsOnATypeIncludedInParameters = this.desc.contains(fieldInsnNode.owner);
			
			return reassignmentIsOnATypeIncludedInParameters;
		}

		private boolean reassignedIsThisType(String ownerOfReassignedField) {
			return this.owner.compareTo(ownerOfReassignedField) == 0;
		}

		private void detectInInstanceMethod(FieldInsnNode fieldInsnNode, BasicValue stackValue) {
			if(thisObjectWasAddedToStack()) {
				
				int stackSpaceToLookBack = reassignedReferenceIsPrimitiveType(stackValue) ? 1 : 2;
				
				int indexOfOwningObject = varInstructionIndices.get(varInstructionIndices.size() - stackSpaceToLookBack);
				if(isThisObject(indexOfOwningObject)) {
					setIsImmutableResult(fieldInsnNode.name);
				} else {
					System.out.printf("Setting field [%s] on other instance of %s%n", fieldInsnNode.name, ownerClass);
				}
				
			}
		}

		private boolean reassignedReferenceIsPrimitiveType(BasicValue stackValue) {
			return stackValue.getType().getSort() != Type.OBJECT; 
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			super.visitFieldInsn(opcode, owner, name, desc);
			if(opcode == Opcodes.GETFIELD) {
				varInstructionIndices.add(varInstructionIndices.size());
			}
		}
		
		private boolean isThisObject(int indexOfOwningObject) {
			return indexOfOwningObject == 0;
		}
		
		
		private boolean thisObjectWasAddedToStack() {
			// the "this" reference is at position 0 of the local variable table
			return varInstructionIndices.contains(0);
		}

		@Override
		public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
			super.visitLocalVariable(name, desc, signature, start, end, index);
			System.out.printf("\tLocalVariable: %s, %s, %s, %s, %s, %d%n", name, desc, signature, start, end, index);
		}
		
		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			varInstructionIndices.add(var);
			System.out.printf("\tVar Insn: %d, %d%n", opcode, var);
		}

		private boolean isConstructor() {
			return "<init>".equals(name);
		}


		private void setIsImmutableResult(String fieldName) {
			addResult("Field [" + fieldName + "] can be reassigned within method [" + this.name + "]",
					null, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
			result = IsImmutable.DEFINITELY_NOT;
		}
		
	}
	
}
