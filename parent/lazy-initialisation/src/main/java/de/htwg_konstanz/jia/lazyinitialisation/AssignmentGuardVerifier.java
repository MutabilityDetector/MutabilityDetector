package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.Opcodes.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Entry;
import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Initialisers;

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
            final int indexOfPredecessor = assignmentGuard.getIndexWithinBlock() - 1;
            final AbstractInsnNode predecessor = controlFlowBlock.getBlockInstructionForIndex(indexOfPredecessor);
            if (isGetInstructionForVariable(predecessor, candidate)) {
                if (!isZeroOnlyPossibleInitialValueForVariable()) {
                    final String msgTemplate = "Field [%s] has possibly other initial values than zero.";
                    final String msg = format(msgTemplate, candidate.name);
                    setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
                } else if (isComparisonInsn(predecessor)) {
                    verifyPredecessorOfComparisonInstruction(indexOfPredecessor - 1);
                } else if (checksAgainstOtherObject(assignmentGuard, controlFlowBlock, candidate)) {
                    if (isOtherObjectNotAnInitialValue(assignmentGuard, controlFlowBlock)) {
                        final String msgTemplate = "The compared object is not a possible initial value of field [%s]";
                        final String msg = format(msgTemplate, candidate.name);
                        setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
                    }
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


    private final Map<FieldNode, Collection<UnknownTypeValue>> initialValues;
    private final Map<FieldNode, Collection<JumpInsn>> assignmentGuards;
    private final VariableInitialisersAssociation variableInitialisersAssociation;
    private final AbstractSetterMethodChecker setterMethodChecker;

    private AssignmentGuardVerifier(final Map<FieldNode, Collection<UnknownTypeValue>> theInitialValues,
            final Map<FieldNode, Collection<JumpInsn>> theAssignmentGuards,
            final VariableInitialisersAssociation theVariableInitialisersAssociation,
            final AbstractSetterMethodChecker theSetterMethodChecker) {
        initialValues = new HashMap<FieldNode, Collection<UnknownTypeValue>>(theInitialValues);
        assignmentGuards = new HashMap<FieldNode, Collection<JumpInsn>>(theAssignmentGuards);
        variableInitialisersAssociation = theVariableInitialisersAssociation;
        setterMethodChecker = theSetterMethodChecker;
    }

    public static AssignmentGuardVerifier newInstance(final Map<FieldNode, Collection<UnknownTypeValue>> initialValues,
            final Map<FieldNode, Collection<JumpInsn>> assignmentGuards,
            final VariableInitialisersAssociation variableInitialisersAssociation,
            final AbstractSetterMethodChecker setterMethodChecker) {
        return new AssignmentGuardVerifier(notNull(initialValues), notNull(assignmentGuards),
                notNull(variableInitialisersAssociation), notNull(setterMethodChecker));
    }

    public void verify() {
        for (final Entry e : variableInitialisersAssociation) {
            verifyEachCandidateInitialisersPair(e);
        }
    }

    private void verifyEachCandidateInitialisersPair(final Entry e) {
        if (hasNoAssignmentGuardFor(e.getCandidate())) {
            setFieldCanBeReassignedResultBecausOfMissingAssignmentGuards(e.getCandidate());
        } else {
            verifyEachInitialisingMethodForCandidate(e.getCandidate(), e.getInitialisers());
        }
    }

    private boolean hasNoAssignmentGuardFor(final FieldNode candidate) {
        return !assignmentGuards.containsKey(candidate);
    }

    private void setFieldCanBeReassignedResultBecausOfMissingAssignmentGuards(final FieldNode candidate) {
        final String msgTemplate = "Lazy initialisation requires at least one assignment guard for field [%s]";
        final String msg = format(msgTemplate, candidate.name);
        setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
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
            // TODO Meldung machen.
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

    private boolean
            isPossibleInitialValueOfCandidate(final UnknownTypeValue comparativeValue, final FieldNode candidate) {
        return !isNotPossibleInitialValueOfCandidate(comparativeValue, candidate);
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

    private void verifyLoadInstructionForAlias(final int indexOfPreComparisonInsn, final ControlFlowBlock block,
            final FieldNode candidate) {
        final int indexOfLoadInsnPredecessor = indexOfPreComparisonInsn - 1;
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

    // private static boolean checksAgainstNull(final JumpInsn jumpInstruction)
    // {
    // return IFNULL == getOpcode(jumpInstruction);
    // }

    private static boolean checksAgainstNonNull(final JumpInsn jumpInstruction) {
        return IFNONNULL == getOpcode(jumpInstruction);
    }

    private void verifyNonNullCheckAssignmentGuard(final FieldNode candidate, final JumpInsn assignmentGuard,
            final ControlFlowBlock controlFlowBlock) {
        final NonNullCheckAssignmentGuardVerifier v = new NonNullCheckAssignmentGuardVerifier(candidate,
                controlFlowBlock, assignmentGuard);
        v.verify();
//        final int indexOfPredecessor = assignmentGuard.getIndexWithinBlock() - 1;
//        final AbstractInsnNode predecessor = block.getBlockInstructionForIndex(indexOfPredecessor);
//        if (isNotPossibleInitialValueOfCandidate(DefaultUnknownTypeValue.getInstanceForNull(), candidate)) {
//            // TODO Meldung machen.
//        } else if (isGetInstructionForVariable(predecessor, candidate)) {
//            if (isPossibleInitialValueOfCandidate(DefaultUnknownTypeValue.getInstanceForNull(), candidate)) {
//                // TODO Meldung machen.
//            }
//        } else if (checksAgainstOtherObject(assignmentGuard, block, candidate)) {
//            // TODO Block implementieren.
//        }
    }

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
            final int indexOfPredecessor = assignmentGuard.getIndexWithinBlock() - 1;
            final AbstractInsnNode predecessor = controlFlowBlock.getBlockInstructionForIndex(indexOfPredecessor);
            if (isNotPossibleInitialValueOfCandidate(DefaultUnknownTypeValue.getInstanceForNull(), candidate)) {
                // TODO Meldung machen.
            } else if (isGetInstructionForVariable(predecessor, candidate)) {
                if (isPossibleInitialValueOfCandidate(DefaultUnknownTypeValue.getInstanceForNull(), candidate)) {
                    // TODO Meldung machen.
                }
            } else if (checksAgainstOtherObject(assignmentGuard, controlFlowBlock, candidate)) {
                // TODO Block implementieren.
            }
        }

    } // class NonNullCheckAssignmentGuardVerifier

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
        final int indexOfPreComparisonInsn = indexOfPredecessor - 1;
        if (isGetInstructionForVariable(predecessor, candidate)) {
            verifyGetInstructionForVariable(indexOfPreComparisonInsn, block, candidate);
        } else if (isLoadInstructionForAlias(candidate, block, predecessor)) {
            verifyLoadInstructionForAlias(indexOfPreComparisonInsn, block, candidate);
        }
    }

}
