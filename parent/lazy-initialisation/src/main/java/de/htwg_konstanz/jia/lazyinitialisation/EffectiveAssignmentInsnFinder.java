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
final class EffectiveAssignmentInsnFinder {

    private final FieldNode targetVariable;
    private final InsnList setterInstructions;

    private EffectiveAssignmentInsnFinder(final FieldNode theTargetVariable, final InsnList instructions) {
        targetVariable = theTargetVariable;
        setterInstructions = instructions;

    }

    /**
     * Static factory method.
     * 
     * @param targetVariable
     *            the variable to find the effective {@code putfield}
     *            or {@code putstatic} instruction for.
     * @param setterInstructions
     *            all instructions of a constructor or setter method.
     * @return a new instance of this class.
     */
    public static EffectiveAssignmentInsnFinder newInstance(final FieldNode targetVariable,
            final InsnList setterInstructions) {
        return new EffectiveAssignmentInsnFinder(notNull(targetVariable), notNull(setterInstructions));
    }

    /**
     * @return an instance of {@link AssignmentInsn} which contains
     *         the particular {@code putfield} or {@code putstatic}
     *         instruction which effectively sets the value for the
     *         provided targetVariable. The result ist never
     *         {@code null} instead {@code isNull} should be invoked.
     */
    public AssignmentInsn getEffectiveAssignmentInstruction() {
        final Set<AssignmentInsn> assignmentInstructions = findAssignmentInstructionsForVariable();
        return getEffectiveAssignmentInstruction(assignmentInstructions);
    }

    private Set<AssignmentInsn> findAssignmentInstructionsForVariable() {
        final Set<AssignmentInsn> result = new HashSet<AssignmentInsn>(setterInstructions.size());
        LabelNode label = null;
        for (int i = 0; i < setterInstructions.size(); i++) {
            final AbstractInsnNode abstractInsnNode = setterInstructions.get(i);
            if (isLabelNode(abstractInsnNode)) {
                label = (LabelNode) abstractInsnNode;
            } else {
                addIfAssignmentInstructionForVariable(label, i, abstractInsnNode, result);
            }
        }
        return result;
    }

    private static boolean isLabelNode(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.LABEL == abstractInsnNode.getType();
    }

    private void addIfAssignmentInstructionForVariable(final LabelNode label,
            final int numberOfInsn,
            final AbstractInsnNode insn,
            final Set<AssignmentInsn> assignmentInsnsForVariable) {
        if (isInitialiserForTargetVariable(insn)) {
            assignmentInsnsForVariable.add(DefaultAssignmentInsn.getInstance(label, numberOfInsn,
                    (FieldInsnNode) insn));
        }
    }

    private boolean isInitialiserForTargetVariable(final AbstractInsnNode insn) {
        final boolean result;
        if (isPutfieldInstruction(insn) || isPutstaticInstruction(insn)) {
            final FieldInsnNode assignmentInsn = (FieldInsnNode) insn;
            result = assignmentInsn.name.equals(targetVariable.name);
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isPutfieldInstruction(final AbstractInsnNode insn) {
        return Opcodes.PUTFIELD == insn.getOpcode();
    }

    private static boolean isPutstaticInstruction(final AbstractInsnNode insn) {
        return Opcodes.PUTSTATIC == insn.getOpcode();
    }

    /*
     * The effective assignment instruction is the last one in the
     * sequence of instructions which puts a value to the target
     * variable. Thus the highest instruction number indicates the
     * position of the effective assignment instruction.
     */
    private AssignmentInsn getEffectiveAssignmentInstruction(final Set<AssignmentInsn> assignmentInstructions) {
        AssignmentInsn result = NullAssignmentInsn.getInstance();
        int maxInstructionNumber = -1;
        for (final AssignmentInsn assignmentInsn : assignmentInstructions) {
            final int indexOfAssignmentInstruction = assignmentInsn.getIndexOfAssignmentInstruction();
            if (indexOfAssignmentInstruction > maxInstructionNumber) {
                maxInstructionNumber = indexOfAssignmentInstruction;
                result = assignmentInsn;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [targetVariable=").append(targetVariable.name);
        b.append(", setterInstructions=").append(setterInstructions).append("]");
        return b.toString();
    }

}
