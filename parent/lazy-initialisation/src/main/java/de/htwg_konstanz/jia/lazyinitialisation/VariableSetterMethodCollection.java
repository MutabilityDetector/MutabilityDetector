package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Collection to hold relations of variables and setter methods for those.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.02.2013
 */
@NotThreadSafe
final class VariableSetterMethodCollection implements Iterable<Entry<FieldNode, List<MethodNode>>> {

    private final Map<FieldNode, List<MethodNode>> variableSetterMethods;

    private VariableSetterMethodCollection() {
        variableSetterMethods = new HashMap<FieldNode, List<MethodNode>>();
    }

    /**
     * @return a new instance of this class.
     */
    public static VariableSetterMethodCollection newInstance() {
        return new VariableSetterMethodCollection();
    }

    /**
     * @param variableNode
     *            variable to add to this collection. Must not be {@code null}.
     * @return {@code true} if the node was added, {@code false} if the node was
     *         already contained in this collection.
     */
    public boolean addVariable(final FieldNode variableNode) {
        notNull(variableNode);
        final boolean result = !variableSetterMethods.containsKey(variableNode);
        variableSetterMethods.put(variableNode, new ArrayList<MethodNode>(2));
        return result;
    }

    /**
     * @param variableName
     *            name of the variable to associate the setter method with. Must
     *            neither be {@code null} nor {@code ""}.
     * @param setterMethodNode
     *            setter method to be associated with {@code variableName} Must
     *            not be {@code null}.
     * @return {@code true} if {@code variableName} was already part of this
     *         collection, {@code false} else.
     */
    public boolean addSetterMethodForVariable(final String variableName, final MethodNode setterMethodNode) {
        notEmpty(variableName);
        notNull(setterMethodNode);
        boolean result = false;
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            result = addSetterMethodForVariable(variableNode, setterMethodNode);
        }
        return result;
    }

    private FieldNode getVariableNodeForName(final String variableName) {
        for (final Map.Entry<FieldNode, List<MethodNode>> entry : variableSetterMethods.entrySet()) {
            final FieldNode variableNode = entry.getKey();
            if (variableNode.name.equals(variableName)) {
                return variableNode;
            }
        }
        return null;
    }

    private boolean addSetterMethodForVariable(final FieldNode variableNode, final MethodNode setterMethodNode) {
        final List<MethodNode> setterMethodsForvariable = variableSetterMethods.get(variableNode);
        return setterMethodsForvariable.add(setterMethodNode);
    }

    /**
     * @param variableName
     *            name of the variable to get all setter methods for. Must
     *            neither be {@code null} nor {@code ""}.
     * @return all getter methods for the variable with name
     *         {@code variableName}. If none are found an empty {@code List} is
     *         returned.
     */
    public List<MethodNode> getSetterMethodsFor(final String variableName) {
        notEmpty(variableName);
        List<MethodNode> result = Collections.emptyList();
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            final List<MethodNode> setterMethodsForVariable = variableSetterMethods.get(variableNode);
            result = new ArrayList<MethodNode>(setterMethodsForVariable);
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
        for (final Entry<FieldNode, List<MethodNode>> entry : variableSetterMethods.entrySet()) {
            final List<MethodNode> associatedSetterMethods = entry.getValue();
            if (associatedSetterMethods.isEmpty()) {
                result.add(entry.getKey());
            }
        }
        for (final FieldNode unassociatedVariable : result) {
            variableSetterMethods.remove(unassociatedVariable);
        }
        return result;
    }

    @Override
    public Iterator<Entry<FieldNode, List<MethodNode>>> iterator() {
        final Set<Entry<FieldNode, List<MethodNode>>> entrySet = new HashSet<Entry<FieldNode, List<MethodNode>>>(
                variableSetterMethods.size());
        for (final Entry<FieldNode, List<MethodNode>> entry : variableSetterMethods.entrySet()) {
            entrySet.add(entry);
        }
        return entrySet.iterator();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variableSetterMethods", variableSetterMethods);
        return builder.toString();
    }

}
