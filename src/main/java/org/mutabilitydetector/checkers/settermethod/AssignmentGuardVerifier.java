package org.mutabilitydetector.checkers.settermethod;

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
import static java.lang.String.format;
import static org.objectweb.asm.Opcodes.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Entry;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Initialisers;
import org.objectweb.asm.tree.*;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.03.2013
 */
@NotThreadSafe
final class AssignmentGuardVerifier {

    @NotThreadSafe
    private final class ZeroCheckAssignmentGuardVerifier {

        private final FieldNode candidate;
        private final JumpInsn assignmentGuard;
        private final ControlFlowBlock controlFlowBlock;

        public ZeroCheckAssignmentGuardVerifier(final FieldNode theCandidate, final JumpInsn theAssignmentGuard,
                final ControlFlowBlock theControlFlowBlock) {
            candidate = theCandidate;
            assignmentGuard = theAssignmentGuard;
            controlFlowBlock = theControlFlowBlock;
        }

        public void verify() {
            final String candidateName = candidate.name;
            final int indexOfPredecessor = assignmentGuard.getIndexWithinBlock() - 1;
            final AbstractInsnNode predecessor = controlFlowBlock.getBlockInstructionForIndex(indexOfPredecessor);
            if (isGetInstructionForVariable(predecessor, candidate)) {
                final String msgTemplate = "The assignment guard for lazy field [%s] is not correct.";
                final String msg = format(msgTemplate, candidateName);
                if (isZeroOnlyPossibleInitialValueForVariable() && assignmentGuard.getOpcode() == Opcode.IFEQ) {
                    setterMethodChecker.setFieldCanBeReassignedResult(msg);
                } else if (!isZeroOnlyPossibleInitialValueForVariable() && assignmentGuard.getOpcode() == Opcode.IFNE) {
                    setterMethodChecker.setFieldCanBeReassignedResult(msg);
                }
            } else if (isComparisonInsn(predecessor)) {
                verifyPredecessorOfComparisonInstruction(indexOfPredecessor - 1);
            } else if (checksAgainstOtherObject(assignmentGuard, controlFlowBlock, candidate)) {
                if (isOtherObjectNotAnInitialValue(assignmentGuard, controlFlowBlock)) {
                    final String msgTemplate = "The compared object is not a possible initial value of lazy field "
                            + "[%s].";
                    final String msg = format(msgTemplate, candidateName);
                    setterMethodChecker.setFieldCanBeReassignedResult(msg);
                }
            }
        }

        private boolean isZeroOnlyPossibleInitialValueForVariable() {
            boolean result = true;
            final Collection<UnknownTypeValue> possibleInitialValuesForVariable = initialValues.get(candidate);
            final Iterator<UnknownTypeValue> i = possibleInitialValuesForVariable.iterator();
            while (result && i.hasNext()) {
                final UnknownTypeValue u = i.next();
                result = u.isZero();
            }
            return result;
        }

        private void verifyPredecessorOfComparisonInstruction(final int indexOfInstructionToVerify) {
            final AbstractInsnNode predecessorOfComparisonInsn = controlFlowBlock
                    .getBlockInstructionForIndex(indexOfInstructionToVerify);
            if (isGetInstructionForVariable(predecessorOfComparisonInsn, candidate)) {
                verifyGetInstructionForVariable(indexOfInstructionToVerify, controlFlowBlock, candidate);
            } else if (isLoadInstructionForAlias(candidate, controlFlowBlock, predecessorOfComparisonInsn)) {
                verifyLoadInstructionForAlias(indexOfInstructionToVerify, controlFlowBlock, candidate);
            }
        }

    } // class ZeroCheckAssignmentGuardVerifier


    private final class NonNullCheckAssignmentGuardVerifier {
    
        private final FieldNode candidate;
        private final ControlFlowBlock controlFlowBlock;
        private final JumpInsn assignmentGuard;
    
        public NonNullCheckAssignmentGuardVerifier(final FieldNode theCandidate,
                final ControlFlowBlock theControlFlowBlock, final JumpInsn theAssignmentGuard) {
            candidate = theCandidate;
            controlFlowBlock = theControlFlowBlock;
            assignmentGuard = theAssignmentGuard;
        }
    
