package org.mutabilitydetector.checkers.settermethod;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Collection to hold relations of variables and initialising methods for those.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.02.2013
 */
@NotThreadSafe
final class VariableInitialisersAssociation implements Iterable<VariableInitialisersAssociation.Entry> {

    public interface Initialisers {

        boolean add(MethodNode setter);

        boolean isEmpty();

        List<MethodNode> getConstructors();

        List<MethodNode> getMethods();

        Initialisers copy();

    } // interface Setters

    @Immutable
    private static final class NullInitialisers implements Initialisers {
        private static final NullInitialisers INSTANCE = new NullInitialisers();

        private NullInitialisers() {
            super();
        }

        public static Initialisers getInstance() {
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
        public List<MethodNode> getConstructors() {
            return Collections.emptyList();
        }

        @Override
        public List<MethodNode> getMethods() {
            return Collections.emptyList();
        }

        @Override
        public Initialisers copy() {
            return this;
        }

        @Override
        public String toString() {
            return "NullSetters []";
        }

    } // class NullSetters

    @NotThreadSafe
    private static final class DefaultInitialisers implements Initialisers {
        private final List<MethodNode> constructors;
        private final List<MethodNode> methods;

        private DefaultInitialisers() {
            final byte initialSize = 2;
            constructors = new ArrayList<MethodNode>(initialSize);
            methods = new ArrayList<MethodNode>(initialSize);
        }

        public static Initialisers getInstance() {
            return new DefaultInitialisers();
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
        public List<MethodNode> getConstructors() {
            return Collections.unmodifiableList(constructors);
        }

        @Override
        public List<MethodNode> getMethods() {
            return Collections.unmodifiableList(methods);
        }

        @Override
        public Initialisers copy() {
            final DefaultInitialisers result = new DefaultInitialisers();
            result.constructors.addAll(constructors);
            result.methods.addAll(methods);
            return result;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + constructors.hashCode();
            result = prime * result + methods.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DefaultInitialisers)) {
                return false;
            }
            final DefaultInitialisers other = (DefaultInitialisers) obj;
            if (!constructors.equals(other.constructors)) {
                return false;
            }
            if (!methods.equals(other.methods)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(getClass().getSimpleName()).append(" [");
            builder.append("constructors=").append(concatenateMethodNames(constructors));
            builder.append(", methods=").append(concatenateMethodNames(methods)).append("]");
            return builder.toString();
        }

        private static String concatenateMethodNames(final List<MethodNode> methods) {
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

    } // class DefaultInitialisers


    public interface Entry {

        FieldNode getCandidate();

        Initialisers getInitialisers();

    } // interface Entry


    private static final class DefaultEntry implements Entry {
        private final FieldNode candidate;
        private final Initialisers initialisers;

        public DefaultEntry(final FieldNode theCandidate, final Initialisers theInitialisers) {
            candidate = theCandidate;
            initialisers = theInitialisers;
        }

        @Override
        public FieldNode getCandidate() {
            return candidate;
        }

        @Override
        public Initialisers getInitialisers() {
            return initialisers;
        }
    } // class DefaultEntry


    private final ConcurrentMap<FieldNode, Initialisers> variableInitialisers;

    private VariableInitialisersAssociation() {
        this(new ConcurrentHashMap<FieldNode, Initialisers>());
    }

    private VariableInitialisersAssociation(final Map<FieldNode, Initialisers> otherVariableSetters) {
        variableInitialisers = new ConcurrentHashMap<FieldNode, Initialisers>(otherVariableSetters);
    }

    /**
     * @return a new instance of this class.
     */
    public static VariableInitialisersAssociation newInstance() {
        return new VariableInitialisersAssociation();
    }

    /**
     * @param variableNode
     *            variable to add to this collection. Must not be
     *            {@code null}.
     * @return {@code true} if the node was added, {@code false} if
     *         the node was already contained in this collection.
     */
    public boolean addVariable(final FieldNode variableNode) {
        notNull(variableNode);
        final boolean result = !variableInitialisers.containsKey(variableNode);
        variableInitialisers.put(variableNode, DefaultInitialisers.getInstance());
        return result;
    }

