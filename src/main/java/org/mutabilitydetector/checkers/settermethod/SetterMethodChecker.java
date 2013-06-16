/*
 *    Copyright (c) 2008-2013 Graham Allan, Juergen Fickel
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.checkers.settermethod;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Entry;
import org.mutabilitydetector.checkers.settermethod.CandidatesInitialisersMapping.Initialisers;
import org.mutabilitydetector.locations.Slashed;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.03.2013
 */
@NotThreadSafe
public final class SetterMethodChecker extends AbstractSetterMethodChecker {

    private final class MethodIdentifierFactory {
        private final MethodNode method;

        public MethodIdentifierFactory(final MethodNode theMethod) {
            method = checkNotNull(theMethod);
        }

        public MethodIdentifier getMethodIdentifier() {
            final Slashed className = getSlashedClassName();
            final String methodDescriptor = getMethodDescriptor();
            final MethodIdentifier result = forMethod(className, methodDescriptor);
            return result;
        }

        private Slashed getSlashedClassName() {
            final String owner = getEnhancedClassNode().getName();
            final Slashed result = slashed(owner);
            return result;
        }

        private String getMethodDescriptor() {
            final String methodName = method.name;
            final String methodDesc = method.desc;
            final String result = String.format("%s:%s", methodName, methodDesc);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(getClass().getSimpleName()).append(" [method=").append(method).append("]");
            return builder.toString();
        }
    } // class MethodIdentifierFactory


    private final PrivateMethodInvocationInformation privateMethodInvocationInfo;
    private final Map<FieldNode, Collection<UnknownTypeValue>> initialValues;
    private final Map<FieldNode, Collection<JumpInsn>> assignmentGuards;
    private final Map<FieldNode, AssignmentInsn> effectiveAssignmentInstructions;

    private SetterMethodChecker(final PrivateMethodInvocationInformation thePrivateMethodInvocationInfo) {
        super();
        privateMethodInvocationInfo = thePrivateMethodInvocationInfo;
        initialValues = new HashMap<FieldNode, Collection<UnknownTypeValue>>();
        assignmentGuards = new HashMap<FieldNode, Collection<JumpInsn>>();
        effectiveAssignmentInstructions = new HashMap<FieldNode, AssignmentInsn>();
    }

    public static AsmMutabilityChecker newInstance() {
        return new SetterMethodChecker(null);
    }

    public static AsmMutabilityChecker newInstance(final PrivateMethodInvocationInformation privateMethodInvocationInfo) {
        return new SetterMethodChecker(checkNotNull(privateMethodInvocationInfo));
    }

    @Override
    protected void collectInitialisers() {
        final Collection<MethodNode> methodsOfAnalysedClass = getEnhancedClassNode().getMethods();
        final Finder<CandidatesInitialisersMapping> f = InitialisersFinder.newInstance(methodsOfAnalysedClass,
                candidatesInitialisersMapping);
        candidatesInitialisersMapping = f.find();
    }

    @Override
    protected void verifyCandidates() {
        final Collection<FieldNode> unassociatedVariables = candidatesInitialisersMapping
                .removeAndGetCandidatesWithoutInitialisingMethod();
        for (final FieldNode unassociatedVariable : unassociatedVariables) {
            setNonFinalFieldResult(unassociatedVariable.name);
        }
    }

    @Override
    protected void verifyInitialisers() {
        for (final Entry entry : candidatesInitialisersMapping) {
            verifyInitialisersFor(entry.getCandidate(), entry.getInitialisers());
        }
        verifyVisibleSetterMethods();
    }

    private void verifyInitialisersFor(final FieldNode candidate, final Initialisers allInitialisersForCandidate) {
        final Collection<MethodNode> initialisingMethods = allInitialisersForCandidate.getMethods();
        if (containsMoreThanOne(initialisingMethods)) {
            setFieldCanBeReassignedResultForEachMethodInitialiser(candidate.name, initialisingMethods);
        } else if (hasPrivateMethodInvocationInfo()) {
            for (final MethodNode initialisingMethod : initialisingMethods) {
                removeCandidateIfInitialisingMethodIsOnlyCalledFromContructor(initialisingMethod);
            }
        }
    }