        public void verify() {
            final String candidateName = candidate.name;
            if (isNotPossibleInitialValueOfCandidate(DefaultUnknownTypeValue.getInstanceForNull(), candidate)) {
                final String msgTemplate = "The assignment guard for lazy field [%s] should check against null. "
                        + "Otherwise the field gets never initialised.";
                setterMethodChecker.setNonFinalFieldResult(format(msgTemplate, candidateName), candidateName);
            }
        }
    
    } // class NonNullCheckAssignmentGuardVerifier


    private final Map<FieldNode, Collection<UnknownTypeValue>> initialValues;
    private final Map<FieldNode, Collection<JumpInsn>> assignmentGuards;
    private final CandidatesInitialisersMapping candidatesInitialisersMapping;
    private final AbstractSetterMethodChecker setterMethodChecker;

    private AssignmentGuardVerifier(final Map<FieldNode, Collection<UnknownTypeValue>> theInitialValues,
            final Map<FieldNode, Collection<JumpInsn>> theAssignmentGuards,
            final CandidatesInitialisersMapping theVariableInitialisersAssociation,
            final AbstractSetterMethodChecker theSetterMethodChecker) {
        initialValues = new HashMap<FieldNode, Collection<UnknownTypeValue>>(theInitialValues);
        assignmentGuards = new HashMap<FieldNode, Collection<JumpInsn>>(theAssignmentGuards);
        candidatesInitialisersMapping = theVariableInitialisersAssociation;
        setterMethodChecker = theSetterMethodChecker;
    }

    public static AssignmentGuardVerifier newInstance(final Map<FieldNode, Collection<UnknownTypeValue>> initialValues,
            final Map<FieldNode, Collection<JumpInsn>> assignmentGuards,
            final CandidatesInitialisersMapping variableInitialisersAssociation,
            final AbstractSetterMethodChecker setterMethodChecker) {
        return new AssignmentGuardVerifier(checkNotNull(initialValues), checkNotNull(assignmentGuards),
                checkNotNull(variableInitialisersAssociation), checkNotNull(setterMethodChecker));
    }

    public void verify() {
        for (final Entry e : candidatesInitialisersMapping) {
            verifyEachCandidateInitialisersPair(e);
        }
    }

    private void verifyEachCandidateInitialisersPair(final Entry e) {
        if (hasAssignmentGuardFor(e.getCandidate())) {
            verifyEachInitialisingMethodForCandidate(e.getCandidate(), e.getInitialisers());
        } else {
            setFieldCanBeReassignedResultBecauseOfMissingAssignmentGuards(e.getCandidate());
        }
    }

    private boolean hasAssignmentGuardFor(final FieldNode candidate) {
        return assignmentGuards.containsKey(candidate);
    }

    private void verifyEachInitialisingMethodForCandidate(final FieldNode candidate, final Initialisers initialisers) {
        for (final MethodNode initialisingMethod : initialisers.getMethods()) {
            verifyEachAssignmentGuardWithinInitialisingMethod(candidate, initialisingMethod);
        }
    }
    
    private void verifyEachAssignmentGuardWithinInitialisingMethod(final FieldNode candidate, final MethodNode method) {
        final EnhancedClassNode enhancedClassNode = setterMethodChecker.getEnhancedClassNode();
        final List<ControlFlowBlock> controlFlowBlocks = enhancedClassNode.getControlFlowBlocksForMethod(method);
        for (final JumpInsn assignmentGuard : assignmentGuards.get(candidate)) {
            final ControlFlowBlock block = getControlFlowBlockWhichCovers(controlFlowBlocks, assignmentGuard);
            if (isOneValueJumpInstruction(assignmentGuard)) {
                verifyOneValueAssignmentGuard(candidate, assignmentGuard, block);
            } else if (isTwoValuesJumpInstruction(assignmentGuard)) {
                verifyTwoValuesAssignmentGuard(candidate, assignmentGuard, block);
            }
        }
    }
    
    private ControlFlowBlock getControlFlowBlockWhichCovers(final Collection<ControlFlowBlock> controlFlowBlocks,
            final JumpInsn assignmentGuard) {
        for (final ControlFlowBlock controlFlowBlock : controlFlowBlocks) {
            if (controlFlowBlock.covers(assignmentGuard.getIndexWithinMethod())) {
                return controlFlowBlock;
            }
        }
        return null;
    }
    
    private static boolean isOneValueJumpInstruction(final JumpInsn jumpInstruction) {
        switch (getOpcode(jumpInstruction)) {
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
        case IFNULL:
        case IFNONNULL:
            return true;
        default:
            return false;
        }
    }
    
