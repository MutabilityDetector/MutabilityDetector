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
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.03.2013
 */
final class EffectiveAssignmentInsnVerifier {

    private final AssignmentInsn effectiveAssignmentInstruction;
    private final FieldNode candidate;
    private final AbstractSetterMethodChecker setterMethodChecker;

    private EffectiveAssignmentInsnVerifier(final AssignmentInsn theAssignmentInsn,
            final FieldNode theCandidate,
            final AbstractSetterMethodChecker theSetterMethodChecker) {
        effectiveAssignmentInstruction = theAssignmentInsn;
        candidate = theCandidate;
        setterMethodChecker = theSetterMethodChecker;
    }

    public static EffectiveAssignmentInsnVerifier newInstance(final AssignmentInsn assignmentInsn,
            final FieldNode candidate,
            final AbstractSetterMethodChecker setterMethodChecker) {
        return new EffectiveAssignmentInsnVerifier(checkNotNull(assignmentInsn), checkNotNull(candidate),
                checkNotNull(setterMethodChecker));
    }

    public void verify() {
        if (effectiveAssignmentInstruction.isNull()) {
            return;
        }
        final Alias alias = findAlias();
        final AbstractInsnNode p = getPredecessorOfAssignmentInstruction();
        if (alias.doesExist) {
            verifyWithAlias(p, alias.localVariable);
        } else {
            recognizeAsMutableIfNecessary(p);
        }
    }

    private Alias findAlias() {
        final String nameOfAssignedVariable = effectiveAssignmentInstruction.getNameOfAssignedVariable();
        final ControlFlowBlock surroundingBlock = effectiveAssignmentInstruction.getSurroundingControlFlowBlock();
        final Finder<Alias> f = AliasFinder.newInstance(nameOfAssignedVariable, surroundingBlock);
        return f.find();
    }

    private AbstractInsnNode getPredecessorOfAssignmentInstruction() {
        final ControlFlowBlock surroundingBlock = effectiveAssignmentInstruction.getSurroundingControlFlowBlock();
        final int indexWithinMethod = effectiveAssignmentInstruction.getIndexWithinMethod();
        final int predecessorIndexWithinMethod = indexWithinMethod - 1;
        final int predecessorIndexWithinBlock = surroundingBlock.getIndexWithinBlock(predecessorIndexWithinMethod);
        return surroundingBlock.getBlockInstructionForIndex(predecessorIndexWithinBlock);
    }

    private void verifyWithAlias(final AbstractInsnNode p, final int aliasLocalVariable) {
        if (isAliasLoadInstruction(p, aliasLocalVariable)) {
            final ControlFlowBlock block = effectiveAssignmentInstruction.getSurroundingControlFlowBlock();
            for (final ControlFlowBlock predecessorBlock : block.getPredecessors()) {
                verifyWithAliasForEachBlock(aliasLocalVariable, predecessorBlock);
            }
        }
    }

    private static boolean isAliasLoadInstruction(final AbstractInsnNode insn, final int aliasLocalVariable) {
        switch (insn.getOpcode()) {
        case Opcodes.ILOAD:
        case Opcodes.LLOAD:
        case Opcodes.FLOAD:
        case Opcodes.DLOAD:
        case Opcodes.ALOAD:
            final VarInsnNode varInsnNode = (VarInsnNode) insn;
            return aliasLocalVariable == varInsnNode.var;
        default:
            return false;
        }
    }

    private void verifyWithAliasForEachBlock(final int aliasLocalVariable, final ControlFlowBlock predecessorBlock) {
        final List<AbstractInsnNode> blockInstructions = predecessorBlock.getBlockInstructions();
        for (int i = 0; i < blockInstructions.size(); i++) {
            final AbstractInsnNode insn = blockInstructions.get(i);
            if (isAliasStoreInstruction(insn, aliasLocalVariable)) {
                final AbstractInsnNode predecessor = blockInstructions.get(i - 1);
                recognizeAsMutableIfNecessary(predecessor);
                break;
            }
        }
    }

    private static boolean isAliasStoreInstruction(final AbstractInsnNode insn, final int aliasLocalVariable) {
        switch (insn.getOpcode()) {
        case Opcodes.ISTORE:
        case Opcodes.LSTORE:
        case Opcodes.FSTORE:
        case Opcodes.DSTORE:
        case Opcodes.ASTORE:
            final VarInsnNode varInsnNode = (VarInsnNode) insn;
            return aliasLocalVariable == varInsnNode.var;
        default:
            return false;
        }
    }

    private void recognizeAsMutableIfNecessary(final AbstractInsnNode p) {
        if (isNotPushConstantOntoStackInstruction(p) && isNotInvokationOfParameterlessInstanceOrClassMethod(p)) {
            final String candidateName = effectiveAssignmentInstruction.getNameOfAssignedVariable();
            if (isCandidateOfPrimitiveType()) {
                final String msgTemplate = "Value for lazy field [%s] is not a constant but stems from a method which "
                        + "is neither parameterless nor an instance or class method.";
                setterMethodChecker.setFieldCanBeReassignedResult(String.format(msgTemplate, candidateName));
            } else {
                final String message = "Value for lazy field is not a constant but stems from a method which is "
                        + "neither parameterless nor an instance or class method.";
                setterMethodChecker.setMutableTypeToFieldResult(message, candidateName);
            }
        }
    }

    private boolean isNotPushConstantOntoStackInstruction(final AbstractInsnNode insn) {
        final Opcode opcode = Opcode.forInt(insn.getOpcode());
        final Collection<Opcode> stackPushingConstants = Opcode.constants();
        return !stackPushingConstants.contains(opcode);
    }

    private boolean isNotInvokationOfParameterlessInstanceOrClassMethod(final AbstractInsnNode insn) {
        final boolean result;
        if (AbstractInsnNode.METHOD_INSN != insn.getType()) {
            result = true;
        } else {
            final MethodInsnNode methodInvokationInstruction = (MethodInsnNode) insn;
            result = hasInvokedMethodArguments(methodInvokationInstruction)
                    || isInvokedMethodNotInstanceOrClassMethod(methodInvokationInstruction);
        }
        return result;
    }

    private static boolean hasInvokedMethodArguments(final MethodInsnNode methodInvokationInstruction) {
        final String invokedMethodDescriptor = methodInvokationInstruction.desc;
        final Type[] argumentTypes = Type.getArgumentTypes(invokedMethodDescriptor);
        return 0 < argumentTypes.length;
    }

    private boolean isInvokedMethodNotInstanceOrClassMethod(final MethodInsnNode methodInvokationInstruction) {
        final String invokedMethodOwner = methodInvokationInstruction.owner;
        final EnhancedClassNode enhancedClassNode = setterMethodChecker.getEnhancedClassNode();
        final String internalClassName = enhancedClassNode.getName();
        return !invokedMethodOwner.equals(internalClassName);
    }

    private boolean isCandidateOfPrimitiveType() {
        final Type typeOfCandidate = Type.getType(candidate.desc);
        final int sortOfType = typeOfCandidate.getSort();
        return Type.ARRAY != sortOfType && Type.METHOD != sortOfType && Type.OBJECT != sortOfType;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [effectiveAssignmentInstruction=");
        builder.append(effectiveAssignmentInstruction);
        builder.append(", candidate=").append(candidate.name);
        builder.append(", setterMethodChecker=").append(setterMethodChecker);
        builder.append("]");
        return builder.toString();
    }

}
