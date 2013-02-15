/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@NotThreadSafe
final class EffectivePutfieldInsnFinder {

    private final FieldNode targetVariable;
    private final AbstractInsnNode[] setterInstructions;

    private EffectivePutfieldInsnFinder(final FieldNode theTargetVariable, final InsnList instructions) {
        targetVariable = theTargetVariable;
        setterInstructions = instructions.toArray();
    }

    public static EffectivePutfieldInsnFinder getInstance(final FieldNode targetVariable,
            final InsnList instructions) {
        return new EffectivePutfieldInsnFinder(notNull(targetVariable), notNull(instructions));
    }

    public AssignmentInsn getEffectivePutfieldInstruction() {
        final Set<AssignmentInsn> putfieldInsns = findPutfieldInstructionsForVariable();
        return getEffectivePutfieldInstruction(putfieldInsns);
    }

    private Set<AssignmentInsn> findPutfieldInstructionsForVariable() {
        final Set<AssignmentInsn> result = new HashSet<AssignmentInsn>(setterInstructions.length);
        LabelNode label = null;
        for (int i = 0; i < setterInstructions.length; i++) {
            final AbstractInsnNode abstractInsnNode = setterInstructions[i];
            if (isLabelNode(abstractInsnNode)) {
                label = (LabelNode) abstractInsnNode;
            } else {
                addIfPutfieldInstructionForVariable(label, i, abstractInsnNode, result);
            }
        }
        return result;
    }

    private static boolean isLabelNode(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.LABEL == abstractInsnNode.getType();
    }

    private void addIfPutfieldInstructionForVariable(final LabelNode label, final int numberOfInsn,
            final AbstractInsnNode abstractInsnNode,
            final Set<AssignmentInsn> putfieldInsnsForVariable) {
        if (Opcodes.PUTFIELD == abstractInsnNode.getOpcode()) {
            final FieldInsnNode putfieldInsn = (FieldInsnNode) abstractInsnNode;
            if (putfieldInsn.name.equals(targetVariable.name)) {
                putfieldInsnsForVariable.add(DefaultAssignmentInsn.getInstance(label, numberOfInsn, putfieldInsn));
            }
        }
    }

    /*
     * The effective putfield instruction is the last one in the sequence of
     * instructions which puts a value to the target variable. Thus the highest
     * instruction number indicates the position of the effective putfield
     * instruction.
     */
    private AssignmentInsn getEffectivePutfieldInstruction(final Set<AssignmentInsn> putfieldInstructions) {
        AssignmentInsn result = NullAssignmentInsn.getInstance();
        int maxInstructionNumber = -1;
        for (final AssignmentInsn putfieldInsn : putfieldInstructions) {
            final int indexOfAssignmentInstruction = putfieldInsn.getIndexOfAssignmentInstruction();
            if (indexOfAssignmentInstruction > maxInstructionNumber) {
                maxInstructionNumber = indexOfAssignmentInstruction;
                result = putfieldInsn;
            }
        }
        return result;
    }

}
