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



import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Collection to hold relations of candidates and initialising methods
 * for those.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.02.2013
 */
@NotThreadSafe
final class CandidatesInitialisersMapping implements Iterable<CandidatesInitialisersMapping.Entry> {

    public interface Initialisers {
        boolean add(MethodNode initialiser);

        List<MethodNode> getConstructors();

        List<MethodNode> getMethods();
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
        public boolean add(final MethodNode initialiser) {
            return false;
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
        public String toString() {
            return getClass().getSimpleName() + " []";
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
        public List<MethodNode> getConstructors() {
            return Collections.unmodifiableList(constructors);
        }

        @Override
        public List<MethodNode> getMethods() {
            return Collections.unmodifiableList(methods);
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
            builder.append(", methods=").append(concatenateMethodNames(methods)).append(']');
            return builder.toString();
        }

        private static String concatenateMethodNames(final List<MethodNode> methods) {
            final StringBuilder result = new StringBuilder();
            result.append('[');
            final String separator = ", ";
            String sep = "";
            for (final MethodNode method : methods) {
                result.append(sep).append(method.name);
                sep = separator;
            }
            result.append(']');
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
            final String msgTemplate = "Argument '%s' must not be null!";
            candidate = checkNotNull(theCandidate, msgTemplate, "theCandidate");
            initialisers = checkNotNull(theInitialisers, msgTemplate, "theInitialisers");
        }

        @Override
        public FieldNode getCandidate() {
            return candidate;
        }

        @Override
        public Initialisers getInitialisers() {
            return initialisers;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + candidate.name.hashCode();
            result = prime * result + candidate.desc.hashCode();
            result = prime * result + initialisers.hashCode();
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
            if (!(obj instanceof DefaultEntry)) {
                return false;
            }
            final DefaultEntry other = (DefaultEntry) obj;
            if (!candidate.name.equals(other.candidate.name)) {
                return false;
            }
            if (!candidate.desc.equals(other.candidate.desc)) {
                return false;
            }
            if (!initialisers.equals(other.initialisers)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append(getClass().getSimpleName()).append(" [");
            b.append("candidate=").append(candidate.name);
            b.append(", initialisers=").append(initialisers);
            return b.toString();
        }
    } // class DefaultEntry


    private final ConcurrentMap<FieldNode, Initialisers> candidatesAndInitialisers;
    private final Map<String, Set<MethodNode>> visibleSetterMethods;

    private CandidatesInitialisersMapping() {
        this(new ConcurrentHashMap<FieldNode, Initialisers>());
    }

    private CandidatesInitialisersMapping(final Map<FieldNode, Initialisers> otherVariableSetters) {
        candidatesAndInitialisers = new ConcurrentHashMap<FieldNode, Initialisers>(otherVariableSetters);
        visibleSetterMethods = new HashMap<String, Set<MethodNode>>();
    }

    /**
     * @return a new instance of this class.
     */
    public static CandidatesInitialisersMapping newInstance() {
        return new CandidatesInitialisersMapping();
    }

    /**
     * @param candidate
     *            candidate to add to this collection. Must not be
     *            {@code null}.
     * @return {@code true} if the node was added, {@code false} if
     *         the node was already contained in this collection.
     */
    public boolean addCandidate(final FieldNode candidate) {
        checkNotNull(candidate);
        final boolean result = !candidatesAndInitialisers.containsKey(candidate);
        candidatesAndInitialisers.put(candidate, DefaultInitialisers.getInstance());
        return result;
    }

    /**
     * @param candidateName
     *            name of the candidate to associate the setter method
     *            with. Must neither be {@code null} nor {@code ""}.
     * @param initialiser
     *            initialiser to be associated with
     *            {@code candidateName} Must not be {@code null}.
     * @return {@code true} if {@code candidateName} was already part
     *         of this collection, {@code false} else.
     */
    public boolean addInitialiserForCandidate(final String candidateName, final MethodNode initialiser) {
        checkArgument(!candidateName.isEmpty());
        checkNotNull(initialiser);
        boolean result = false;
        final FieldNode candidate = getCandidateForName(candidateName);
        if (null != candidate) {
            result = addInitialiserForCandidate(candidate, initialiser);
        } else {
            if (isNotAConstructor(initialiser) && isNotPrivate(initialiser)) {
                addToVisibleSetterMethods(candidateName, initialiser);
            }
        }
        return result;
    }

    private FieldNode getCandidateForName(final String candidateName) {
        for (final Map.Entry<FieldNode, Initialisers> entry : candidatesAndInitialisers.entrySet()) {
            final FieldNode candidate = entry.getKey();
            if (candidate.name.equals(candidateName)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean addInitialiserForCandidate(final FieldNode candidate, final MethodNode initialiser) {
        final Initialisers initialisersForCandidate = candidatesAndInitialisers.get(candidate);
        return initialisersForCandidate.add(initialiser);
    }

    private static boolean isNotAConstructor(final MethodNode methodOrConstructor) {
        final String methodOrConstructorName = methodOrConstructor.name;
        return !MethodIs.aConstructor(methodOrConstructorName) && !"<clinit>".equals(methodOrConstructorName);
    }

    private static boolean isNotPrivate(final MethodNode method) {
        return field(method.access).isNotPrivate();
    }

    private void addToVisibleSetterMethods(final String candidateName, final MethodNode method) {
        final Set<MethodNode> setterMethods;
        if (visibleSetterMethods.containsKey(candidateName)) {
            setterMethods = visibleSetterMethods.get(candidateName);
            setterMethods.add(method);
        } else {
            setterMethods = new HashSet<MethodNode>();
            setterMethods.add(method);
            visibleSetterMethods.put(candidateName, setterMethods);
        }
    }

    Initialisers getInitialisersFor(final FieldNode candidate) {
        checkNotNull(candidate);
        Initialisers result = candidatesAndInitialisers.get(candidate);
        if (null == result) {
            result = NullInitialisers.getInstance();
        }
        return result;
    }

    /**
     * @param candidateName
     *            name of the candidate to get all initialising
     *            methods for. Must neither be {@code null} nor
     *            {@code ""}.
     * @return all initialising methods (not constructors) for the
     *         candidate with name {@code candidateName}. If none are
     *         found an empty {@code List} is returned.
     */
    Collection<MethodNode> getInitialisingMethodsFor(final String candidateName) {
        checkArgument(!candidateName.isEmpty());
        Collection<MethodNode> result = Collections.emptyList();
        final FieldNode candidate = getCandidateForName(candidateName);
        if (null != candidate) {
            final Initialisers settersForVariable = candidatesAndInitialisers.get(candidate);
            result = settersForVariable.getMethods();
        }
        return result;
    }

    public Map<String, Set<MethodNode>> getAllVisibleSetterMethods() {
        return Collections.unmodifiableMap(visibleSetterMethods);
    }

    public FieldNode removeAndGetCandidateForInitialisingMethod(final MethodNode initialisingMethod) {
        FieldNode result = null;
        final Set<Map.Entry<FieldNode, Initialisers>> entrySet = candidatesAndInitialisers.entrySet();
        final Iterator<Map.Entry<FieldNode, Initialisers>> iterator = entrySet.iterator();
        while (null == result && iterator.hasNext()) {
            final Map.Entry<FieldNode, Initialisers> entry = iterator.next();
            final Initialisers initialisers = entry.getValue();
            final List<MethodNode> initialisingMethods = initialisers.getMethods();
            if (initialisingMethods.contains(initialisingMethod)) {
                result = entry.getKey();
                iterator.remove();
            }
        }
        return result;
    }

    /**
     * Removes all candidates from this collection which are not
     * associated with an initialising method.
     * 
     * @return a {@code Collection} containing the removed
     *         unassociated candidates. This list is empty if none
     *         were removed, i. e. the result is never {@code null}.
     */
    public Collection<FieldNode> removeAndGetCandidatesWithoutInitialisingMethod() {
        final List<FieldNode> result = new ArrayList<FieldNode>();
        for (final Map.Entry<FieldNode, Initialisers> entry : candidatesAndInitialisers.entrySet()) {
            final Initialisers setters = entry.getValue();
            final List<MethodNode> initialisingMethods = setters.getMethods();
            if (initialisingMethods.isEmpty()) {
                result.add(entry.getKey());
            }
        }
        for (final FieldNode unassociatedVariable : result) {
            candidatesAndInitialisers.remove(unassociatedVariable);
        }
        return result;
    }

    @Override
    public Iterator<Entry> iterator() {
        final Set<Entry> entrySet = new HashSet<Entry>(candidatesAndInitialisers.size());
        for (final Map.Entry<FieldNode, Initialisers> entry : candidatesAndInitialisers.entrySet()) {
            entrySet.add(new DefaultEntry(entry.getKey(), entry.getValue()));
        }
        return entrySet.iterator();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + candidatesAndInitialisers.hashCode();
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
        if (!(o instanceof CandidatesInitialisersMapping)) {
            return false;
        }
        final CandidatesInitialisersMapping other = (CandidatesInitialisersMapping) o;
        if (!candidatesAndInitialisers.equals(other.candidatesAndInitialisers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("candidatesAndInitialisers=").append(candidatesAndInitialisersToString());
        builder.append(", visibleSetterMethods=").append(visibleSetterMethods).append(']');
        return builder.toString();
    }

    private String candidatesAndInitialisersToString() {
        final StringBuilder result = new StringBuilder();
        result.append('{');
        final String separator = ", ";
        String sep = "";
        for (final Entry entry : this) {
            final FieldNode candidate = entry.getCandidate();
            result.append(sep).append(candidate.name).append(": ").append(entry.getInitialisers());
            sep = separator;
        }
        result.append('}');
        return result.toString();
    }

}