    private void verifyVisibleSetterMethods() {
        final Map<String, Set<MethodNode>> allVisibleSetterMethods = candidatesInitialisersMapping
                .getAllVisibleSetterMethods();
        for (final Map.Entry<String, Set<MethodNode>> e : allVisibleSetterMethods.entrySet()) {
            final String variableName = e.getKey();
            for (final MethodNode visibleSetterMethod : e.getValue()) {
                setFieldCanBeReassignedResult(variableName, visibleSetterMethod.name);
            }
        }
    }

    private static boolean containsMoreThanOne(final Collection<?> aCollection) {
        return 1 < aCollection.size();
    }

    private void setFieldCanBeReassignedResultForEachMethodInitialiser(final String candidateName,
            final Collection<MethodNode> methodInitialisers) {
        for (final MethodNode methodInitialiser : methodInitialisers) {
            setFieldCanBeReassignedResult(candidateName, methodInitialiser.name);
        }
    }

    private boolean hasPrivateMethodInvocationInfo() {
        return null != privateMethodInvocationInfo;
    }

    private void removeCandidateIfInitialisingMethodIsOnlyCalledFromContructor(final MethodNode initialisingMethod) {
        if (isOnlyCalledFromConstructor(initialisingMethod)) {
            candidatesInitialisersMapping.removeAndGetCandidateForInitialisingMethod(initialisingMethod);
        }
    }

    private boolean isOnlyCalledFromConstructor(final MethodNode initialisingMethod) {
        final MethodIdentifierFactory factory = new MethodIdentifierFactory(initialisingMethod);
        final MethodIdentifier methodId = factory.getMethodIdentifier();
        return privateMethodInvocationInfo.isOnlyCalledFromConstructor(methodId);
    }

    @Override
    protected void collectPossibleInitialValues() {
        for (final Entry entry : candidatesInitialisersMapping) {
            final FieldNode candidate = entry.getCandidate();
            final Initialisers initialisers = entry.getInitialisers();
            final Finder<Set<UnknownTypeValue>> f = InitialValueFinder.newInstance(candidate, initialisers,
                    getEnhancedClassNode());
            final Set<UnknownTypeValue> possibleInitialValues = f.find();
            initialValues.put(candidate, possibleInitialValues);
        }
    }

    @Override
    protected void verifyPossibleInitialValues() {
        if (hasAnyVariableMoreThanOneInitialValue()) {
            setFieldCanBeReassignedResultForEachInitialValue();
        }
    }

    private boolean hasAnyVariableMoreThanOneInitialValue() {
        for (final Map.Entry<FieldNode, Collection<UnknownTypeValue>> e : initialValues.entrySet()) {
            final Collection<UnknownTypeValue> initialValuesForVariable = e.getValue();
            if (1 < initialValuesForVariable.size()) {
                return true;
            }
        }
        return false;
    }

