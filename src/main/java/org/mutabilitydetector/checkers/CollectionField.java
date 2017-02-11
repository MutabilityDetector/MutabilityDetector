package org.mutabilitydetector.checkers;

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


import com.google.common.base.Objects;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableList;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.Dotted.fromClass;

public abstract class CollectionField {
    public final Dotted collectionType;
    public final Node root;

    /**
     * Get flat list of generics tree represented by {@code root}
     */
    public List<GenericType> getGenericParameterTypes() {
        if (root.children.isEmpty()) {
            return null;
        }

        List<GenericType> genericParameters = new ArrayList<GenericType>();
        putAllChildren(root, genericParameters);
        return unmodifiableList(new ArrayList<GenericType>(genericParameters));
    }

    private void putAllChildren(Node root, List<GenericType> genericParameters) {
        for (Node child : root.children) {
            genericParameters.add(child.type);
            putAllChildren(child, genericParameters);
        }
    }

    private static final class RawCollection extends CollectionField {
        private RawCollection(Dotted collectionType) {
            super(collectionType, Node.dummyRoot());
        }

        @Override
        public boolean isGeneric() {
            return false;
        }

        public String asString() {
            return "raw " + collectionType.asString();
        }

        @Override
        public String asSimpleString() {
            return "raw " + collectionType.asSimpleString();
        }

        @Override
        public CollectionField transformGenericTree(Function<GenericType, GenericType> function) {
            return this;
        }
    }

    private static final class GenericCollection extends CollectionField {
        public GenericCollection(Dotted collectionType, Node root) {
            super(collectionType, root);
        }

        @Override
        public boolean isGeneric() {
            return true;
        }

        public String asString() {
            return root.asString();
        }

        @Override
        public String asSimpleString() {
            return root.asSimpleString();
        }

        @Override
        public CollectionField transformGenericTree(Function<GenericType, GenericType> function) {
            Node newRoot = transformNode(root, function);
            return new GenericCollection(collectionType, newRoot);
        }

        private Node transformNode(Node node, Function<GenericType, GenericType> function) {
            Node transformed = new Node(function.apply(node.type));
            for (Node child : node.children) {
                transformed.addChild(transformNode(child, function));
            }
            
            return transformed;
        }
    }

    public abstract boolean isGeneric();

    public abstract String asString();

    /**
     * Similar to {@code CollectionField#asString}, but uses unqualified class names
     */
    public abstract String asSimpleString();

    protected CollectionField(Dotted collectionType, Node root) {
        this.collectionType = collectionType;
        this.root = root;
    }

    public static CollectionField from(String collectionType, String signature) {
        if (signature == null) {
            return new RawCollection(dotted(collectionType));
        }
        GenericCollectionVisitor collectionTypeReader = new GenericCollectionVisitor(dotted(collectionType));

        new SignatureReader(signature).accept(collectionTypeReader);

        return new GenericCollection(collectionTypeReader.state.collectionType,
                collectionTypeReader.root);
    }

    /**
     * Apply function to all generics tree nodes and return resulting tree
     */
    public abstract CollectionField transformGenericTree(Function<GenericType, GenericType> function);

    /**
     * Constructs generics tree by visiting signature
     */
    private static class GenericCollectionVisitor extends SignatureVisitor {
        private GenericCollectionReaderState state;
        private Node root;
        private Node lastStored;

        public GenericCollectionVisitor(Dotted collectionType) {
            super(Opcodes.ASM5);
            state = new GenericCollectionReaderState();
            root = new Node(GenericType.exact(collectionType));
        }

        private GenericCollectionVisitor(GenericCollectionReaderState state, Node root) {
            super(Opcodes.ASM5);
            this.state = state;
            this.root = root;
        }

        @Override
        public void visitClassType(String name) {
            if (!state.seenOuterCollectionType) {
                state.collectionType = dotted(name);
                state.seenOuterCollectionType = true;
            } else {
                state.elementType = dotted(name);
                storeNode();
            }
        }

        @Override
        public void visitTypeVariable(String name) {
            state.typeVariable = dotted(name);
            storeNode();
        }

        @Override
        public void visitTypeArgument() {
            state.wildcard = "?";
            state.elementType = null;
            storeNode();
        }

