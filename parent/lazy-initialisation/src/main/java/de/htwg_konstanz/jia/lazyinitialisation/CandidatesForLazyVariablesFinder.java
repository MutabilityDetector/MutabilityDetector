package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.tree.FieldNode;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 05.03.2013
 */
@ThreadSafe
final class CandidatesForLazyVariablesFinder {

    private final ConvenienceClassNode classNode;
    private final VariableSetterCollection candidatesForLazyVariables;
    private volatile boolean isCandidatesAlreadyFound;

    private CandidatesForLazyVariablesFinder(final ConvenienceClassNode theClassNode) {
        classNode = theClassNode;
        candidatesForLazyVariables = VariableSetterCollection.newInstance();
        isCandidatesAlreadyFound = false;
    }

    public static CandidatesForLazyVariablesFinder newInstance(final ConvenienceClassNode classNode) {
        final String msg = "Argument 'convenienceClassNode' must not be null!";
        return new CandidatesForLazyVariablesFinder(notNull(classNode, msg));
    }

    public VariableSetterCollection getCandidatesForLazyVariables() {
        if (!isCandidatesAlreadyFound) {
            findCandidatesForLazyVariables();
            isCandidatesAlreadyFound = true;
        }
        return candidatesForLazyVariables.copy();
    }

    private void findCandidatesForLazyVariables() {
        for (final FieldNode variable : classNode.getFields()) {
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
        b.append("classNode=").append(classNode).append(", candidatesForLazyVariables=");
        b.append(candidatesForLazyVariables).append(", isCandidatesAlreadyFound=").append(isCandidatesAlreadyFound);
        b.append("]");
        return b.toString();
    }

}
