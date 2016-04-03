/**
 * 
 */package org.mutabilitydetector.checkers.settermethod;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@NotThreadSafe
final class EffectiveAssignmentInsnFinder implements Finder<AssignmentInsn> {

    private final FieldNode targetVariable;
    private final Collection<ControlFlowBlock> controlFlowBlocks;

    private EffectiveAssignmentInsnFinder(final FieldNode theTargetVariable,
            final Collection<ControlFlowBlock> theControlFlowBlocks) {
        targetVariable = theTargetVariable;
        controlFlowBlocks = Collections.unmodifiableCollection(theControlFlowBlocks);
    }

    /**
     * Static factory method.
     * 
     * @param targetVariable
     *            the variable to find the effective {@code putfield} or
     *            {@code putstatic} instruction for.
     * @param controlFlowBlocks
     *            all control flow blocks of an initialising constructor or
     *            method.
     * @return a new instance of this class.
     */
    public static EffectiveAssignmentInsnFinder newInstance(final FieldNode targetVariable,
            final Collection<ControlFlowBlock> controlFlowBlocks) {
        return new EffectiveAssignmentInsnFinder(checkNotNull(targetVariable), checkNotNull(controlFlowBlocks));
    }

    /**
     * @return an instance of {@link AssignmentInsn} which contains the
     *         particular {@code putfield} or {@code putstatic} instruction
     *         which effectively sets the value for the provided targetVariable.
     *         The result ist never {@code null} instead {@code isNull} should
     *         be invoked.
     */
    @Override
    public AssignmentInsn find() {
        final Collection<AssignmentInsn> assignmentInstructions = findAssignmentInstructionsForVariable();
        return getEffectiveAssignmentInstruction(assignmentInstructions);
    }

    private Collection<AssignmentInsn> findAssignmentInstructionsForVariable() {
        final Set<AssignmentInsn> result = new HashSet<AssignmentInsn>();
        for (final ControlFlowBlock controlFlowBlock : controlFlowBlocks) {
            result.addAll(findInAllBlockInstructions(controlFlowBlock));
        }
        return result;
    }

    private Collection<AssignmentInsn> findInAllBlockInstructions(final ControlFlowBlock controlFlowBlock) {
        final Set<AssignmentInsn> result = new HashSet<AssignmentInsn>();
        final List<AbstractInsnNode> blockInstructions = controlFlowBlock.getBlockInstructions();
        for (int i = 0; i < blockInstructions.size(); i++) {
            final AbstractInsnNode insn = blockInstructions.get(i);
            if (isInitialiserForTargetVariable(insn)) {
                final FieldInsnNode assignmentInsnNode = (FieldInsnNode) insn;
                final int indexWithinMethod = controlFlowBlock.getIndexWithinMethod(i);
                result.add(DefaultAssignmentInsn.newInstance(controlFlowBlock, indexWithinMethod, assignmentInsnNode));
            }
        }
        return result;
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
     * The effective assignment instruction is the last one in the sequence of
     * instructions which puts a value to the target variable. Thus the highest
     * instruction number indicates the position of the effective assignment
     * instruction.
     */
    private AssignmentInsn getEffectiveAssignmentInstruction(final Collection<AssignmentInsn> assignmentInstructions) {
        AssignmentInsn result = NullAssignmentInsn.getInstance();
        int maxInstructionNumber = -1;
        for (final AssignmentInsn assignmentInsn : assignmentInstructions) {
            final int indexOfAssignmentInstruction = assignmentInsn.getIndexWithinMethod();
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
        b.append(", controlFlowBlocks=").append(controlFlowBlocks).append(']');
        return b.toString();
    }

}
