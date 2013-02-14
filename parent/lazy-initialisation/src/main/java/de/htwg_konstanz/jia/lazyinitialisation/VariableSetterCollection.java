package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Collection to hold relations of variables and setter methods for those.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.02.2013
 */
@NotThreadSafe
final class VariableSetterCollection implements Iterable<Entry<FieldNode, VariableSetterCollection.Setters>> {

    @NotThreadSafe
    public static final class Setters {
        private final List<MethodNode> constructors;
        private final List<MethodNode> methods;

        private Setters() {
            final byte initialSize = 2;
            constructors = new ArrayList<MethodNode>(initialSize);
            methods = new ArrayList<MethodNode>(initialSize);
        }

        public static Setters getInstance() {
            return new Setters();
        }

        public boolean add(final MethodNode setter) {
            final boolean result;
            if (isConstructor(setter)) {
                result = constructors.add(setter);
            } else {
                result = methods.add(setter);
            }
            return result;
        }

        private static boolean isConstructor(final MethodNode setter) {
            return MethodIs.aConstructor(setter.name);
        }

        public boolean isEmpty() {
            return constructors.isEmpty() && methods.isEmpty();
        }

        public List<MethodNode> constructors() {
            return Collections.unmodifiableList(constructors);
        }

        public List<MethodNode> methods() {
            return Collections.unmodifiableList(methods);
        }

        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("constructors", constructors).append("methods", methods);
            return builder.toString();
        }
    } // class Setters


    private final Map<FieldNode, Setters> variableSetters;

    private VariableSetterCollection() {
        variableSetters = new HashMap<FieldNode, Setters>();
    }

    /**
     * @return a new instance of this class.
     */
    public static VariableSetterCollection newInstance() {
        return new VariableSetterCollection();
    }

    /**
     * @param variableNode
     *            variable to add to this collection. Must not be {@code null}.
     * @return {@code true} if the node was added, {@code false} if the node was
     *         already contained in this collection.
     */
    public boolean addVariable(final FieldNode variableNode) {
        notNull(variableNode);
        final boolean result = !variableSetters.containsKey(variableNode);
        variableSetters.put(variableNode, Setters.getInstance());
        return result;
    }

    /**
     * @param variableName
     *            name of the variable to associate the setter method with. Must
     *            neither be {@code null} nor {@code ""}.
     * @param setter
     *            setter to be associated with {@code variableName} Must
     *            not be {@code null}.
     * @return {@code true} if {@code variableName} was already part of this
     *         collection, {@code false} else.
     */
    public boolean addSetterForVariable(final String variableName, final MethodNode setter) {
        notEmpty(variableName);
        notNull(setter);
        boolean result = false;
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            result = addSetterForVariable(variableNode, setter);
        }
        return result;
    }

    private FieldNode getVariableNodeForName(final String variableName) {
        for (final Map.Entry<FieldNode, Setters> entry : variableSetters.entrySet()) {
            final FieldNode variableNode = entry.getKey();
            if (variableNode.name.equals(variableName)) {
                return variableNode;
            }
        }
        return null;
    }

    private boolean addSetterForVariable(final FieldNode variableNode, final MethodNode setter) {
        final Setters settersForvariable = variableSetters.get(variableNode);
        return settersForvariable.add(setter);
    }

    /**
     * @param variableName
     *            name of the variable to get all setter methods for.
     *            Must neither be {@code null} nor {@code ""}.
     * @return all setter methods (not constructors) for the variable
     *         with name {@code variableName}. If none are found an
     *         empty {@code List} is returned.
     */
    public List<MethodNode> getSetterMethodsFor(final String variableName) {
        notEmpty(variableName);
        List<MethodNode> result = Collections.emptyList();
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            final Setters settersForVariable = variableSetters.get(variableNode);
            result = settersForVariable.methods();
        }
        return result;
    }

    /**
     * Removes all variables from this collection which are not associated with
     * any setters.
     * 
     * @return a {@code List} containing the removed unassociated variables.
     *         This list is empty if none were removed, i. e. the result is
     *         never {@code null}.
     */
    public List<FieldNode> removeUnassociatedVariables() {
        final List<FieldNode> result = new ArrayList<FieldNode>();
        for (final Entry<FieldNode, Setters> entry : variableSetters.entrySet()) {
            final Setters setters = entry.getValue();
            if (setters.isEmpty()) {
                result.add(entry.getKey());
            }
        }
        for (final FieldNode unassociatedVariable : result) {
            variableSetters.remove(unassociatedVariable);
        }
        return result;
    }

    public boolean isEmpty() {
        return variableSetters.isEmpty();
    }

    @Override
    public Iterator<Entry<FieldNode, Setters>> iterator() {
        final Set<Entry<FieldNode, Setters>> entrySet = new HashSet<Entry<FieldNode, Setters>>(variableSetters.size());
        for (final Entry<FieldNode, Setters> entry : variableSetters.entrySet()) {
            entrySet.add(entry);
        }
        return entrySet.iterator();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variableSetterMethods", variableSetters);
        return builder.toString();
    }

}
