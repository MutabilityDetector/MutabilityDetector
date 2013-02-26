package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;
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

    public interface Setters {

        boolean add(MethodNode setter);

        boolean isEmpty();

        List<MethodNode> constructors();

        List<MethodNode> methods();

    } // interface Setters


    @Immutable
    private static final class NullSetters implements Setters {
        private static final NullSetters INSTANCE = new NullSetters();

        private NullSetters() {
            super();
        }

        public static Setters getInstance() {
            return INSTANCE;
        }

        @Override
        public boolean add(final MethodNode setter) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<MethodNode> constructors() {
            return Collections.emptyList();
        }

        @Override
        public List<MethodNode> methods() {
            return Collections.emptyList();
        }

        @Override
        public String toString() {
            return "NullSetters []";
        }

    } // class NullSetters


    @NotThreadSafe
    private static final class SettersDefault implements Setters {
        private final List<MethodNode> constructors;
        private final List<MethodNode> methods;

        private SettersDefault() {
            final byte initialSize = 2;
            constructors = new ArrayList<MethodNode>(initialSize);
            methods = new ArrayList<MethodNode>(initialSize);
        }

        public static Setters getInstance() {
            return new SettersDefault();
        }

        @Override
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

        @Override
        public boolean isEmpty() {
            return constructors.isEmpty() && methods.isEmpty();
        }

        @Override
        public List<MethodNode> constructors() {
            return Collections.unmodifiableList(constructors);
        }

        @Override
        public List<MethodNode> methods() {
            return Collections.unmodifiableList(methods);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(getClass().getSimpleName()).append(" [");
            builder.append("constructors=").append(concatenateMethodNames(constructors));
            builder.append(", methods=").append(concatenateMethodNames(methods)).append("]");
            return builder.toString();
        }

        private String concatenateMethodNames(final List<MethodNode> methods) {
            final StringBuilder result = new StringBuilder();
            result.append("[");
            final String separator = ", ";
            String sep = "";
            for (final MethodNode method : methods) {
                result.append(sep).append(method.name);
                sep = separator;
            }
            result.append("]");
            return result.toString();
            
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
        variableSetters.put(variableNode, SettersDefault.getInstance());
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

    public List<FieldNode> getVariables() {
        return new ArrayList<FieldNode>(variableSetters.keySet());
    }

    public Setters getSettersFor(final FieldNode variable) {
        notNull(variable);
        Setters result = variableSetters.get(variable);
        if (null == result) {
            result = NullSetters.getInstance();
        }
        return result;
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
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("variableSetterMethods=").append(variableSettersToString()).append("]");
        return builder.toString();
    }

    private String variableSettersToString() {
        final StringBuilder result = new StringBuilder();
        result.append("{");
        final String separator = ", ";
        String sep = "";
        for (final Entry<FieldNode, Setters> entry : variableSetters.entrySet()) {
            final FieldNode variable = entry.getKey();
            result.append(sep).append(variable.name).append(": ").append(entry.getValue());
            sep = separator;
        }
        result.append("}");
        return result.toString();
    }

}
