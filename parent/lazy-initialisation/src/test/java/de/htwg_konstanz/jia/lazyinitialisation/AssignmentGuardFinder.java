/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.objectweb.asm.Opcodes.*;
import static org.junit.Assert.fail;

import java.util.*;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedFloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 20.02.2013
 */
public final class AssignmentGuardFinder {

    private static final class Pair<L, R> {
        public final L left;
        public final R right;

        private Pair(final L theLeft, final R theRight) {
            left = theLeft;
            right = theRight;
        }

        public static <L, R> Pair<L, R> newPair(final L left, final R right) {
            return new Pair<L, R>(left, right);
        }
    } // class Pair<L, R>


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
    private String variableName;
    private Helper h = null;

    @After
    public void tearDown() {
        relevantJumpInsns.clear();
        variableName = null;
        h = null;
    }

    @Test
    public void findAssignmentGuardForIntegerWithDefault() {
        variableName = "hash";
        h = new Helper(IntegerWithDefault.class, variableName, "hashCode");
        final ControlFlowBlock blockWithJumpInsn = h.getBlockWithNumber(0);
        final Map<Integer, JumpInsnNode> jumpInsnNodes = getAllJumpInsructions(blockWithJumpInsn.getInstructions());
        analyseJumpInstructions(jumpInsnNodes, blockWithJumpInsn);
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
            final ControlFlowBlock blockWithJumpInsn) {
        for (final Entry<Integer, JumpInsnNode> jumpInsnWithIndex : jumpInsnNodes.entrySet()) {
            analyseParticularJumpInstruction(jumpInsnWithIndex, blockWithJumpInsn);
        }
    }

    private void analyseParticularJumpInstruction(final Entry<Integer, JumpInsnNode> jumpInsnWithIndex,
            final ControlFlowBlock blockWithJumpInsn) {
        // TODO Auto-generated method stub
        final int previousIndex = jumpInsnWithIndex.getKey() - 1;
        final List<AbstractInsnNode> instructions = blockWithJumpInsn.getInstructions();
        if (isPreviousInsnGetfieldForVariable(instructions.get(previousIndex))) {
            relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
        } else {
            /* Im aktuellen und in allen Vorgaengerbloecken:
             *     GETFIELD fuer `variableName` suchen.
             *     Lokale Variable fuer den Wert von `variableName` suchen (`?STORE x`).
             *     *Implementiert*
             * 
             * 
             * In Block mit Sprunganweisung:
             * Typ der Vorgaenger-Anweisung ermitteln:
             *     `?LOAD` x
             *         Alias suchen
             *         ? Stimmt x überein
             *             ? Bedingungspruefung entscheidet evtl. ueber Zuweisung
             *             : aktueller Block scheidet aus
             *         : `GETFIELD` fuer `variableName`
             *             ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
             *             : mit naechster Sprunganweisung fortfahren.
             *         : Ist Anweisung n - 1 eine Vergleichsanweisung (z. B. `FCMPL`)
             *             ? Ist n - 2 `GETFIELD` fuer `variableName`
             *                 ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
             *                 : mit naechster Sprunganweisung fortfahren.
             *             : Ist n - 2 `?LOAD` fuer Alias x
             *                 ? relevantJumpInsns.put(jumpInsnWithIndex.getKey(), jumpInsnWithIndex.getValue());
             *                 : mit naechster Sprunganweisung fortfahren.
             *
             */
            final Pair<Boolean, Integer> psblCompareInstruction = isPreviousInsnComparison(instructions.get(previousIndex));

//            final Pair<Boolean,Integer> aliasInBlock = searchForAliasInBlock(blockWithJumpInsn);
//            if (aliasInBlock.left) {
//                System.out.println(String.format("Alias ist %d", aliasInBlock.right));
//            }
        }
    }