    /**
     * @param variableName
     *            name of the variable to associate the setter method with. Must
     *            neither be {@code null} nor {@code ""}.
     * @param initialiser
     *            initialiser to be associated with {@code variableName} Must
     *            not be {@code null}.
     * @return {@code true} if {@code variableName} was already part of this
     *         collection, {@code false} else.
     */
    public boolean addInitialiserForVariable(final String variableName, final MethodNode initialiser) {
        notEmpty(variableName);
        notNull(initialiser);
        boolean result = false;
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            result = addSetterForVariable(variableNode, initialiser);
        }
        return result;
    }

    private FieldNode getVariableNodeForName(final String variableName) {
        for (final Map.Entry<FieldNode, Initialisers> entry : variableInitialisers.entrySet()) {
            final FieldNode variableNode = entry.getKey();
            if (variableNode.name.equals(variableName)) {
                return variableNode;
            }
        }
        return null;
    }

    private boolean addSetterForVariable(final FieldNode variableNode, final MethodNode setter) {
        final Initialisers settersForvariable = variableInitialisers.get(variableNode);
        return settersForvariable.add(setter);
    }

    public List<FieldNode> getVariables() {
        return new ArrayList<FieldNode>(variableInitialisers.keySet());
    }

    public Initialisers getInitialisersFor(final FieldNode variable) {
        notNull(variable);
        Initialisers result = variableInitialisers.get(variable);
        if (null == result) {
            result = NullInitialisers.getInstance();
        }
        return result;
    }

    /**
     * @param variableName
     *            name of the variable to get all initialising methods for. Must
     *            neither be {@code null} nor {@code ""}.
     * @return all initialising methods (not constructors) for the variable with
     *         name {@code variableName}. If none are found an empty
     *         {@code List} is returned.
     */
    public Collection<MethodNode> getInitialisingMethodsFor(final String variableName) {
        notEmpty(variableName);
        Collection<MethodNode> result = Collections.emptyList();
        final FieldNode variableNode = getVariableNodeForName(variableName);
        if (null != variableNode) {
            final Initialisers settersForVariable = variableInitialisers.get(variableNode);
            result = settersForVariable.getMethods();
        }
        return result;
    }

    /**
     * Removes all variables from this collection which are not associated with
     * any setters.
     * 
     * @return a {@code Collection} containing the removed unassociated
     *         variables. This list is empty if none were removed, i. e. the
     *         result is never {@code null}.
     */
    public Collection<FieldNode> removeAndGetUnassociatedVariables() {
        final List<FieldNode> result = new ArrayList<FieldNode>();
        for (final Map.Entry<FieldNode, Initialisers> entry : variableInitialisers.entrySet()) {
            final Initialisers setters = entry.getValue();
            if (setters.isEmpty()) {
                result.add(entry.getKey());
            }
        }
        for (final FieldNode unassociatedVariable : result) {
            variableInitialisers.remove(unassociatedVariable);
        }
        return result;
    }

    public boolean isEmpty() {
        return variableInitialisers.isEmpty();
    }

    public int getSize() {
        return variableInitialisers.size();
    }

    @Override
    public Iterator<Entry> iterator() {
        final Set<Entry> entrySet = new HashSet<Entry>(variableInitialisers.size());
        for (final Map.Entry<FieldNode, Initialisers> entry : variableInitialisers.entrySet()) {
            entrySet.add(new DefaultEntry(entry.getKey(), entry.getValue()));
        }
        return entrySet.iterator();
    }

    public VariableInitialisersAssociation copy() {
        final VariableInitialisersAssociation result = new VariableInitialisersAssociation();
        for (final Map.Entry<FieldNode, Initialisers> entry : variableInitialisers.entrySet()) {
            final FieldNode variable = entry.getKey();
            final Initialisers setters = entry.getValue();
            result.variableInitialisers.put(variable, setters.copy());
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + variableInitialisers.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof VariableInitialisersAssociation)) {
            return false;
        }
        final VariableInitialisersAssociation other = (VariableInitialisersAssociation) o;
        if (!variableInitialisers.equals(other.variableInitialisers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("variableInitialisers=").append(variableInitialisersToString()).append("]");
        return builder.toString();
    }

    private String variableInitialisersToString() {
        final StringBuilder result = new StringBuilder();
        result.append("{");
        final String separator = ", ";
        String sep = "";
        for (final Entry entry : this) {
            final FieldNode variable = entry.getCandidate();
            result.append(sep).append(variable.name).append(": ").append(entry.getInitialisers());
            sep = separator;
        }
        result.append("}");
        return result.toString();
    }

}