    private void setFieldCanBeReassignedResultBecauseOfMissingAssignmentGuards(final FieldNode candidate) {
        final String msgTemplate = "Lazy initialisation requires at least one assignment guard for field [%s]";
        final String msg = format(msgTemplate, candidate.name);
        setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
    }

    private void verifyOneValueAssignmentGuard(final FieldNode candidate, final JumpInsn assignmentGuard,
            final ControlFlowBlock block) {
        if (checksAgainstZero(assignmentGuard)) {
            verifyZeroCheckAssignmentGuard(candidate, assignmentGuard, block);
        } else if (checksAgainstNonNull(assignmentGuard)) {
            verifyNonNullCheckAssignmentGuard(candidate, assignmentGuard, block);
        }
    }

    private static boolean checksAgainstZero(final JumpInsn jumpInstruction) {
        switch (getOpcode(jumpInstruction)) {
        case IFEQ:
        case IFNE:
        case IFLT:
        case IFGE:
        case IFGT:
        case IFLE:
            return true;
        default:
            return false;
        }
    }

    private void verifyZeroCheckAssignmentGuard(final FieldNode candidate, final JumpInsn assignmentGuard,
            final ControlFlowBlock controlFlowBlock) {
        final ZeroCheckAssignmentGuardVerifier v = new ZeroCheckAssignmentGuardVerifier(candidate, assignmentGuard,
                controlFlowBlock);
        v.verify();
    }

    private static boolean checksAgainstNonNull(final JumpInsn jumpInstruction) {
        return IFNONNULL == getOpcode(jumpInstruction);
    }

