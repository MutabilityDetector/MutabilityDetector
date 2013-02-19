/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

import de.htwg_konstanz.jia.lazyinitialisation.Range.RangeBuilder;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.02.2013
 */
final class ControlFlowBlock {

    @NotThreadSafe
    public static final class Builder {
        private final int blockNumber;
        private final String identifier;
        private final RangeBuilder rangeBuilder;
        private final List<AbstractInsnNode> instructionsOfCurrentBlock;

        public Builder(final int theBlockNumber, final String theIdentifier) {
            blockNumber = theBlockNumber;
            identifier = notEmpty(theIdentifier);
            rangeBuilder = new RangeBuilder();
            instructionsOfCurrentBlock = new ArrayList<AbstractInsnNode>();
        }

        public void addInstruction(final int instructionIndex, final AbstractInsnNode actualInstruction) {
            rangeBuilder.add(instructionIndex);
            instructionsOfCurrentBlock.add(actualInstruction);
        }

        public ControlFlowBlock build() {
            final AbstractInsnNode[] insns = instructionsOfCurrentBlock
                    .toArray(new AbstractInsnNode[instructionsOfCurrentBlock.size()]);
            return ControlFlowBlock.newInstance(blockNumber, identifier, insns, rangeBuilder.build());
        }
    } // class Builder


    public static final class ControlFlowBlockFactory {
        private final String owner;
        private final MethodNode setter;
        private final AbstractInsnNode[] allInstructions;
        private final List<ControlFlowBlock> controlFlowBlocks;
        private final AtomicInteger currentBlockNumber;

        private final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected void newControlFlowEdge(int src, int dest) {
                interlinkControlFlowBlocks(src, dest);
            }

            private void interlinkControlFlowBlocks(final int src, final int dest) {
                ControlFlowBlock srcBlock = null;
                ControlFlowBlock destBlock = null;
                for (final ControlFlowBlock cfb : controlFlowBlocks) {
                    if (cfb.covers(src)) {
                        srcBlock = cfb;
                    } else if (cfb.covers(dest)) {
                        destBlock = cfb;
                    }
                }
                if (null != srcBlock && null != destBlock) {
                    srcBlock.addSuccessor(destBlock);
                    destBlock.addPredecessor(srcBlock);
                }
            }
        };
        
        private ControlFlowBlockFactory(final String theOwner, final MethodNode theSetter) {
            setter = theSetter;
            owner = theOwner;
            final AbstractInsnNode[] allInstructionsOriginal = theSetter.instructions.toArray();
            this.allInstructions = Arrays.copyOf(allInstructionsOriginal, allInstructionsOriginal.length);
            controlFlowBlocks = new ArrayList<ControlFlowBlock>();
            currentBlockNumber = new AtomicInteger(0);
        }

        public static ControlFlowBlockFactory newInstance(final String owner, final MethodNode setter) {
            final ControlFlowBlockFactory result = new ControlFlowBlockFactory(notEmpty(owner), notNull(setter));
            result.createAllControlFlowBlocks();
            result.analyseMethod();
            return result;
        }

        private void createAllControlFlowBlocks() {
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
            for (final ControlFlowBlock controlFlowBlock : controlFlowBlocks) {
                if (controlFlowBlock.isNotEmpty()) {
                    result.add(controlFlowBlock);
                }
            }
            result.trimToSize();
            return result;
        }
    } // class ControlFlowFactory


    private final int blockNumber;
    private final String identifier;
    private final AbstractInsnNode[] instructions;
    private final Range rangeOfInstructionIndices;
    private final Set<ControlFlowBlock> predecessors;
    private final Set<ControlFlowBlock> successors;

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

    public boolean containsEffectiveConditionCheck(final AssignmentInsn effectiveAssignmentInsn) {
        final EffectiveJumpInsnFinder f = EffectiveJumpInsnFinder.newInstance(effectiveAssignmentInsn, instructions);
        final JumpInsn effectiveJumpInsn = f.getEffectiveJumpInsn();
        return !effectiveJumpInsn.isNull();
    }

    public boolean containsEffectiveAssignmentInstruction(final FieldNode assignedVariable) {
        final EffectivePutfieldInsnFinder f = EffectivePutfieldInsnFinder.newInstance(assignedVariable, instructions);
        final AssignmentInsn effectivePutfieldInstruction = f.getEffectivePutfieldInstruction();
        return !effectivePutfieldInstruction.isNull();
    }

    public boolean covers(final int someInstructionIndex) {
        return rangeOfInstructionIndices.covers(someInstructionIndex);
    }

    public void addPredecessor(final ControlFlowBlock predecessor) {
        predecessors.add(notNull(predecessor));
    }

    public void addSuccessor(final ControlFlowBlock successor) {
        successors.add(notNull(successor));
    }

    public boolean isPredecessorOf(final ControlFlowBlock successor) {
        return successors.contains(successor);
    }

    public boolean isSuccessorOf(final ControlFlowBlock predecessor) {
        return predecessors.contains(predecessor);
    }

    public List<AbstractInsnNode> getInstructions() {
        return Arrays.asList(instructions);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[blockNumber=").append(blockNumber);
        builder.append(", identifier=").append(identifier);
        builder.append(", rangeOfInstructionIndices=").append(rangeOfInstructionIndices);
        builder.append(", predecessors=").append(setToString(predecessors));
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
