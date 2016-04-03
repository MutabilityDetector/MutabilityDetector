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
final class InitialisersFinder implements Finder<CandidatesInitialisersMapping> {

    private final Collection<MethodNode> methods;
    private final CandidatesInitialisersMapping candidatesInitialisersMapping;
    private volatile boolean areMethodsAlreadyExamined;

    private InitialisersFinder(final Collection<MethodNode> theMethods,
            final CandidatesInitialisersMapping theVariableInitialisersMapping) {
        methods = Collections.unmodifiableCollection(theMethods);
        candidatesInitialisersMapping = theVariableInitialisersMapping;
        areMethodsAlreadyExamined = false;
    }

    /**
     * Creates a new instance of this class. None of the arguments must be
     * {@code null}.
     * 
     * @param methodsOfAnalysedClass
     *            {@link Collection} containing all methods ({@code MethodNode})
     *            of the class under examination.
     * @param candidatesInitialisersMapping
     *            an instance of
     *            {@link VariableInitialiserAssociation} which
     *            contains all candidates for lazy variables of
     *            {@code classNode}. This object should be obtained by
     *            {@link CandidatesFinder#find()}
     *            .
     * @return a new instance of this class.
     */
    public static InitialisersFinder newInstance(final Collection<MethodNode> methodsOfAnalysedClass,
            final CandidatesInitialisersMapping candidatesInitialisersMapping) {
        final String msgTemplate = "Argument '%s' must not be null!";
        checkNotNull(methodsOfAnalysedClass, format(msgTemplate, "methodsOfAnalysedClass"));
        checkNotNull(candidatesInitialisersMapping, format(msgTemplate, "variableInitialiserMapping"));
        return new InitialisersFinder(methodsOfAnalysedClass, candidatesInitialisersMapping);
    }

    @Override
    public CandidatesInitialisersMapping find() {
        if (areMethodsToBeExamined()) {
            collectAllInitialisingMethodsForAllLazyVariableCandidates();
            areMethodsAlreadyExamined = true;
        }
        return candidatesInitialisersMapping;
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
                candidatesInitialisersMapping.addInitialiserForCandidate(assignmentInstruction.name, methodNode);
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
        b.append(", variableSetterMapping=").append(candidatesInitialisersMapping);
        b.append(", areMethodsAlreadyExamined=").append(areMethodsAlreadyExamined).append(']');
        return b.toString();
    }

}
