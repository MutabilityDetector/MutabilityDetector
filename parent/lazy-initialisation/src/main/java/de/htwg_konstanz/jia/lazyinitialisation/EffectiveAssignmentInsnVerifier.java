/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collection;

import org.mutabilitydetector.MutabilityReason;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.03.2013
 */
final class EffectiveAssignmentInsnVerifier {

    private final AssignmentInsn effectiveAssignmentInstruction;
    private final AbstractSetterMethodChecker setterMethodChecker;

    private EffectiveAssignmentInsnVerifier(final AssignmentInsn theAssignmentInsn,
            final AbstractSetterMethodChecker theSetterMethodChecker) {
        effectiveAssignmentInstruction = theAssignmentInsn;
        setterMethodChecker = theSetterMethodChecker;
    }

    public static EffectiveAssignmentInsnVerifier newInstance(final AssignmentInsn assignmentInsn,
            final AbstractSetterMethodChecker setterMethodChecker) {
        return new EffectiveAssignmentInsnVerifier(notNull(assignmentInsn), notNull(setterMethodChecker));
    }

    public void verify() {
        if (effectiveAssignmentInstruction.isNull()) {
            return;
        }
        final AbstractInsnNode p = getPredecessorOfAssignmentInstruction();
        if (isNotPushConstantOntoStackInstruction(p) && isNotInvokationOfParameterlessInstanceOrClassMethod(p)) {
            final String msgTemplate = "Assigned value to field [%s] is neither a constant nor does it stem from "
                    + "invokation of a parameterless method of this class.";
            final String candidateName = effectiveAssignmentInstruction.getNameOfAssignedVariable();
            final String msg = String.format(msgTemplate, candidateName);
            setterMethodChecker.setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }
    }

    private AbstractInsnNode getPredecessorOfAssignmentInstruction() {
        final ControlFlowBlock surroundingBlock = effectiveAssignmentInstruction.getSurroundingControlFlowBlock();
        final int indexWithinMethod = effectiveAssignmentInstruction.getIndexWithinMethod();
        final int predecessorIndexWithinMethod = indexWithinMethod - 1;
        final int predecessorIndexWithinBlock = surroundingBlock.getIndexWithinBlock(predecessorIndexWithinMethod);
        return surroundingBlock.getBlockInstructionForIndex(predecessorIndexWithinBlock);
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

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [effectiveAssignmentInstruction=");
        builder.append(effectiveAssignmentInstruction).append(", setterMethodChecker=").append(setterMethodChecker);
        builder.append("]");
        return builder.toString();
    }

}
