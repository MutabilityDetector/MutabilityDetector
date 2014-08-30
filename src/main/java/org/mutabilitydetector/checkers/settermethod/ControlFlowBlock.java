/**
 * 
 */package org.mutabilitydetector.checkers.settermethod;

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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

/**
 * Range of a method's instructions which is delimited by label nodes.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.02.2013
 */
@ThreadSafe
final class ControlFlowBlock implements Comparable<ControlFlowBlock> {

    @Immutable
    static final class Range {

        /** Value of this range's lower boundary. */
        public final int lowerBoundary;

        /** Value of this range's upper boundary. */
        public final int upperBoundary;

        /** All items of this range. */
        public final List<Integer> allItems;

        private Range(final int theLowerBoundary, final int theUpperBoundary, final List<Integer> allItems) {
            lowerBoundary = theLowerBoundary;
            upperBoundary = theUpperBoundary;
            this.allItems = Collections.unmodifiableList(allItems);
        }

        public static Range newInstance(final SortedSet<Integer> allItems) {
            final List<Integer> allItemsList = new ArrayList<Integer>(allItems.size());
            for (final Integer item : new TreeSet<Integer>(allItems)) {
                allItemsList.add(item);
            }
            final Integer lowerBoundary = allItemsList.isEmpty() ? -1 : allItemsList.get(0);
            final Integer upperBoundary = allItemsList.isEmpty() ? -1 : allItemsList.get(allItemsList.size() - 1);
            return new Range(lowerBoundary, upperBoundary, allItemsList);
        }

        public boolean covers(final int index) {
            return allItems.contains(Integer.valueOf(index));
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + allItems.hashCode();
            result = prime * result + lowerBoundary;
            result = prime * result + upperBoundary;
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
            if (!(obj instanceof Range)) {
                return false;
            }
            final Range other = (Range) obj;
            if (!allItems.equals(other.allItems)) {
                return false;
            }
            if (lowerBoundary != other.lowerBoundary) {
                return false;
            }
            if (upperBoundary != other.upperBoundary) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(getClass().getSimpleName());
            builder.append(" [lowerBoundary=").append(lowerBoundary).append(", upperBoundary=").append(upperBoundary);
            builder.append(", allItems=").append(allItems).append("]");
            return builder.toString();
        }
    } // class Range


    @NotThreadSafe
    private static final class Builder {
        private final int blockNumber;
        private final String identifier;
        private final InsnList allInstructions;
        private final SortedSet<Integer> rangeItems;

        public Builder(final int theBlockNumber, final String theIdentifier, final InsnList allInstructions) {
            checkArgument(!theIdentifier.isEmpty());
            blockNumber = theBlockNumber;
            identifier = theIdentifier;
            this.allInstructions = checkNotNull(allInstructions);
            rangeItems = new TreeSet<Integer>();
        }

        public void addInstruction(final int instructionIndex) {
            rangeItems.add(Integer.valueOf(instructionIndex));
        }

        public ControlFlowBlock build() {
            final Range range = Range.newInstance(rangeItems);
            return ControlFlowBlock.newInstance(blockNumber, identifier, allInstructions, range);
        }

    } // class Builder


    @ThreadSafe
    public static final class ControlFlowBlockFactory {
        private final String owner;
        private final MethodNode method;
        private final InsnList allInstructions;
        private final List<ControlFlowBlock> controlFlowBlocks;
        private final AtomicInteger currentBlockNumber;

        private final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected void newControlFlowEdge(final int src, final int dest) {
                interlinkControlFlowBlocks(src, dest);
            }

            private void interlinkControlFlowBlocks(final int src, final int dest) {
                ControlFlowBlock srcBlock = null;
                ControlFlowBlock destBlock = null;
                for (final ControlFlowBlock b : controlFlowBlocks) {
                    if (b.covers(src)) {
                        srcBlock = b;
                    } else if (b.covers(dest)) {
                        destBlock = b;
                    }
                }
                if (null != srcBlock && null != destBlock) {
                    srcBlock.successors.add(destBlock);
                    destBlock.predecessors.add(srcBlock);
                }
            }
        };

        private ControlFlowBlockFactory(final String theOwner, final MethodNode theMethod) {
            method = theMethod;
            owner = theOwner;
            allInstructions = theMethod.instructions;
            controlFlowBlocks = new ArrayList<ControlFlowBlock>();
            currentBlockNumber = new AtomicInteger(0);
        }

