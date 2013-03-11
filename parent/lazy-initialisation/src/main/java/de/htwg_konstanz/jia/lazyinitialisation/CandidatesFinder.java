package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.tree.FieldNode;

/**
 * 
 * 
 * @author Juergen Fickel
 * @version 05.03.2013
 */
@ThreadSafe
final class CandidatesFinder implements Finder<VariableInitialisersAssociation> {

    private final Collection<FieldNode> variables;
    private final VariableInitialisersAssociation candidatesForLazyVariables;
    private volatile boolean isCandidatesAlreadyFound;

    private CandidatesFinder(final Collection<FieldNode> theVariables) {
        variables = Collections.unmodifiableCollection(theVariables);
        candidatesForLazyVariables = VariableInitialisersAssociation.newInstance();
        isCandidatesAlreadyFound = false;
    }

    public static CandidatesFinder newInstance(final Collection<FieldNode> variablesOfAnalysedClass) {
        final String msg = "Argument 'variablesOfAnalysedClass' must not be null!";
        return new CandidatesFinder(notNull(variablesOfAnalysedClass, msg));
    }

    @Override
    public VariableInitialisersAssociation find() {
        if (!isCandidatesAlreadyFound) {
            findCandidatesForLazyVariables();
            isCandidatesAlreadyFound = true;
        }
        return candidatesForLazyVariables.copy();
    }

    private void findCandidatesForLazyVariables() {
        for (final FieldNode variable : variables) {
            if (isPrivateAndNonFinalVariable(variable.access)) {
                candidatesForLazyVariables.addVariable(variable);
            }
        }
    }

    private boolean isPrivateAndNonFinalVariable(final int access) {
        return field(access).isPrivate() && field(access).isNotFinal();
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [");
        b.append("variables=").append(variables).append(", candidatesForLazyVariables=");
        b.append(candidatesForLazyVariables).append(", isCandidatesAlreadyFound=").append(isCandidatesAlreadyFound);
        b.append("]");
        return b.toString();
    }

}
