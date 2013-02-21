/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
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
@Immutable
final class ControlFlowBlock implements Comparable<ControlFlowBlock> {

    @NotThreadSafe
    public static final class Builder {
        private final int blockNumber;
        private final String identifier;
        private final SortedSet<Integer> rangeItems;
        private final List<AbstractInsnNode> instructionsOfCurrentBlock;

        public Builder(final int theBlockNumber, final String theIdentifier) {
            blockNumber = theBlockNumber;
            identifier = notEmpty(theIdentifier);
            rangeItems = new TreeSet<Integer>();
            instructionsOfCurrentBlock = new ArrayList<AbstractInsnNode>();
        }

        public void addInstruction(final int instructionIndex, final AbstractInsnNode actualInstruction) {
            rangeItems.add(Integer.valueOf(instructionIndex));
            instructionsOfCurrentBlock.add(actualInstruction);
        }

        public boolean isBuilderForBlockNumber(final int aBlockNumber) {
            return blockNumber == aBlockNumber;
        }

        public ControlFlowBlock build() {
            final AbstractInsnNode[] instructions = instructionsToArray();
            final Range range = Range.newInstance(rangeItems);
            return ControlFlowBlock.newInstance(blockNumber, identifier, instructions, range);
        }

        private AbstractInsnNode[] instructionsToArray() {
            final int numberOfInstructions = instructionsOfCurrentBlock.size();
            return instructionsOfCurrentBlock.toArray(new AbstractInsnNode[numberOfInstructions]);
        }
    } // class Builder


    @ThreadSafe
    public static final class ControlFlowBlockFactory {
        private final String owner;
        private final MethodNode setter;
        private final AbstractInsnNode[] allInstructions;
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

        private ControlFlowBlockFactory(final String theOwner, final MethodNode theSetter) {
            setter = theSetter;
            owner = theOwner;
            final AbstractInsnNode[] allInstructionsOriginal = theSetter.instructions.toArray();
            allInstructions = Arrays.copyOf(allInstructionsOriginal, allInstructionsOriginal.length);
            controlFlowBlocks = new ArrayList<ControlFlowBlock>();
            currentBlockNumber = new AtomicInteger(0);
        }

        public static ControlFlowBlockFactory newInstance(final String owner, final MethodNode setter) {
            final ControlFlowBlockFactory result = new ControlFlowBlockFactory(notEmpty(owner), notNull(setter));
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
            final AbstractInsnNode firstInsn = allInstructions[currentBlockNumber.get()];
            if (isLabel(firstInsn)) {
                result = createNewControlFlowBlockBuilderForLabel(firstInsn);
            } else {
                result = new Builder(currentBlockNumber.getAndIncrement(), "L<Pseudo>");
            }
            result.addInstruction(0, firstInsn);
            return result;
        }

        private static boolean isLabel(final AbstractInsnNode insn) {
            return AbstractInsnNode.LABEL == insn.getType();
        }

        private Builder createNewControlFlowBlockBuilderForLabel(final AbstractInsnNode insn) {
            final LabelNode labelNode = (LabelNode) insn;
            final Label label = labelNode.getLabel();
            return new Builder(currentBlockNumber.getAndIncrement(), label.toString());
        }

        private void handleRemainingInstructions(final Builder controlFlowBlockBuilder) {
            Builder builder = controlFlowBlockBuilder;
            for (int i = 1; i < allInstructions.length; i++) {
                final AbstractInsnNode insn = allInstructions[i];
                if (isLabel(insn)) {
                    controlFlowBlocks.add(builder.build());
                    builder = createNewControlFlowBlockBuilderForLabel(insn);
                }
                builder.addInstruction(i, insn);
            }
            controlFlowBlocks.add(builder.build());
        }

        private void analyseMethod() {
            tryToAnalyseMethod();
        }

        private void tryToAnalyseMethod() {
            try {
                analyser.analyze(owner, setter);
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
    private final AbstractInsnNode[] instructions;
    private final Range rangeOfInstructionIndices;
    private final Set<ControlFlowBlock> predecessors;
    private final Set<ControlFlowBlock> successors;
    private int hashCode;
    private String stringRepresentation;

    private ControlFlowBlock(final int theBlockNumber,
            final String theIdentifier,
            final AbstractInsnNode[] theInstructions,
            final Range theRangeOfInstructionIndices) {
        blockNumber = theBlockNumber;
        identifier = theIdentifier;
        instructions = Arrays.copyOf(theInstructions, theInstructions.length);
        rangeOfInstructionIndices = theRangeOfInstructionIndices;
        predecessors = new HashSet<ControlFlowBlock>();
        successors = new HashSet<ControlFlowBlock>();
        hashCode = 0;
        stringRepresentation = null;
    }

    public static ControlFlowBlock newInstance(final int blockNumber,
            final String identifier,
            final AbstractInsnNode[] instructions,
            final Range rangeOfInstructionIndices) {
        return new ControlFlowBlock(blockNumber, notEmpty(identifier), notNull(instructions),
                notNull(rangeOfInstructionIndices));
    }

    public boolean isEmpty() {
        boolean result = false;
        if (1 == instructions.length) {
            result = isLabelNode(instructions[0]);
        }
        return result;
    }

    private static boolean isLabelNode(final AbstractInsnNode insn) {
        return AbstractInsnNode.LABEL == insn.getType();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public boolean containsConditionCheck() {
        boolean result = false;
        for (final AbstractInsnNode insn : instructions) {
            if (isJumpInsnNode(insn)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean isJumpInsnNode(final AbstractInsnNode insn) {
        return AbstractInsnNode.JUMP_INSN == insn.getType();
    }

    public boolean containsEffectiveAssignmentInstruction(final FieldNode assignedVariable) {
        final EffectivePutfieldInsnFinder f = EffectivePutfieldInsnFinder.newInstance(assignedVariable, instructions);
        final AssignmentInsn effectivePutfieldInstruction = f.getEffectivePutfieldInstruction();
        return !effectivePutfieldInstruction.isNull();
    }

    public boolean covers(final int someInstructionIndex) {
        return rangeOfInstructionIndices.covers(someInstructionIndex);
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

    public List<AbstractInsnNode> getInstructions() {
        return Arrays.asList(instructions);
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
            result = prime * result + Arrays.hashCode(instructions);
            result = prime * result + rangeOfInstructionIndices.hashCode();
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
        if (!Arrays.equals(instructions, other.instructions)) {
            return false;
        }
        if (!rangeOfInstructionIndices.equals(other.rangeOfInstructionIndices)) {
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
            builder.append(", rangeOfInstructionIndices=").append(rangeOfInstructionIndices);
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
