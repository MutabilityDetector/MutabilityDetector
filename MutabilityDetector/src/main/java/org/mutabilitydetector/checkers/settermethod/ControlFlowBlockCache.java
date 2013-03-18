package org.mutabilitydetector.checkers.settermethod;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.mutabilitydetector.checkers.settermethod.ControlFlowBlock.ControlFlowBlockFactory;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.02.2013
 */
@ThreadSafe
final class ControlFlowBlockCache implements Iterable<Map.Entry<MethodNode, Collection<ControlFlowBlock>>> {

    private static final class IteratorEntry implements Entry<MethodNode, Collection<ControlFlowBlock>> {

        private final MethodNode key;
        private final Collection<ControlFlowBlock> value;

        public IteratorEntry(final MethodNode theKey, final Collection<ControlFlowBlock> theValue) {
            key = theKey;
            value = theValue;
        }

        @Override
        public MethodNode getKey() {
            return key;
        }

        @Override
        public Collection<ControlFlowBlock> getValue() {
            return value;
        }

        @Override
        public Collection<ControlFlowBlock> setValue(final Collection<ControlFlowBlock> value) {
            return value;
        }

    } // class IteratorEntry


    private static final class CfbIterator implements Iterator<Map.Entry<MethodNode, Collection<ControlFlowBlock>>> {

        private final List<Entry<MethodNode, Collection<ControlFlowBlock>>> entries;
        private final AtomicInteger currentPosition;

        public CfbIterator() {
            entries = new ArrayList<Entry<MethodNode, Collection<ControlFlowBlock>>>();
            currentPosition = new AtomicInteger(0);
        }

        void addEntry(final MethodNode key, final Collection<ControlFlowBlock> value) {
            final Entry<MethodNode, Collection<ControlFlowBlock>> entry = new IteratorEntry(key, value);
            entries.add(entry);
        }

        @Override
        public boolean hasNext() {
            final int amountOfEntries = entries.size();
            return currentPosition.get() < amountOfEntries;
        }

        @Override
        public Entry<MethodNode, Collection<ControlFlowBlock>> next() {
            return entries.get(currentPosition.getAndIncrement());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    } // class CfbIterator


    private final String owner;
    @GuardedBy("this") private final Map<String, MethodNode> methodNodes;
    @GuardedBy("this") private final Map<String, List<ControlFlowBlock>> controlFlowBlocks;

    private ControlFlowBlockCache(final String theOwner) {
        owner = theOwner;
        methodNodes = new HashMap<String, MethodNode>();
        controlFlowBlocks = new HashMap<String, List<ControlFlowBlock>>();
    }

    public static ControlFlowBlockCache newInstance(final String owner) {
        return new ControlFlowBlockCache(notEmpty(owner));
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
    public Iterator<Entry<MethodNode, Collection<ControlFlowBlock>>> iterator() {
        final CfbIterator result = new CfbIterator();
        for (final Entry<String, List<ControlFlowBlock>> controlFlowBlocksEntry : controlFlowBlocks.entrySet()) {
            final String mapKey = controlFlowBlocksEntry.getKey();
            final MethodNode method = getMethodForKey(mapKey);
            final List<ControlFlowBlock> controlFlowBlocks = controlFlowBlocksEntry.getValue();
            result.addEntry(method, controlFlowBlocks);
        }
        return result;
    }

    private MethodNode getMethodForKey(final String mapKey) {
        return methodNodes.get(mapKey);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" owner=").append(owner);
        b.append(", controlFlowBlocks=").append(controlFlowBlocks).append("]");
        return b.toString();
    }

}
