package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.FieldNode;

/**
 * Collection to hold relations of variables and assignment instructions for
 * those.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
@NotThreadSafe
final class VariableAssignmentCollection implements Iterable<Entry<FieldNode, List<AssignmentInsn>>> {

    private final Map<FieldNode, List<AssignmentInsn>> variableAssignmentInstructions;

    private VariableAssignmentCollection() {
        variableAssignmentInstructions = new HashMap<FieldNode, List<AssignmentInsn>>();
    }

    /**
     * @return a new instance of this class.
     */
    public static VariableAssignmentCollection newInstance() {
        return new VariableAssignmentCollection();
    }

    /**
     * @param variableNode
     *            variable to add to this collection. Must not be {@code null}.
     * @return {@code true} if the node was added, {@code false} if the node was
     *         already contained in this collection.
     */
    public boolean addVariable(final FieldNode variableNode) {
        notNull(variableNode);
        final boolean result = !variableAssignmentInstructions.containsKey(variableNode);
        variableAssignmentInstructions.put(variableNode, new ArrayList<AssignmentInsn>(2));
        return result;
    }

    /**
     * @param variableName
     *            name of the variable to associate the assignment instruction
     *            with. Must neither be {@code null} nor {@code ""}.
     * @param assignmentInstructionNode
     *            assignment instructions to be associated with
     *            {@code variableName} Must not be {@code null}.
     * @return {@code true} if {@code variableName} was already part of this
     *         collection, {@code false} else.
     */
    public boolean addAssignmentInstructionForVariable(final String variableName,
            final AssignmentInsn assignmentInstructionNode) {
        notEmpty(variableName);
        notNull(assignmentInstructionNode);
        boolean result = false;
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            result = addAssignmentInstructionForVariable(variableNode, assignmentInstructionNode);
        }
        return result;
    }

    private FieldNode getVariableNodeForName(final String variableName) {
        for (final Map.Entry<FieldNode, List<AssignmentInsn>> entry : variableAssignmentInstructions.entrySet()) {
            final FieldNode variableNode = entry.getKey();
            if (variableNode.name.equals(variableName)) {
                return variableNode;
            }
        }
        return null;
    }

    private boolean addAssignmentInstructionForVariable(final FieldNode variableNode, final AssignmentInsn setterAssignmentInsn) {
        final List<AssignmentInsn> setterMethodsForvariable = variableAssignmentInstructions.get(variableNode);
        return setterMethodsForvariable.add(setterAssignmentInsn);
    }

    public Set<FieldNode> getVariables() {
        return Collections.unmodifiableSet(variableAssignmentInstructions.keySet());
    }

    /**
     * @param variableName
     *            name of the variable to get all setter methods for. Must
     *            neither be {@code null} nor {@code ""}.
     * @return all assignment instructions for the variable with name
     *         {@code variableName}. If none are found an empty {@code List} is
     *         returned.
     */
    public List<AssignmentInsn> getAssignmentInstructionsFor(final String variableName) {
        notEmpty(variableName);
        List<AssignmentInsn> result = Collections.emptyList();
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            final List<AssignmentInsn> assignmentInstructionsForVariable = variableAssignmentInstructions
                    .get(variableNode);
            result = new ArrayList<AssignmentInsn>(assignmentInstructionsForVariable);
        }
        return result;
    }

    /**
     * Removes all variables from this collection which are not associated with
     * setter methods.
     * 
     * @return a {@code List} containing the removed unassociated variables.
     *         This list is empty if none were removed, i. e. the result is
     *         never {@code null}.
     */
    public List<FieldNode> removeUnassociatedVariables() {
        final List<FieldNode> result = new ArrayList<FieldNode>();
        for (final Entry<FieldNode, List<AssignmentInsn>> entry : variableAssignmentInstructions.entrySet()) {
            final List<AssignmentInsn> associatedSetterMethods = entry.getValue();
            if (associatedSetterMethods.isEmpty()) {
                result.add(entry.getKey());
            }
        }
        for (final FieldNode unassociatedVariable : result) {
            variableAssignmentInstructions.remove(unassociatedVariable);
        }
        return result;
    }

    @Override
    public Iterator<Entry<FieldNode, List<AssignmentInsn>>> iterator() {
        final Set<Entry<FieldNode, List<AssignmentInsn>>> entrySet = new HashSet<Entry<FieldNode, List<AssignmentInsn>>>(
                variableAssignmentInstructions.size());
        for (final Entry<FieldNode, List<AssignmentInsn>> entry : variableAssignmentInstructions.entrySet()) {
            entrySet.add(entry);
        }
        return entrySet.iterator();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variableAssignmentInstructions", variableAssignmentInstructions);
        return builder.toString();
    }

}
