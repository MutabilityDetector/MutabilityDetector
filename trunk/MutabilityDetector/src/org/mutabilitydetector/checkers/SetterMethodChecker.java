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

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.objectweb.asm.MethodVisitor;
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
		this.ownerName = name;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return new SetterAssignmentVisitor(ownerName, access, name, desc, signature, exceptions);
	}

	class SetterAssignmentVisitor extends FieldAssignmentVisitor {

		public SetterAssignmentVisitor(String ownerName, int access, String name, String desc, String signature, String[] exceptions) {
			super(ownerName, access, name, desc, signature, exceptions);
		}
		
		
		protected void visitFieldAssignmentFrame(Frame assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
			if (isInvalidStackValue(stackValue) || isConstructor()) {
				return;
			}
			
			setIsImmutableResult(fieldInsnNode.name);
			
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