        @Override
        public void visitBaseType(char descriptor) {
            if (state.isElementTypeArray) {
                state.elementType = dotted("[" + descriptor);
                storeNode();
            } else {
                throw new IllegalStateException("It shouldn't happen. Java doesn't support primitive generic types");
            }
        }

        @Override
        public SignatureVisitor visitArrayType() {
            state.isElementTypeArray = true;
            return withRoot(firstNonNull(lastStored, root));
        }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            state.wildcard = valueOf(wildcard);

            return withRoot(firstNonNull(lastStored, root));
        }

        private void storeNode() {
            lastStored = new Node(createGenericType());
            root.addChild(lastStored);
        }

        private GenericType createGenericType() {
            boolean isVariable = state.typeVariable != null;
            return new GenericType(isVariable ? state.typeVariable : state.elementType, state.wildcard, isVariable);
        }

        /**
         * Return visitor representing child node with the same state
         */
        private GenericCollectionVisitor withRoot(Node newRoot) {
            return new GenericCollectionVisitor(state, newRoot);
        }

        private static final class GenericCollectionReaderState {
            protected Dotted collectionType;
            protected Dotted elementType;
            protected Dotted typeVariable;
            protected String wildcard;
            protected boolean seenOuterCollectionType = false;
            boolean isElementTypeArray = false;

        }
    }

    public static class GenericType {
        public final Dotted type;
        public final String wildcard;
        public final boolean isVariable;

        public GenericType(Dotted type, String wildcard, boolean isVariable) {
            this.type = type;
            this.wildcard = checkNotNull(wildcard, "wildcard");
            this.isVariable = isVariable;
        }

        public static GenericType wildcard() {
            return new GenericType(null, "?", false);
        }

        public static GenericType exact(Dotted type) {
            return new GenericType(type, "=", false);
        }

        public static GenericType extends_(Dotted type) {
            return new GenericType(type, "+", false);
        }

        public static GenericType super_(Dotted type) {
            return new GenericType(type, "-", false);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type, wildcard);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GenericType other = (GenericType) obj;
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            if (wildcard == null) {
                if (other.wildcard != null) {
                    return false;
                }
            } else if (!wildcard.equals(other.wildcard)) {
                return false;
            }
            return true;
        }


        @Override
        public String toString() {
            return toStringWithFunction(Object::toString);
        }

        /**
         * Similar to {@code GenericType#asString} but uses unqualified class names
         */
        public String asSimpleString() {
            return toStringWithFunction(Dotted::asSimpleString);
        }

        private String toStringWithFunction(Function<? super Dotted, String> toStringFunction) {
            if (type == null) {
                return wildcard;
            } else {
                if (wildcard.equals("=")) {
                    return toStringFunction.apply(type);
                } else if (wildcard.equals("+")) {
                    return "? extends " + toStringFunction.apply(type);
                } else if (wildcard.equals("-")) {
                    return "? super " + toStringFunction.apply(type);
                }
            }

            throw new IllegalStateException();
        }

        public GenericType withoutWildcard() {
            if ("?".equals(wildcard)) {
                return new GenericType(fromClass(Object.class), "=", false);
            }
            return new GenericType(type, "=", false);
        }
    }

    /**
     * Represents a node of generic types tree in type declaration
     */
    private static final class Node {
        public final List<Node> children = new ArrayList<Node>();
        public final GenericType type;

        public static Node dummyRoot() {
            return new Node(null);
        }

        public Node(GenericType type) {
            this.type = type;
        }

        public void addChild(Node child) {
            children.add(checkNotNull(child));
        }

        public String asString() {
            return asStringUsingFunctions(Object::toString, Object::toString);
        }

        /**
         * Similar to {@code GenericType#asString} but uses unqualified class names
         */
        public String asSimpleString() {
            return asStringUsingFunctions(GenericType::asSimpleString, Node::asSimpleString);
        }

        private String asStringUsingFunctions(Function<? super GenericType, String> genericTypeStringFunction,
                                              Function<? super Node, String> nodeStringFunction) {
            String childrenPart = "";
            if (children.size() > 0) {
                childrenPart = children.stream()
                        .map(nodeStringFunction)
                        .collect(Collectors.joining(", ", "<", ">"));
            }

            return genericTypeStringFunction.apply(type) + childrenPart;
        }

        @Override
        public String toString() {
            return asString();
        }
    }
}