    private boolean isPreviousInsnGetfieldForVariable(final AbstractInsnNode previousInsn) {
        boolean result = false;
        if (Opcodes.GETFIELD == previousInsn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) previousInsn;
            result = variableName.equals(getfield.name);
        }
        return result;
    }
    
    private Pair<Boolean, Integer> isPreviousInsnComparison(final AbstractInsnNode abstractInsnNode) {
        Pair<Boolean, Integer> result = Pair.newPair(Boolean.FALSE, Integer.MIN_VALUE);
        if (isCompareInstruction(abstractInsnNode)) {
            
        }
        return result;
    }

    private boolean isCompareInstruction(final AbstractInsnNode abstractInsnNode) {
        final Set<Integer> compareInstructions = getCompareInstructions();
        final Integer opcode = Integer.valueOf(abstractInsnNode.getOpcode());
        return compareInstructions.contains(opcode);
    }

    private static Set<Integer> getCompareInstructions() {
        final class SetBuilder {
            final Set<Integer> compareInstructions = new HashSet<Integer>();
            SetBuilder add(final int opcode) {
                compareInstructions.add(Integer.valueOf(opcode));
                return this;
            }
        }
        final SetBuilder b = new SetBuilder();
        b.add(LCMP).add(FCMPL).add(FCMPG).add(DCMPL).add(DCMPG);
        b.add(IF_ICMPEQ).add(IF_ICMPNE).add(IF_ICMPLT).add(IF_ICMPGE).add(IF_ICMPGT).add(IF_ICMPLE);
        b.add(IF_ACMPEQ).add(IF_ACMPNE);
        return b.compareInstructions;
    }

    private Pair<Boolean, Integer> searchForAliasInBlock(final ControlFlowBlock block) {
        Pair<Boolean, Integer> result = Pair.newPair(Boolean.FALSE, Integer.MIN_VALUE);
        final AbstractInsnNode[] insns = toArray(block.getInstructions());
        int indexOfGetfield = -1;
        for (int i = 0; i < insns.length; i++) {
            if (isGetfieldForVariable(insns[i])) {
                indexOfGetfield = i;
                break;
            }
        }
        if (-1 < indexOfGetfield) {
            if (isStoreInstruction(insns[indexOfGetfield + 1])) {
                final VarInsnNode storeInsn = (VarInsnNode) insns[indexOfGetfield + 1];
                result = Pair.newPair(Boolean.TRUE, storeInsn.var);
            }
        }
        if (!result.left) {
            for (final ControlFlowBlock predecessor : block.getPredecessors()) {
                return searchForAliasInBlock(predecessor);
            }
        }
        return result;
    }

    private static AbstractInsnNode[] toArray(final List<AbstractInsnNode> asList) {
        final List<AbstractInsnNode> instructions = asList;
        return instructions.toArray(new AbstractInsnNode[asList.size()]);
    }

    private boolean isGetfieldForVariable(final AbstractInsnNode insn) {
        boolean result = false;
        if (Opcodes.GETFIELD == insn.getOpcode()) {
            final FieldInsnNode getfield = (FieldInsnNode) insn;
            result = variableName.equals(getfield.name);
        }
        return result;
    }

    private boolean isStoreInstruction(final AbstractInsnNode insn) {
        final Set<Integer> storeInstructions = getStoreInstructions();
        final Integer opcode = Integer.valueOf(insn.getOpcode());
        return storeInstructions.contains(opcode);
    }

    private static Set<Integer> getStoreInstructions() {
        final class SetBuilder {
            final Set<Integer> storeInstructions = new HashSet<Integer>();
            SetBuilder add(final int opcode) {
                storeInstructions.add(Integer.valueOf(opcode));
                return this;
            }
        }
        final SetBuilder b = new SetBuilder();
        b.add(ISTORE).add(LSTORE).add(FSTORE).add(DSTORE).add(ASTORE);
        return b.storeInstructions;
    }

    @Test
    public void findAssignmentGuardForAliasedFloatWithDefault() {
        variableName = "hash";
        h = new Helper(AliasedFloatWithDefault.class, variableName, "hashCodeFloat");
        final ControlFlowBlock blockWithJumpInsn = h.getBlockWithNumber(1);
        final Map<Integer, JumpInsnNode> jumpInsnNodes = getAllJumpInsructions(blockWithJumpInsn.getInstructions());
        analyseJumpInstructions(jumpInsnNodes, blockWithJumpInsn);
    }

}
