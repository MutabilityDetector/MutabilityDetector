package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.FieldLocation;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Entry;
import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Initialisers;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 13.03.2013
 */
@NotThreadSafe
final class SetterMethodChecker extends AbstractSetterMethodChecker {

    private final Map<FieldNode, Collection<UnknownTypeValue>> initialValues;
    private final Map<FieldNode, Collection<JumpInsn>> assignmentGuards;
    private VariableInitialisersAssociation variableInitialisersAssociation;

    private SetterMethodChecker() {
        super();
        initialValues = new HashMap<FieldNode, Collection<UnknownTypeValue>>();
        assignmentGuards = new HashMap<FieldNode, Collection<JumpInsn>>();
        variableInitialisersAssociation = null;
    }

    public static AsmMutabilityChecker newInstance() {
        return new SetterMethodChecker();
    }

    @Override
    protected void collectCandidates() {
        final Collection<FieldNode> variablesOfAnalysedClass = getEnhancedClassNode().getFields();
        final Finder<VariableInitialisersAssociation> f = CandidatesFinder.newInstance(variablesOfAnalysedClass);
        variableInitialisersAssociation = f.find();
    }

    @Override
    protected void collectInitialisers() {
        final Collection<MethodNode> methodsOfAnalysedClass = getEnhancedClassNode().getMethods();
        final Finder<VariableInitialisersAssociation> f = InitialisersFinder.newInstance(methodsOfAnalysedClass,
                variableInitialisersAssociation);
        variableInitialisersAssociation = f.find();
    }

    @Override
    protected void verifyCandidates() {
        final Collection<FieldNode> unassociatedVariables = variableInitialisersAssociation
                .removeAndGetUnassociatedVariables();
        for (final FieldNode unassociatedVariable : unassociatedVariables) {
            setNonFinalFieldResult(unassociatedVariable.name);
        }
    }

    private void setNonFinalFieldResult(final String variableName) {
        final String msg = "Field is not final, if shared across threads the Java Memory Model will not"
                + " guarantee it is initialised before it is read.";
        final FieldLocation location = fieldLocation(variableName, ClassLocation.fromInternalName(ownerClass));
        setResult(msg, location, MutabilityReason.NON_FINAL_FIELD);
    }

    @Override
    protected void verifyInitialisers() {
        for (final Entry entry : variableInitialisersAssociation) {
            verifyInitialisersFor(entry.getCandidate(), entry.getInitialisers());
        }
    }

    private void verifyInitialisersFor(final FieldNode candidate, final Initialisers allInitialisersForCandidate) {
        final Collection<MethodNode> methodInitialisers = allInitialisersForCandidate.getMethods();
        if (containsMoreThanOne(methodInitialisers)) {
            setFieldCanBeReassignedResultForEachMethodInitialiser(candidate.name, methodInitialisers);
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

    @Override
    protected void collectPossibleInitialValues() {
        for (final Entry entry : variableInitialisersAssociation) {
            final FieldNode candidate = entry.getCandidate();
            final Initialisers initialisers = entry.getInitialisers();
            final Finder<Set<UnknownTypeValue>> f = InitialValueFinder.newInstance(candidate, initialisers);
            initialValues.put(candidate, f.find());
        }
    }

    @Override
    protected void verifyPossibleInitialValues() {
        if (containsMoreThanOne(initialValues)) {
            setFieldCanBeReassignedResultForEachInitialValue();
        }
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

    private static boolean containsMoreThanOne(final Map<?, ?> aMap) {
        return 1 < aMap.size();
    }

    @Override
    protected void collectAssignmentGuards() {
        for (final Entry e : variableInitialisersAssociation) {
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
                variableInitialisersAssociation, this);
        v.verify();
    }

    @Override
    protected void collectAssignmentInstructions() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void verifyAssignmentInstructions() {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [initialValues=");
        b.append(initialValues);
        // TODO Methodenrumpf korrekt implementieren.
        b.append("]");
        return b.toString();
    }

}
