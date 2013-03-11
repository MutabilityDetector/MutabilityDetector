package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collection;
import java.util.Collections;

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
final class InitialisingMethodsFinder implements Finder<VariableInitialisersAssociation> {

    private final Collection<MethodNode> methods;
    private final VariableInitialisersAssociation variableInitialisers;
    private volatile boolean areMethodsAlreadyExamined;

    private InitialisingMethodsFinder(final Collection<MethodNode> theMethods,
            final VariableInitialisersAssociation theVariableSetterCollection) {
        methods = Collections.unmodifiableCollection(theMethods);
        variableInitialisers = theVariableSetterCollection.copy();
        areMethodsAlreadyExamined = false;
    }

    /**
     * Creates a new instance of this class. None of the arguments must be
     * {@code null}.
     * 
     * @param methodsOfAnalysedClass
     *            {@link Collection} containing all methods ({@code MethodNode})
     *            of the class under examination.
     * @param variableInitialiserAssociation
     *            an instance of
     *            {@link VariableInitialiserAssociation} which
     *            contains all candidates for lazy variables of
     *            {@code classNode}. This object should be obtained by
     *            {@link CandidatesFinder#find()}
     *            .
     * @return a new instance of this class.
     */
    public static InitialisingMethodsFinder newInstance(final Collection<MethodNode> methodsOfAnalysedClass,
            final VariableInitialisersAssociation variableInitialiserAssociation) {
        final String msgTemplate = "Argument '%s' must not be null!";
        notNull(methodsOfAnalysedClass, format(msgTemplate, "methodsOfAnalysedClass"));
        notNull(variableInitialiserAssociation, format(msgTemplate, "variableInitialiserAssociation"));
        return new InitialisingMethodsFinder(methodsOfAnalysedClass, variableInitialiserAssociation);
    }

    @Override
    public VariableInitialisersAssociation find() {
        if (areMethodsToBeExamined()) {
            collectAllInitialisingMethodsForAllLazyVariableCandidates();
            variableInitialisers.removeUnassociatedVariables();
            areMethodsAlreadyExamined = true;
        }
        return variableInitialisers.copy();
    }

    private boolean areMethodsToBeExamined() {
        return !areMethodsAlreadyExamined;
    }

    private void collectAllInitialisingMethodsForAllLazyVariableCandidates() {
        for (final MethodNode methodNode : methods) {
            addMethodNodeIfIsInitialiserForVariable(methodNode);
        }
    }

    private void addMethodNodeIfIsInitialiserForVariable(final MethodNode methodNode) {
        for (final AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (isInitialiserForAVariable(insn)) {
                final FieldInsnNode assignmentInstruction = (FieldInsnNode) insn;
                variableInitialisers.addInitialiserForVariable(assignmentInstruction.name, methodNode);
            }
        }
    }

    private static boolean isInitialiserForAVariable(final AbstractInsnNode insn) {
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
        b.append(getClass().getSimpleName()).append(" [methods=").append(methods);
        b.append(", variableSetterCollection=").append(variableInitialisers);
        b.append(", areMethodsAlreadyExamined=").append(areMethodsAlreadyExamined).append("]");
        return b.toString();
    }

}
