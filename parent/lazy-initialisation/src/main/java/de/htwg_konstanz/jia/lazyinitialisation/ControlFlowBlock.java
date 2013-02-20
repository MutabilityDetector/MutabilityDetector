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
        private final Set<Builder> successorBuilders;
        private ControlFlowBlock cfbInstance;

        public Builder(final int theBlockNumber, final String theIdentifier) {
            blockNumber = theBlockNumber;
            identifier = notEmpty(theIdentifier);
            rangeItems = new TreeSet<Integer>();
            instructionsOfCurrentBlock = new ArrayList<AbstractInsnNode>();
            successorBuilders = new HashSet<Builder>();
            cfbInstance = null;
        }

        public void addInstruction(final int instructionIndex, final AbstractInsnNode actualInstruction) {
            rangeItems.add(Integer.valueOf(instructionIndex));
            instructionsOfCurrentBlock.add(actualInstruction);
        }

        public boolean isBuilderForBlockNumber(final int aBlockNumber) {
            return blockNumber == aBlockNumber;
        }

        public boolean doesRangeCover(final int instructionIndex) {
            return rangeItems.contains(Integer.valueOf(instructionIndex));
        }

        public void addSuccessor(final Builder successor) {
            successorBuilders.add(notNull(successor));
        }

        public synchronized ControlFlowBlock build() {
            if (null == cfbInstance) {
                final Set<ControlFlowBlock> successors = buildSuccessors();
                final AbstractInsnNode[] instructions = instructionsToArray();
                final Range range = Range.newInstance(rangeItems);
                cfbInstance = ControlFlowBlock.newInstance(blockNumber, identifier, instructions, range, successors);
            }
            return cfbInstance;
        }

        private AbstractInsnNode[] instructionsToArray() {
            final int numberOfInstructions = instructionsOfCurrentBlock.size();
            return instructionsOfCurrentBlock.toArray(new AbstractInsnNode[numberOfInstructions]);
        }

        private Set<ControlFlowBlock> buildSuccessors() {
            final Set<ControlFlowBlock> result = new HashSet<ControlFlowBlock>();
            for (final Builder b : successorBuilders) {
                result.add(b.build());
            }
            return result;
        }
    } // class Builder


    public static final class ControlFlowBlockFactory {
        private final String owner;
        private final MethodNode setter;
        private final AbstractInsnNode[] allInstructions;
        private final List<ControlFlowBlock> controlFlowBlocks;
        private final List<Builder> controlFlowBlockBuilders;
        private final AtomicInteger currentBlockNumber;

        private final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected void newControlFlowEdge(int src, int dest) {
                interlinkControlFlowBlocks(src, dest);
            }

            private void interlinkControlFlowBlocks(final int src, final int dest) {
                Builder srcBlock = null;
                Builder destBlock = null;
                for (final Builder b : controlFlowBlockBuilders) {
                    if (b.doesRangeCover(src)) {
                        srcBlock = b;
                    } else if (b.doesRangeCover(dest)) {
                        destBlock = b;
                    }
                }
                if (null != srcBlock && null != destBlock) {
                    srcBlock.addSuccessor(destBlock);
                }
            }
        };
        
        private ControlFlowBlockFactory(final String theOwner, final MethodNode theSetter) {
            setter = theSetter;
            owner = theOwner;
            final AbstractInsnNode[] allInstructionsOriginal = theSetter.instructions.toArray();
            this.allInstructions = Arrays.copyOf(allInstructionsOriginal, allInstructionsOriginal.length);
            controlFlowBlocks = new ArrayList<ControlFlowBlock>();
            controlFlowBlockBuilders = new ArrayList<Builder>();
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

        private Builder handleFirstInstruction()  {
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
                    controlFlowBlockBuilders.add(builder);
                    builder = createNewControlFlowBlockBuilderForLabel(insn);
                }
                builder.addInstruction(i, insn);
            }
            controlFlowBlockBuilders.add(builder);
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
            createAllControlFlowBlocks();
            final ArrayList<ControlFlowBlock> result = new ArrayList<ControlFlowBlock>(controlFlowBlocks);
            result.trimToSize();
            return result;
        }

        private void createAllControlFlowBlocks() {
            for (final Builder b : controlFlowBlockBuilders) {
                final ControlFlowBlock cfb = b.build();
                if (cfb.isNotEmpty()) {
                    controlFlowBlocks.add(cfb);
                }
            }
        }
    } // class ControlFlowFactory


    private final int blockNumber;
    private final String identifier;
    private final AbstractInsnNode[] instructions;
    private final Range rangeOfInstructionIndices;
    private final Set<ControlFlowBlock> successors;

    private ControlFlowBlock(final int theBlockNumber,
            final String theIdentifier,
            final AbstractInsnNode[] theInstructions,
            final Range theRangeOfInstructionIndices,
            final Set<ControlFlowBlock> theSuccessors) {
        blockNumber = theBlockNumber;
        identifier = theIdentifier;
        instructions = Arrays.copyOf(theInstructions, theInstructions.length);
        rangeOfInstructionIndices = theRangeOfInstructionIndices;
        successors = Collections.unmodifiableSet(theSuccessors);
    }

    public static ControlFlowBlock newInstance(final int blockNumber,
            final String identifier,
            final AbstractInsnNode[] instructions,
            final Range rangeOfInstructionIndices,
            final Set<ControlFlowBlock> successors) {
        return new ControlFlowBlock(blockNumber, notEmpty(identifier), notNull(instructions),
                notNull(rangeOfInstructionIndices), notNull(successors));
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
        return possiblePredecessor.isDirectPredecessorOf(this);
    }

    public boolean isSuccessorOf(final ControlFlowBlock possiblePredecessor) {
        return possiblePredecessor.isPredecessorOf(this);
    }

    public List<AbstractInsnNode> getInstructions() {
        return Arrays.asList(instructions);
    }

    @Override
    public int compareTo(final ControlFlowBlock o) {
        final Integer thisBlockNumber = Integer.valueOf(blockNumber);
        final Integer otherBlockNumber = Integer.valueOf(o.blockNumber);
        return thisBlockNumber.compareTo(otherBlockNumber);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + blockNumber;
        result = prime * result + identifier.hashCode();
        result = prime * result + Arrays.hashCode(instructions);
        result = prime * result + rangeOfInstructionIndices.hashCode();
        result = prime * result + successors.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ControlFlowBlock)) {
            return false;
        }
        ControlFlowBlock other = (ControlFlowBlock) obj;
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
        if (!successors.equals(other.successors)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[blockNumber=").append(blockNumber);
        builder.append(", identifier=").append(identifier);
        builder.append(", rangeOfInstructionIndices=").append(rangeOfInstructionIndices);
        builder.append(", successors=").append(setToString(successors));
        builder.append("]");
        return builder.toString();
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
