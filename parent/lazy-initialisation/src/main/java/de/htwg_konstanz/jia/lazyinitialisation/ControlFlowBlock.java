/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
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
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.02.2013
 */
@ThreadSafe
final class ControlFlowBlock implements Comparable<ControlFlowBlock> {

    @NotThreadSafe
    public static final class Builder {
        private final int blockNumber;
        private final String identifier;
        private final InsnList allInstructions;
        private final SortedSet<Integer> rangeItems;

        public Builder(final int theBlockNumber, final String theIdentifier, final InsnList allInstructions) {
            blockNumber = theBlockNumber;
            identifier = notEmpty(theIdentifier);
            this.allInstructions = notNull(allInstructions);
            rangeItems = new TreeSet<Integer>();
        }

        public void addInstruction(final int instructionIndex) {
            rangeItems.add(Integer.valueOf(instructionIndex));
        }

        public boolean isBuilderForBlockNumber(final int aBlockNumber) {
            return blockNumber == aBlockNumber;
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
            final ControlFlowBlockFactory result = new ControlFlowBlockFactory(notEmpty(owner), notNull(method));
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

        public Map<Integer, ControlFlowBlock> getAllControlFlowBlocksForMethodInMap() {
            final List<ControlFlowBlock> allControlFlowBlocks = getAllControlFlowBlocksForMethod();
            final Map<Integer, ControlFlowBlock> result = new HashMap<Integer, ControlFlowBlock>();
            for (final ControlFlowBlock controlFlowBlock : allControlFlowBlocks) {
                result.put(Integer.valueOf(controlFlowBlock.getBlockNumber()), controlFlowBlock);
            }
            return result;
        }
    } // class ControlFlowFactory


    private final int blockNumber;
    private final String identifier;
    private final InsnList methodInstructions;
    private final Range rangeOfBlockInstructions;
    private final Set<ControlFlowBlock> predecessors;
    private final Set<ControlFlowBlock> successors;
    @GuardedBy("this") private final Map<String, JumpInsn> assignmentGuards;
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
        assignmentGuards = new WeakHashMap<String, JumpInsn>();
        hashCode = 0;
        stringRepresentation = null;
    }

    public static ControlFlowBlock newInstance(final int blockNumber,
            final String identifier,
            final InsnList methodInstructions,
            final Range rangeOfInstructionIndices) {
        return new ControlFlowBlock(blockNumber, notEmpty(identifier), notNull(methodInstructions),
                notNull(rangeOfInstructionIndices));
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

//    public boolean containsConditionCheck() {
//        boolean result = false;
//        for (int i = rangeOfBlockInstructions.lowerBoundary; i <= rangeOfBlockInstructions.upperBoundary; i++) {
//            if (isJumpInsnNode(methodInstructions[i])) {
//                result = true;
//                break;
//            }
//        }
//        return result;
//    }

//    private static boolean isJumpInsnNode(final AbstractInsnNode insn) {
//        return AbstractInsnNode.JUMP_INSN == insn.getType();
//    }

//    public List<JumpInsn> getJumpInstructions() {
//        final ArrayList<JumpInsn> result = new ArrayList<JumpInsn>();
//        int indexWithinBlock = 0;
//        for (int i = rangeOfBlockInstructions.lowerBoundary; i <= rangeOfBlockInstructions.upperBoundary; i++) {
//            final AbstractInsnNode abstractInsnNode = methodInstructions[i];
//            if (isJumpInsnNode(abstractInsnNode)) {
//                final JumpInsnNode jumpInsnNode = (JumpInsnNode) abstractInsnNode;
//                result.add(JumpInsnDefault.newInstance(jumpInsnNode, indexWithinBlock, i));
//            }
//            indexWithinBlock++;
//        }
//        result.trimToSize();
//        return result;
//    }

    public synchronized boolean containsAssignmentGuardForVariable(final String variableName) {
        final boolean result;
        if (variableNameIsNotSuitable(variableName)) {
            result = false;
        } else if (assignmentGuards.containsKey(variableName)) {
            final JumpInsn supposedAssignmentGuard = assignmentGuards.get(variableName);
            result = supposedAssignmentGuard.isAssignmentGuard();
        } else {
            findAssignmentGuardForVariableInThisBlock(variableName);
            result = containsAssignmentGuardForVariable(variableName);
        }
        return result;
    }

    private static boolean variableNameIsNotSuitable(final String variableName) {
        return null == variableName || variableName.isEmpty();
    }

    private void findAssignmentGuardForVariableInThisBlock(final String variableName) {
        final Finder<JumpInsn> f = AssignmentGuardFinder.newInstance(variableName, this);
        final JumpInsn supposedAssignmentGuard = f.find();
        addToAssignmentGuardsIfNecessary(variableName, supposedAssignmentGuard);
    }

    private void addToAssignmentGuardsIfNecessary(final String variableName, final JumpInsn assignmentGuard) {
        if (!assignmentGuards.containsKey(variableName)) {
            assignmentGuards.put(variableName, assignmentGuard);
        }
    }

    public JumpInsn getAssignmentGuardForVariable(final String variableName) {
        final JumpInsn result;
        if (containsAssignmentGuardForVariable(variableName)) {
            synchronized (this) {
                result = assignmentGuards.get(variableName);
            }
        } else {
            result = NullJumpInsn.getInstance();
        }
        return result;
    }

    public boolean covers(final int someInstructionIndex) {
        return rangeOfBlockInstructions.covers(someInstructionIndex);
    }

    public boolean coversAll(final Collection<Integer> indices) {
        return rangeOfBlockInstructions.coversAll(indices);
    }

    public boolean coversOneOf(final Collection<Integer> indices) {
        return rangeOfBlockInstructions.coversOneOf(indices);
    }

    public boolean isDirectPredecessorOf(final ControlFlowBlock successor) {
        return successors.contains(successor);
    }

    public boolean isPredecessorOf(final ControlFlowBlock possibleSuccessor) {
        boolean result = false;
        for (final ControlFlowBlock directSuccessor : successors) {
            if (directSuccessor.equals(possibleSuccessor) || directSuccessor.isPredecessorOf(possibleSuccessor)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean isDirectSuccessorOf(final ControlFlowBlock possiblePredecessor) {
        return predecessors.contains(possiblePredecessor);
    }

    public boolean isSuccessorOf(final ControlFlowBlock possiblePredecessor) {
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
        return indexWithinBlock + rangeOfBlockInstructions.lowerBoundary;
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