        public static ControlFlowBlockFactory newInstance(final String owner, final MethodNode method) {
            checkArgument(!owner.isEmpty());
            final ControlFlowBlockFactory result = new ControlFlowBlockFactory(owner, checkNotNull(method));
            result.createAllControlFlowBlockBuilders();
            result.analyseMethod();
            return result;
        }

        private void createAllControlFlowBlockBuilders() {
            final Builder builder = handleFirstInstruction();
            handleRemainingInstructions(builder);
        }

        private Builder handleFirstInstruction() {
            final Builder result;
            final AbstractInsnNode firstInsn = allInstructions.get(currentBlockNumber.get());
            if (isLabel(firstInsn)) {
                result = createNewControlFlowBlockBuilderForLabel(firstInsn);
            } else {
                result = new Builder(currentBlockNumber.getAndIncrement(), "L<Pseudo>", allInstructions);
            }
            result.addInstruction(0);
            return result;
        }

        private static boolean isLabel(final AbstractInsnNode insn) {
            return AbstractInsnNode.LABEL == insn.getType();
        }

        private Builder createNewControlFlowBlockBuilderForLabel(final AbstractInsnNode insn) {
            final LabelNode labelNode = (LabelNode) insn;
            final Label label = labelNode.getLabel();
            return new Builder(currentBlockNumber.getAndIncrement(), label.toString(), allInstructions);
        }

        private void handleRemainingInstructions(final Builder controlFlowBlockBuilder) {
            Builder builder = controlFlowBlockBuilder;
            for (int i = 1; i < allInstructions.size(); i++) {
                final AbstractInsnNode insn = allInstructions.get(i);
                if (isLabel(insn)) {
                    controlFlowBlocks.add(builder.build());
                    builder = createNewControlFlowBlockBuilderForLabel(insn);
                }
                builder.addInstruction(i);
            }
            controlFlowBlocks.add(builder.build());
        }

        private void analyseMethod() {
            tryToAnalyseMethod();
        }

        private void tryToAnalyseMethod() {
            try {
                analyser.analyze(owner, method);
            } catch (final AnalyzerException e) {
                e.printStackTrace();
            }
        }

