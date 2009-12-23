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