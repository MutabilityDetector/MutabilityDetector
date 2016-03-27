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

import java.util.*;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.mutabilitydetector.checkers.settermethod.ControlFlowBlock.ControlFlowBlockFactory;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.02.2013
 */
@ThreadSafe
final class ControlFlowBlockCache {

    private final String owner;
    @GuardedBy("this") private final Map<String, MethodNode> methodNodes;
    @GuardedBy("this") private final Map<String, List<ControlFlowBlock>> controlFlowBlocks;

    private ControlFlowBlockCache(final String theOwner) {
        owner = theOwner;
        methodNodes = new HashMap<String, MethodNode>();
        controlFlowBlocks = new HashMap<String, List<ControlFlowBlock>>();
    }

    public static ControlFlowBlockCache newInstance(final String owner) {
        checkArgument(!owner.isEmpty());
        return new ControlFlowBlockCache(owner);
    }

    public List<ControlFlowBlock> getControlFlowBlocksForMethod(final MethodNode methodNode) {
        final List<ControlFlowBlock> result;
        if (null == methodNode) {
            result = Collections.emptyList();
        } else {
            final String mapKey = toMapKey(methodNode);
            if (controlFlowBlocks.containsKey(mapKey)) {
                result = controlFlowBlocks.get(mapKey);
            } else {
                result = createAndAddToCache(mapKey, methodNode);
            }
        }
        return result;
    }

    private static String toMapKey(final MethodNode methodNode) {
        final String keyTemplate = "%s:%s";
        return String.format(keyTemplate, methodNode.name, methodNode.desc);
    }

    private synchronized List<ControlFlowBlock> createAndAddToCache(final String mapKey, final MethodNode methodNode) {
        methodNodes.put(mapKey, methodNode);
        final ControlFlowBlockFactory f = ControlFlowBlockFactory.newInstance(owner, methodNode);
        final List<ControlFlowBlock> allControlFlowBlocksForMethod = f.getAllControlFlowBlocksForMethod();
        final List<ControlFlowBlock> result = Collections.unmodifiableList(allControlFlowBlocksForMethod);
        controlFlowBlocks.put(mapKey, result);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" owner=").append(owner);
        b.append(", controlFlowBlocks=").append(controlFlowBlocks).append(']');
        return b.toString();
    }

}
