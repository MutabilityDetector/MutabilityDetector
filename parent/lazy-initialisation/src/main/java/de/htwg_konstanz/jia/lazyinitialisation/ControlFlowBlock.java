/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
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
        private final String identifier;
        private final RangeBuilder rangeBuilder;
        private final List<AbstractInsnNode> instructionsOfCurrentBlock;

        public Builder(final String theIdentifier) {
            identifier = notEmpty(theIdentifier);
            rangeBuilder = new RangeBuilder();
            instructionsOfCurrentBlock = new ArrayList<AbstractInsnNode>();
        }

        public void addInstruction(final int instructionIndex, final AbstractInsnNode actualInstruction) {
            rangeBuilder.add(instructionIndex);
            instructionsOfCurrentBlock.add(actualInstruction);
        }

        public ControlFlowBlock build() {
            return new ControlFlowBlock(identifier, instructionsOfCurrentBlock, rangeBuilder.build());
        }
    } // class Builder


    public static final class ControlFlowBlockFactory {
        private final String owner;
        private final MethodNode setter;
        private final AbstractInsnNode[] allInstructions;
        private final Set<ControlFlowBlock> controlFlowBlocks;

        private final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected boolean newControlFlowExceptionEdge(int src, int dest) {
                interlinkControlFlowBlocks(src, dest);
                return super.newControlFlowExceptionEdge(src, dest);
            }

            private void interlinkControlFlowBlocks(final int src, final int dest) {
                ControlFlowBlock srcBlock = null;
                ControlFlowBlock destBlock = null;
                for (final ControlFlowBlock cfb : controlFlowBlocks) {
                    if (srcBlock.covers(src)) {
                        srcBlock = cfb;
                    } else if (destBlock.covers(dest)) {
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
            controlFlowBlocks = new HashSet<ControlFlowBlock>();
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
            final AbstractInsnNode firstInsn = allInstructions[0];
            if (isLabel(firstInsn)) {
                result = createNewControlFlowBlockBuilderForLabel(firstInsn);
            } else {
                result = new Builder("L<Pseudo>");
                result.addInstruction(0, firstInsn);
            }
            return result;
        }

        private static boolean isLabel(final AbstractInsnNode insn) {
            return AbstractInsnNode.LABEL == insn.getType();
        }

        private static Builder createNewControlFlowBlockBuilderForLabel(final AbstractInsnNode insn) {
            final LabelNode labelNode = (LabelNode) insn;
            final Label label = labelNode.getLabel();
            return new Builder(label.toString());
        }

        private void handleRemainingInstructions(final Builder controlFlowBlockBuilder) {
            Builder builder = controlFlowBlockBuilder;
            for (int i = 1; i < allInstructions.length; i++) {
                final AbstractInsnNode insn = allInstructions[i];
                if (isLabel(insn)) {
                    controlFlowBlocks.add(builder.build());
                    builder = createNewControlFlowBlockBuilderForLabel(insn);
                } else {
                    builder.addInstruction(i, insn);
                }
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

        public Set<ControlFlowBlock> getAllControlFlowBlocksForMethod() {
            return Collections.unmodifiableSet(controlFlowBlocks);
        }
    } // class ControlFlowFactory


    private final String identifier;
    private final List<AbstractInsnNode> instructions;
    private final Range rangeOfInstructionIndices;
    private final Set<ControlFlowBlock> predecessors;
    private final Set<ControlFlowBlock> successors;

    private ControlFlowBlock(final String theIdentifier, final List<AbstractInsnNode> theInstructions,
            final Range theRangeOfInstructionIndices) {
        identifier = theIdentifier;
        instructions = Collections.unmodifiableList(theInstructions);
        rangeOfInstructionIndices = theRangeOfInstructionIndices;
        predecessors = new HashSet<ControlFlowBlock>();
        successors = new HashSet<ControlFlowBlock>();
    }

    public void addInstruction(final AbstractInsnNode instruction) {
        instructions.add(instruction);
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

    @Override
    public String toString() {
        final ToStringBuilder builder2 = new ToStringBuilder(this);
        builder2.append("identifier", identifier).append("instructions", instructions)
                .append("rangeOfInstructionIndices", rangeOfInstructionIndices).append("predecessors", predecessors)
                .append("successors", successors);
        return builder2.toString();
    }

}
