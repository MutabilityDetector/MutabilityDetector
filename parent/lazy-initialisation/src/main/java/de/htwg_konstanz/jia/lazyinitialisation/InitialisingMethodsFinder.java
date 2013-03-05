package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 05.03.2013
 */
@ThreadSafe
final class InitialisingMethodsFinder {

    private final ConvenienceClassNode classNode;
    private final VariableSetterCollection variableSetterCollection;
    private volatile boolean areMethodsAlreadyExamined;

    public InitialisingMethodsFinder(final ConvenienceClassNode theClassNode,
            final VariableSetterCollection theVariableSetterCollection) {
        classNode = theClassNode;
        variableSetterCollection = theVariableSetterCollection.copy();
        areMethodsAlreadyExamined = false;
    }

    /**
     * Creates a new instance of this class. None of the arguments
     * must be {@code null}.
     * 
     * @param classNode
     *            an instance of {@link ConvenienceClassNode} which
     *            contains all information about the class under
     *            examination.
     * @param variableSetterCollection
     *            an instance of {@link VariableSetterCollection}
     *            which contains all candidates for lazy variables of
     *            {@code classNode}. This object should be obtained by
     *            {@link CandidatesForLazyVariablesFinder#getCandidatesForLazyVariables()}
     *            .
     * @return a new instance of this class.
     */
    public static InitialisingMethodsFinder newInstance(final ConvenienceClassNode classNode,
            final VariableSetterCollection variableSetterCollection) {
        final String msgTemplate = "Argument '%s' must not be null!";
        notNull(classNode, format(msgTemplate, "classNode"));
        notNull(variableSetterCollection, format(msgTemplate, "variableSetterCollection"));
        return new InitialisingMethodsFinder(classNode, variableSetterCollection);
    }

    public VariableSetterCollection getVariablesAndTheirInitialisingMethods() {
        if (areMethodsToBeExamined()) {
            collectAllInitialisingMethodsForAllLazyVariableCandidates();
            variableSetterCollection.removeUnassociatedVariables();
            areMethodsAlreadyExamined = true;
        }
        return variableSetterCollection.copy();
    }

    private boolean areMethodsToBeExamined() {
        return !areMethodsAlreadyExamined;
    }

    private void collectAllInitialisingMethodsForAllLazyVariableCandidates() {
        for (final MethodNode methodNode : classNode.getMethods()) {
            addMethodNodeIfIsInitialiserForVariable(methodNode);
        }
    }

    private void addMethodNodeIfIsInitialiserForVariable(final MethodNode methodNode) {
        for (final AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (isInitialiserForVariable(insn)) {
                final FieldInsnNode putfield = (FieldInsnNode) insn;
                variableSetterCollection.addSetterForVariable(putfield.name, methodNode);
                break;
            }
        }
    }

    private static boolean isInitialiserForVariable(final AbstractInsnNode insn) {
        return isPutfieldInstruction(insn) || isPutstaticInstruction(insn);
    }

    private static boolean isPutfieldInstruction(final AbstractInsnNode insn) {
        return Opcodes.PUTFIELD == insn.getOpcode();
    }

    private static boolean isPutstaticInstruction(final AbstractInsnNode insn) {
        return Opcodes.PUTSTATIC == insn.getOpcode();
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [classNode=").append(classNode);
        b.append(", variableSetterCollection=").append(variableSetterCollection);
        b.append(", areMethodsAlreadyExamined=").append(areMethodsAlreadyExamined).append("]");
        return b.toString();
    }

}
