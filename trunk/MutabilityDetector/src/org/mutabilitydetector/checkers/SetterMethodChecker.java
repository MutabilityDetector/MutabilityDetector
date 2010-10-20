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

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.checkers.info.Slashed.slashed;
import static org.mutabilitydetector.locations.SimpleClassLocation.fromInternalName;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.ArrayList;
import java.util.List;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInfo;
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
	
	
	private PrivateMethodInvocationInfo privateMethodInvocationInfo;
	
	/**
	 * @see #newSetterMethodChecker(PrivateMethodInvocationInfo)
	 */
	private SetterMethodChecker(PrivateMethodInvocationInfo privateMethodInvocationInfo) {
		this.privateMethodInvocationInfo = privateMethodInvocationInfo;
	}
	
	public static SetterMethodChecker newSetterMethodChecker(PrivateMethodInvocationInfo privateMethodInvocationInfo) {
		return new SetterMethodChecker(privateMethodInvocationInfo);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new SetterAssignmentVisitor(ownerClass, access, name, desc, signature, exceptions, privateMethodInvocationInfo);
	}

	class SetterAssignmentVisitor extends FieldAssignmentVisitor {

		private List<Integer> varInstructionIndices = new ArrayList<Integer>();
		private boolean refOnStackIsAField = false;
		private final PrivateMethodInvocationInfo privateMethodInvocationInfo;

		public SetterAssignmentVisitor(String ownerName, int access, String name, String desc, String signature, String[] exceptions, 
				PrivateMethodInvocationInfo privateMethodInvocationInfo) 
		{
			super(ownerName, access, name, desc, signature, exceptions);
			this.privateMethodInvocationInfo = privateMethodInvocationInfo;
		}
		
		protected void visitFieldAssignmentFrame(Frame assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
			if (isConstructor() || isInvalidStackValue(stackValue)) {
				return;
			}
			
			
			if(method(access).is(ACC_STATIC)) {
				detectInStaticMethod(fieldInsnNode);
			} else {
				detectInInstanceMethod(fieldInsnNode, stackValue);
			}
			
		}

		private boolean isOnlyCalledFromConstructor() {
			MethodIdentifier methodId = forMethod(slashed(this.owner), name + ":" + desc);
			return privateMethodInvocationInfo.isOnlyCalledFromConstructor(methodId);
		}

		private void detectInStaticMethod(FieldInsnNode fieldInsnNode) {
			String ownerOfReassignedField = fieldInsnNode.owner;
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
				if(isThisObject(indexOfOwningObject) || refOnStackIsAField) { 
					setIsImmutableResult(fieldInsnNode.name);
				} else {
					// Setting field on other instance of 'this' type
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
				refOnStackIsAField = true;
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
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			varInstructionIndices.add(var);
			refOnStackIsAField = false;
		}

		private boolean isConstructor() {
			return "<init>".equals(name);
		}


		private void setIsImmutableResult(String fieldName) {
			
			if(isOnlyCalledFromConstructor()) {
				return;
			}
			
			String message = format("Field [%s] can be reassigned within method [%s]", fieldName, this.name);
			addResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
			result = IsImmutable.DEFINITELY_NOT;
		}
		
	}
	
}