        public List<ControlFlowBlock> getAllControlFlowBlocksForMethod() {
            final ArrayList<ControlFlowBlock> result = new ArrayList<ControlFlowBlock>(controlFlowBlocks.size());
            for (final ControlFlowBlock b : controlFlowBlocks) {
                if (b.isNotEmpty()) {
                    result.add(b);
                }
            }
            result.trimToSize();
            return result;
        }
    } // class ControlFlowFactory


    private final int blockNumber;
    private final String identifier;
    private final InsnList methodInstructions;
    private final Range rangeOfBlockInstructions;
    private final Set<ControlFlowBlock> predecessors;
    private final Set<ControlFlowBlock> successors;
    private int hashCode;
    private String stringRepresentation;

    private ControlFlowBlock(final int theBlockNumber,
            final String theIdentifier,
            final InsnList theMethodInstructions,
            final Range theRangeOfBlockInstructionIndices) {
        blockNumber = theBlockNumber;
        identifier = theIdentifier;
        methodInstructions = theMethodInstructions;
        rangeOfBlockInstructions = theRangeOfBlockInstructionIndices;
        predecessors = new HashSet<ControlFlowBlock>();
        successors = new HashSet<ControlFlowBlock>();
        hashCode = 0;
        stringRepresentation = null;
    }

    public static ControlFlowBlock newInstance(final int blockNumber,
            final String identifier,
            final InsnList methodInstructions,
            final Range rangeOfInstructionIndices) {
        checkArgument(!identifier.isEmpty());
        return new ControlFlowBlock(blockNumber, identifier, checkNotNull(methodInstructions),
                checkNotNull(rangeOfInstructionIndices));
    }

    public boolean isEmpty() {
        final boolean result;
        final List<AbstractInsnNode> blockInstructions = getBlockInstructions();
        if (blockInstructions.isEmpty()) {
            result = true;
        } else {
            if (1 == blockInstructions.size()) {
                final AbstractInsnNode soleBlockInstruction = blockInstructions.get(0);
                result = AbstractInsnNode.LABEL == soleBlockInstruction.getType();
            } else {
                result = false;
            }
        }
        return result;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public List<AbstractInsnNode> getBlockInstructions() {
        final ArrayList<AbstractInsnNode> result = new ArrayList<AbstractInsnNode>();
        for (int i = rangeOfBlockInstructions.lowerBoundary; i <= rangeOfBlockInstructions.upperBoundary; i++) {
            result.add(methodInstructions.get(i));
        }
        result.trimToSize();
        return result;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public boolean covers(final int someInstructionIndex) {
        return rangeOfBlockInstructions.covers(someInstructionIndex);
    }

    /*
     * For test purposes only.
     */
    boolean isDirectPredecessorOf(final ControlFlowBlock successor) {
        return successors.contains(successor);
    }

    /*
     * For test purposes only.
     */
    boolean isPredecessorOf(final ControlFlowBlock possibleSuccessor) {
        boolean result = false;
        for (final ControlFlowBlock directSuccessor : successors) {
            if (directSuccessor.equals(possibleSuccessor) || directSuccessor.isPredecessorOf(possibleSuccessor)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /*
     * For test purposes only.
     */
    boolean isSuccessorOf(final ControlFlowBlock possiblePredecessor) {
        return possiblePredecessor.isPredecessorOf(this);
    }

    public AbstractInsnNode getBlockInstructionForIndex(final int index) {
        final int resultIndex = rangeOfBlockInstructions.lowerBoundary + index;
        AbstractInsnNode result;
        try {
            result = methodInstructions.get(resultIndex);
        } catch (final IndexOutOfBoundsException e) {
            result = null;
        }
        return result;
    }

    public int getIndexWithinMethod(final int indexWithinBlock) {
        final int result = indexWithinBlock + rangeOfBlockInstructions.lowerBoundary;
        if (rangeOfBlockInstructions.upperBoundary < result) {
            final String msgTemplate = "Index would be %d which is bigger than the maximum index of method (%d).";
            final String msg = String.format(msgTemplate, result, rangeOfBlockInstructions.upperBoundary);
            throw new IndexOutOfBoundsException(msg);
        }
        return result;
    }

    public int getIndexWithinBlock(final int indexWithinMethod) {
        final int result = indexWithinMethod - rangeOfBlockInstructions.lowerBoundary;
        return result;
    }

    public Set<ControlFlowBlock> getPredecessors() {
        return Collections.unmodifiableSet(predecessors);
    }

    public Set<ControlFlowBlock> getSuccessors() {
        return Collections.unmodifiableSet(successors);
    }

    @Override
    public int compareTo(final ControlFlowBlock o) {
        final Integer thisBlockNumber = Integer.valueOf(blockNumber);
        final Integer otherBlockNumber = Integer.valueOf(o.blockNumber);
        return thisBlockNumber.compareTo(otherBlockNumber);
    }

    @Override
    public synchronized int hashCode() {
        if (0 == hashCode) {
            final int prime = 31;
            int result = 1;
            result = prime * result + blockNumber;
            result = prime * result + identifier.hashCode();
            result = prime * result + methodInstructions.hashCode();
            result = prime * result + rangeOfBlockInstructions.hashCode();
            result = prime * result + predecessors.hashCode();
            result = prime * result + successors.hashCode();
            hashCode = result;
        }
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ControlFlowBlock)) {
            return false;
        }
        final ControlFlowBlock other = (ControlFlowBlock) obj;
        if (blockNumber != other.blockNumber) {
            return false;
        }
        if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (!methodInstructions.equals(other.methodInstructions)) {
            return false;
        }
        if (!rangeOfBlockInstructions.equals(other.rangeOfBlockInstructions)) {
            return false;
        }
        if (!predecessors.equals(other.predecessors)) {
            return false;
        }
        if (!successors.equals(other.successors)) {
            return false;
        }
        return true;
    }

    @Override
    public synchronized String toString() {
        if (null == stringRepresentation) {
            final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
            builder.append("[blockNumber=").append(blockNumber);
            builder.append(", identifier=").append(identifier);
            builder.append(", rangeOfBlockInstructionIndices=").append(rangeOfBlockInstructions);
            builder.append(", predecessors=").append(setToString(predecessors));
            builder.append(", successors=").append(setToString(successors));
            builder.append("]");
            stringRepresentation = builder.toString();
        }
        return stringRepresentation;
    }

    private static String setToString(final Set<ControlFlowBlock> cfbSet) {
        final StringBuilder result = new StringBuilder();
        result.append("{");
        final String separator = ", ";
        String sep = "";
        for (final ControlFlowBlock controlFlowBlock : cfbSet) {
            result.append(sep).append(controlFlowBlock.getBlockNumber());
            sep = separator;
        }
        result.append("}");
        return result.toString();
    }

}