    private void setFieldCanBeReassignedResultForEachInitialValue() {
        for (final Map.Entry<FieldNode, Collection<UnknownTypeValue>> e : initialValues.entrySet()) {
            final Collection<UnknownTypeValue> initialValuesForCandidate = e.getValue();
            final String msgTmpl = "Field [%s] has too many possible initial values for lazy initialisation: [%s]";
            final String candidateName = e.getKey().name;
            final String initialValues = initialValuesToString(initialValuesForCandidate);
            final String msg = format(msgTmpl, candidateName, initialValues);
            setResultForClass(msg, MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }
    }

    private static String initialValuesToString(final Collection<UnknownTypeValue> initialValuesForCandidate) {
        final StringBuilder result = new StringBuilder();
        final String separatorValue = ", ";
        String separator = "";
        for (final UnknownTypeValue initialValue : initialValuesForCandidate) {
            result.append(separator).append(initialValue);
            separator = separatorValue;
        }
        return result.toString();
    }

    @Override
    protected void collectEffectiveAssignmentInstructions() {
        for (final Entry e : candidatesInitialisersMapping) {
            final FieldNode candidate = e.getCandidate();
            final MethodNode initialisingMethod = getSoleInitialisingMethod(e.getInitialisers());
            addEffectiveAssignmentInstructionForCandidateIfPossible(candidate, initialisingMethod);
        }
    }

    /*
     * There must be at most one initialising method.
     * This is verified by `verifyInitialisers()`.
     */
    private MethodNode getSoleInitialisingMethod(final Initialisers initialisers) {
        final List<MethodNode> initialisingMethods = initialisers.getMethods();
        final MethodNode result;
        if (!initialisingMethods.isEmpty()) {
            result = initialisingMethods.get(0);
        } else {
            result = null;
        }
        return result;
    }

    private void addEffectiveAssignmentInstructionForCandidateIfPossible(final FieldNode candidate,
            final MethodNode initialisingMethod) {
        if (null != initialisingMethod) {
            final EnhancedClassNode cn = getEnhancedClassNode();
            final Collection<ControlFlowBlock> blocks = cn.getControlFlowBlocksForMethod(initialisingMethod);
            final Finder<AssignmentInsn> f = EffectiveAssignmentInsnFinder.newInstance(candidate, blocks);
            effectiveAssignmentInstructions.put(candidate, f.find());
        }
    }

    @Override
    protected void verifyEffectiveAssignmentInstructions() {
        for (final Map.Entry<FieldNode, AssignmentInsn> e : effectiveAssignmentInstructions.entrySet()) {
            final EffectiveAssignmentInsnVerifier v = EffectiveAssignmentInsnVerifier.newInstance(e.getValue(),
                    e.getKey(), this);
            v.verify();
        }
    }

    @Override
    protected void collectAssignmentGuards() {
        for (final Entry e : candidatesInitialisersMapping) {
            final Initialisers initialisers = e.getInitialisers();
            collectAssignmentGuardsForEachInitialisingMethod(e.getCandidate(), initialisers.getMethods());
        }
    }

    private void collectAssignmentGuardsForEachInitialisingMethod(final FieldNode candidate,
            final Collection<MethodNode> initialisingMethods) {
        for (final MethodNode initialisingMethod : initialisingMethods) {
            final EnhancedClassNode cn = getEnhancedClassNode();
            final Collection<ControlFlowBlock> blocks = cn.getControlFlowBlocksForMethod(initialisingMethod);
            collectAssignmentGuardsForEachControlFlowBlock(candidate, blocks);
        }
    }

    private void collectAssignmentGuardsForEachControlFlowBlock(final FieldNode candidate,
            final Collection<ControlFlowBlock> controlFlowBlocks) {
        for (final ControlFlowBlock controlFlowBlock : controlFlowBlocks) {
            final Finder<JumpInsn> f = AssignmentGuardFinder.newInstance(candidate.name, controlFlowBlock);
            final JumpInsn supposedAssignmentGuard = f.find();
            addToAssignmentGuards(candidate, supposedAssignmentGuard);
        }
    }

    private void addToAssignmentGuards(final FieldNode candidate, final JumpInsn supposedAssignmentGuard) {
        if (supposedAssignmentGuard.isAssignmentGuard()) {
            final Collection<JumpInsn> assignmentGuardsForCandidate;
            if (assignmentGuards.containsKey(candidate)) {
                assignmentGuardsForCandidate = assignmentGuards.get(candidate);
            } else {
                final byte expectedMaximum = 3;
                assignmentGuardsForCandidate = new ArrayList<JumpInsn>(expectedMaximum);
                assignmentGuards.put(candidate, assignmentGuardsForCandidate);
            }
            assignmentGuardsForCandidate.add(supposedAssignmentGuard);
        }
    }

    @Override
    protected void verifyAssignmentGuards() {
        final AssignmentGuardVerifier v = AssignmentGuardVerifier.newInstance(initialValues, assignmentGuards,
                candidatesInitialisersMapping, this);
        v.verify();
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [initialValues=");
        b.append(initialValues);
        b.append(", assignmentGuards=").append(assignmentGuards);
        b.append(", effectiveAssignmentInstructions=").append(effectiveAssignmentInstructions);
        b.append(", candidatesInitialisersMapping=").append(candidatesInitialisersMapping);
        b.append("]");
        return b.toString();
    }

}