    private static boolean isGetInstructionForVariable(final AbstractInsnNode insn, final FieldNode candidate) {
        boolean result = false;
        if (GETFIELD == insn.getOpcode() || GETSTATIC == insn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) insn;
            result = candidate.name.equals(getfield.name);
        }
        return result;
    }

    private static boolean isLoadInstructionForAlias(final FieldNode candidate,
            final ControlFlowBlock blockWithAssignmentGuard, final AbstractInsnNode insn) {
        final Finder<Alias> f = AliasFinder.newInstance(candidate.name, blockWithAssignmentGuard);
        final Alias alias = f.find();
        return alias.doesExist && isLoadInstructionForAlias(insn, alias);
    }

    private static boolean isLoadInstructionForAlias(final AbstractInsnNode insn, final Alias alias) {
        boolean result = false;
        if (AbstractInsnNode.VAR_INSN == insn.getType()) {
            final VarInsnNode loadInstruction = (VarInsnNode) insn;
            result = loadInstruction.var == alias.localVariable;
        }
        return result;
    }

    private static boolean isComparisonInsn(final AbstractInsnNode abstractInsnNode) {
        switch (abstractInsnNode.getOpcode()) {
        case LCMP:
        case FCMPL:
        case FCMPG:
        case DCMPL:
        case DCMPG:
        case IF_ICMPEQ:
        case IF_ICMPNE:
        case IF_ICMPLT:
        case IF_ICMPGE:
        case IF_ICMPGT:
        case IF_ICMPLE:
        case IF_ACMPEQ:
        case IF_ACMPNE:
            return true;
        default:
            return false;
        }
    }

    private void verifyGetInstructionForVariable(final int indexOfPreComparisonInsn, final ControlFlowBlock block,
            final FieldNode candidate) {
        final int indexOfGetInstruction = indexOfPreComparisonInsn - 2;
        verifyComparativeValueOf(block.getBlockInstructionForIndex(indexOfGetInstruction), candidate);
    }

    private void verifyComparativeValueOf(final AbstractInsnNode insn, final FieldNode candidate) {
        final UnknownTypeValue comparativeValue = getComparativeValue(insn);
        if (isNotPossibleInitialValueOfCandidate(comparativeValue, candidate)) {
            final String msgTemplate = "Assignment for field [%s] guard does not check against a possible "
                    + "initial value";
            final String msg = String.format(msgTemplate, candidate.name);
            setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }
    }

    private static UnknownTypeValue getComparativeValue(final AbstractInsnNode insn) {
        UnknownTypeValue result = null;
        if (AbstractInsnNode.INSN == insn.getType()) {
            final Opcode opcode = Opcode.forInt(insn.getOpcode());
            result = opcode.stackValue();
        } else if (AbstractInsnNode.LDC_INSN == insn.getType()) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) insn;
            result = DefaultUnknownTypeValue.getInstance(ldcInsn.cst);
        } else if (AbstractInsnNode.INT_INSN == insn.getType()) {
            final IntInsnNode intInsnNode = (IntInsnNode) insn;
            result = DefaultUnknownTypeValue.getInstance(intInsnNode.operand);
        }
        return result;
    }

    private boolean isNotPossibleInitialValueOfCandidate(final UnknownTypeValue comparativeValue,
            final FieldNode candidate) {
        final boolean result;
        final Collection<UnknownTypeValue> possibleInitialValuesOfCandidate = initialValues.get(candidate);
        if (null != possibleInitialValuesOfCandidate) {
            result = !possibleInitialValuesOfCandidate.contains(comparativeValue);
        } else {
            result = true;
        }
        return result;
    }

    private void verifyLoadInstructionForAlias(final int indexOfLoadInstruction, final ControlFlowBlock block,
            final FieldNode candidate) {
        final int indexOfLoadInsnPredecessor = indexOfLoadInstruction - 1;
        verifyComparativeValueOf(block.getBlockInstructionForIndex(indexOfLoadInsnPredecessor), candidate);
    }

    private static boolean checksAgainstOtherObject(final JumpInsn assignmentGuard, final ControlFlowBlock block,
            final FieldNode candidate) {
        final int indexWithinBlock = assignmentGuard.getIndexWithinBlock();
        final AbstractInsnNode possibleEqualsInsn = block.getBlockInstructionForIndex(indexWithinBlock - 1);
        final boolean result;
        if (isEqualsInstruction(possibleEqualsInsn)) {
            final AbstractInsnNode possibleGetInstructionForVariable = block
                    .getBlockInstructionForIndex(indexWithinBlock - 3);
            result = isGetInstructionForVariable(possibleGetInstructionForVariable, candidate);
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isEqualsInstruction(final AbstractInsnNode insn) {
        final boolean result;
        if (AbstractInsnNode.METHOD_INSN == insn.getType()) {
            final MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
            result = methodInsnNode.name.equals("equals");
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isOtherObjectNotAnInitialValue(final JumpInsn assignmentGuard, final ControlFlowBlock block) {
        final int indexWithinBlock = assignmentGuard.getIndexWithinBlock();
        final AbstractInsnNode predecessorInsn = block.getBlockInstructionForIndex(indexWithinBlock - 2);
        final boolean result;
        if (ACONST_NULL != predecessorInsn.getOpcode()) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    private void verifyNonNullCheckAssignmentGuard(final FieldNode candidate, final JumpInsn assignmentGuard,
            final ControlFlowBlock controlFlowBlock) {
        final NonNullCheckAssignmentGuardVerifier v = new NonNullCheckAssignmentGuardVerifier(candidate,
                controlFlowBlock, assignmentGuard);
        v.verify();
    }

    private static boolean isTwoValuesJumpInstruction(final JumpInsn assignmentGuard) {
        switch (getOpcode(assignmentGuard)) {
        case IF_ICMPEQ:
        case IF_ICMPNE:
        case IF_ICMPLT:
        case IF_ICMPGE:
        case IF_ICMPGT:
        case IF_ICMPLE:
        case IF_ACMPEQ:
        case IF_ACMPNE:
            return true;
        default:
            return false;
        }
    }

    private static int getOpcode(final JumpInsn jumpInstruction) {
        final JumpInsnNode jumpInsnNode = jumpInstruction.getJumpInsnNode();
        return jumpInsnNode.getOpcode();
    }

    private void verifyTwoValuesAssignmentGuard(final FieldNode candidate, final JumpInsn assignmentGuard,
            final ControlFlowBlock block) {
        final int indexOfPredecessor = assignmentGuard.getIndexWithinBlock() - 1;
        final AbstractInsnNode predecessor = block.getBlockInstructionForIndex(indexOfPredecessor);
        if (isGetInstructionForVariable(predecessor, candidate)) {
            verifyGetInstructionForVariable(indexOfPredecessor, block, candidate);
        } else if (isLoadInstructionForAlias(candidate, block, predecessor)) {
            verifyLoadInstructionForAlias(indexOfPredecessor, block, candidate);
        }
    }

}
