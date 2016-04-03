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

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.*;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 17.02.2013
 */
@NotThreadSafe
final class EnhancedClassNode {

    private final ClassNode classNode;
    private final ControlFlowBlockCache cfbCache;

    private EnhancedClassNode(final ClassNode theClassNode) {
        classNode = theClassNode;
        cfbCache = ControlFlowBlockCache.newInstance(theClassNode.name);
    }

    public static EnhancedClassNode newInstance(final ClassNode classNode) {
        return new EnhancedClassNode(checkNotNull(classNode));
    }

    public String getName() {
        return classNode.name;
    }

    public List<FieldNode> getFields() {
        return classNode.fields;
    }

    public List<MethodNode> getMethods() {
        return classNode.methods;
    }

    FieldNode findVariableWithName(final String variableName) {
        checkArgument(!variableName.isEmpty());
        for (final FieldNode variable : getFields()) {
            if (variableName.equals(variable.name)) {
                return variable;
            }
        }
        return null;
    }

    List<MethodNode> findMethodByName(final String methodName) {
        checkArgument(!methodName.isEmpty());
        final ArrayList<MethodNode> result = new ArrayList<MethodNode>();
        for (final MethodNode method : getMethods()) {
            if (methodName.equals(method.name)) {
                result.add(method);
            }
        }
        result.trimToSize();
        return result;
    }

    MethodNode findMethodByDescriptor(final String methodName, final Type returnType, final Type... argumentTypes) {
        checkArgument(!methodName.isEmpty(), "Parameter 'methodName' must neither be null nor empty!");
        checkNotNull(returnType, "Parameter 'returnType' must not be null!");
        final String desc = createDescriptorFor(methodName, returnType, argumentTypes);
        return findMethodByDescriptor(methodName, desc);
    }

    MethodNode findMethodByDescriptor(final String methodName, final String descriptor) {
        checkArgument(!methodName.isEmpty(), "Parameter 'methodName' must neither be null nor empty!");
        checkArgument(!descriptor.isEmpty(), "Parameter 'descriptor' must neither be null nor empty!");
        for (final MethodNode method : getMethods()) {
            if (method.name.equals(methodName) && method.desc.equals(descriptor)) {
                return method;
            }
        }
        return null;
    }

    private static String createDescriptorFor(final String methodName, final Type returnType,
            final Type[] argumentTypes) {
        final Method method = new Method(methodName, returnType, argumentTypes);
        return method.getDescriptor();
    }

    /**
     * Delivers all {@link ControlFlowBlock}s of the given
     * {@link MethodNode}.
     * 
     * @param method
     *            the method. Must not be {@code null}.
     * @return a {@link List} containing all {@link ControlFlowBlock}s
     *         of {@code method}. Is never {@code null}.
     * @see #findMethodByDescriptor(String, Type, Type...)
     * @see #findMethodByName(String)
     * @see #getControlFlowBlocksForMethod(String, Type, Type...)
     */
    public List<ControlFlowBlock> getControlFlowBlocksForMethod(final MethodNode method) {
        return cfbCache.getControlFlowBlocksForMethod(method);
    }

    /**
     * Delivers all {@link ControlFlowBlock}s of a method which is
     * described by its name, its return type as well as its
     * argumentTypes.
     * 
     * @param methodName
     *            name of the method. Must neither be {@code null} nor
     *            empty.
     * @param returnType
     *            the {@link Type} of the return value of the
     *            described method. Must not be {@code null}.
     * @param argumentTypes
     *            (optional) the types of the method's arguments.
     * @return a {@link List} containing all {@link ControlFlowBlock}s
     *         of the method which is described by {@code methodName},
     *         {@code returnType} and {@code argumentTypes}. Is never
     *         {@code null}.
     * @see #getControlFlowBlocksForMethod(MethodNode)
     */
    List<ControlFlowBlock> getControlFlowBlocksForMethod(final String methodName,
            final Type returnType,
            final Type... argumentTypes) {
        final MethodNode method = findMethodByDescriptor(methodName, returnType, argumentTypes);
        return getControlFlowBlocksForMethod(method);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [").append("classNode=").append(classNode);
        builder.append(", cfbCache=").append(cfbCache).append(']');
        return builder.toString();
    }

}
