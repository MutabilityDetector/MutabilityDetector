/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.fail;

import java.util.*;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
public final class AssignmentGuardFinder {

    private static final class Helper {
        private final Class<?> klasse;
        private final String variableName;
        private final String methodName;
        private final ConvenienceClassNode convenienceClassNode;
        private final List<ControlFlowBlock> controlFlowBlocks;

        private Helper(final Class<?> theKlasse, final String theVariableName, final String theMethodName) {
            super();
            klasse = theKlasse;
            variableName = theVariableName;
            methodName = theMethodName;
            convenienceClassNode = createConvenienceClassNodeFor(klasse);
            controlFlowBlocks = getControlFlowBlocksFor(klasse, methodName);
        }

        private static List<ControlFlowBlock> getControlFlowBlocksFor(final Class<?> klasse, final String methodName) {
            List<ControlFlowBlock> result = Collections.emptyList();
            final ConvenienceClassNode ccn = createConvenienceClassNodeFor(klasse);
            final MethodNode methodNode = ccn.findMethodWithName(methodName);
            if (isNotNull(methodNode)) {
                result = createControlFlowBlocksFor(ccn.name(), methodNode);
            }
            return result;
        }
        
        private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.convenienceClassNodeFor(klasse);
        }
        
        private static boolean isNotNull(final Object ref) {
            return null != ref;
        }

        private static List<ControlFlowBlock> createControlFlowBlocksFor(final String owner, final MethodNode setter) {
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(owner, setter);
            return cfbFactory.getAllControlFlowBlocksForMethod();
        }

        public List<ControlFlowBlock> getAllControlFlowBlocks() {
            return controlFlowBlocks;
        }

        public ControlFlowBlock getBlockWithNumber(final int blockNumber) {
            ControlFlowBlock result = null;
            for (final ControlFlowBlock b : controlFlowBlocks) {
                if (b.getBlockNumber() == blockNumber) {
                    result = b;
                    break;
                }
            }
            if (null == result) {
                fail(String.format("No control flow block found for number %d.", blockNumber));
            }
            return result;
        }

        public FieldNode getVariableForName(final String variableName) {
            return convenienceClassNode.findVariableWithName(variableName);
        }
    } // class Helper


    private final Map<Integer, JumpInsnNode> relevantJumpInsns = new HashMap<Integer, JumpInsnNode>();
    private Helper h = null;

    @After
    public void tearDown() {
        relevantJumpInsns.clear();
        h = null;
    }

    @Test
    public void findAssignmentGuardForIntegerWithDefault() {
        h = new Helper(IntegerWithDefault.class, "hash", "hashCode");
        final ControlFlowBlock blockWithJumpInsn = h.getBlockWithNumber(0);
        final Map<Integer, JumpInsnNode> jumpInsnNodes = getAllJumpInsructions(blockWithJumpInsn.getInstructions());
        analyseJumpInstructions(jumpInsnNodes, blockWithJumpInsn.getInstructions(), "hash");
    }

    private static Map<Integer, JumpInsnNode> getAllJumpInsructions(final List<AbstractInsnNode> instructions) {
        final Map<Integer, JumpInsnNode> result = new HashMap<Integer, JumpInsnNode>();
        for (final AbstractInsnNode abstractInsnNode : instructions) {
            if (isJumpInsnNode(abstractInsnNode)) {
                result.put(instructions.indexOf(abstractInsnNode), (JumpInsnNode) abstractInsnNode);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private static boolean isJumpInsnNode(final AbstractInsnNode abstractInsnNode) {
        return AbstractInsnNode.JUMP_INSN == abstractInsnNode.getType();
    }

    private void analyseJumpInstructions(final Map<Integer, JumpInsnNode> jumpInsnNodes,
            final List<AbstractInsnNode> blockInstructions, final String variableName) {
        for (final Entry<Integer, JumpInsnNode> jumpInsnWithIndex : jumpInsnNodes.entrySet()) {
            analyseParticularJumpInstruction(jumpInsnWithIndex, blockInstructions, variableName);
        }
    }

    private void analyseParticularJumpInstruction(final Entry<Integer, JumpInsnNode> jumpInsnWithIndex,
            final List<AbstractInsnNode> blockInstructions, final String variableName) {
        // TODO Auto-generated method stub
        final int previousIndex = jumpInsnWithIndex.getKey() - 1;
        if (isPreviousGetfield(blockInstructions.get(previousIndex))) {
            relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
        } else {
            /*
             * In allen Vorgaengerbloecken:
             * GETFIELD fuer `variableName` suchen.
             * Lokale Variable fuer den Wert von `variableName` suchen (`?STORE x`).
             * 
             * In Block mit Sprunganweisung:
             * Typ der Vorgaenger-Anweisung ermitteln:
             *     `?LOAD` x:
             *         Stimmt x überein ? Bedinungspruefung entscheidet evtl. ueber Zuweisung : aktueller Block scheidet aus
             *     `?`:
             */
        }
    }

    private static boolean isPreviousGetfield(final AbstractInsnNode previousInsn) {
        return Opcodes.PUTFIELD == previousInsn.getOpcode();
    }

}
