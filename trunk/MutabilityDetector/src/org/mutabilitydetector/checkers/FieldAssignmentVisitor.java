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

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class FieldAssignmentVisitor extends MethodNode {

	protected ArrayList<FieldInsnNode> fieldAssignments = new ArrayList<FieldInsnNode>();

	public FieldAssignmentVisitor(int access, String name, String desc, String signature, String[] exceptions) {
		super(access, name, desc, signature, exceptions);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		super.visitFieldInsn(opcode, owner, name, desc);
		if (opcode == Opcodes.PUTFIELD) {
			fieldAssignments.add((FieldInsnNode) instructions.getLast());
		}
	
	}
}