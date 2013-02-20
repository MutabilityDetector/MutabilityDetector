package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.Range.RangeBuilder;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedFloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.FloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 19.02.2013
 */
public final class ControlFlowBlockTest {

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

        public void assertNumberOfFoundControlFlowBlocks(final int expected) {
            assertEquals(expected, controlFlowBlocks.size());
        }

        public void assertFirstIsPredecessorOfSecond(final int first, final int second) {
            final ControlFlowBlock f = controlFlowBlocks.get(first);
            final ControlFlowBlock s = controlFlowBlocks.get(second);
            assertTrue(f.isPredecessorOf(s));
        }

        public void assertFirstIsDirectPredecessorOfSecond(final int first, final int second) {
            final ControlFlowBlock f = controlFlowBlocks.get(first);
            final ControlFlowBlock s = controlFlowBlocks.get(second);
            assertTrue(f.isDirectPredecessorOf(s));
        }

        public void assertSecondIsSuccessorOfFirst(final int first, final int second) {
            final ControlFlowBlock f = controlFlowBlocks.get(first);
            final ControlFlowBlock s = controlFlowBlocks.get(second);
            assertTrue(s.isSuccessorOf(f));
        }

        public void assertBlockContainsEffectiveAssignmentInsn(final int blockNumber) {
            final ControlFlowBlock b = getBlockWithNumber(blockNumber);
            final FieldNode assignedVariable = convenienceClassNode.findVariableWithName(variableName);
            assertTrue(b.containsEffectiveAssignmentInstruction(assignedVariable));
        }

        public void assertBlockContainsConditionCheck(final int blockNumber) {
            final ControlFlowBlock b = getBlockWithNumber(blockNumber);
            assertTrue(b.containsConditionCheck());
        }

        private ControlFlowBlock getBlockWithNumber(final int blockNumber) {
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
    } // class Helper


    private ControlFlowBlock cfb = null;

    @After
    public void nullifyCfb() {
        cfb = null;
    }

    @Test
    public void rangeCoverageWorks() {
        initialiseCfbWith("L<Pseudo>", 0, 1, 2, 3, 4, 5);
        assertCfbDoesNotCover(-1);
        assertCfbCovers(0);
    }

    private void initialiseCfbWith(final String id, final int first, final int ... further) {
        final RangeBuilder rb = new RangeBuilder();
        rb.add(first);
        for (final int f : further) {
            rb.add(f);
        }
        cfb = ControlFlowBlock.newInstance(0, id, new AbstractInsnNode[0], rb.build(),
                Collections.<ControlFlowBlock> emptySet());
    }

    private void assertCfbDoesNotCover(final int notCovered) {
        assertFalse(cfb.covers(notCovered));
    }
    
    private void assertCfbCovers(final int covered) {
        assertTrue(cfb.covers(covered));
    }

    @Test
    public void recognizedBlockSequenceOfIntegerWithDefaultIsCorrect() {
        final Helper h = new Helper(IntegerWithDefault.class, "hash", "hashCode");
        h.assertNumberOfFoundControlFlowBlocks(3);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(0, 2);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(0, 2);
        h.assertSecondIsSuccessorOfFirst(1, 2);
    }

    @Test
    public void findBlockWithEffectiveAssignmentInsnForIntegerWithDefault() {
        final Helper h = new Helper(IntegerWithDefault.class, "hash", "hashCode");
        h.assertBlockContainsEffectiveAssignmentInsn(1);
    }

    @Test
    public void findBlockWithConditionCheckForIntegerWithDefault() {
        final Helper h = new Helper(IntegerWithDefault.class, "hash", "hashCode");
        h.assertBlockContainsConditionCheck(0);
    }

    @Test
    public void recognizedBlockSequenceOfJavaLangStringIsCorrect() {
        final Helper h = new Helper(String.class, "hash", "hashCode");
        h.assertNumberOfFoundControlFlowBlocks(4);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(0, 3);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertFirstIsPredecessorOfSecond(2, 3);
        h.assertFirstIsPredecessorOfSecond(0, 2);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(0, 2);
        h.assertSecondIsSuccessorOfFirst(0, 3);
        h.assertSecondIsSuccessorOfFirst(1, 2);
        h.assertSecondIsSuccessorOfFirst(1, 3);
        h.assertSecondIsSuccessorOfFirst(2, 3);
    }

    @Test
    public void findBlockWithEffectiveAssignmentInsnForJavaLangString() {
        final Helper h = new Helper(String.class, "hash", "hashCode");
        h.assertBlockContainsEffectiveAssignmentInsn(2);
    }

    @Test
    public void findBlocksWithConditionCheckForJavaLangString() {
        final Helper h = new Helper(String.class, "hash", "hashCode");
        h.assertBlockContainsConditionCheck(0);
        h.assertBlockContainsConditionCheck(1);
    }

    @Test
    public void recognizedBlockSequenceOfFloatWithDefaultIsCorrecdt() {
        final Helper h = new Helper(FloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertNumberOfFoundControlFlowBlocks(3);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(0, 2);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(0, 2);
        h.assertSecondIsSuccessorOfFirst(1, 2);
    }

    @Test
    public void findBlockWithEffectiveAssignmentInsnForFloatWithDefault() {
        final Helper h = new Helper(FloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertBlockContainsEffectiveAssignmentInsn(1);
    }

    @Test
    public void findblockWithConditionCheckForFloatwithDefault() {
        final Helper h = new Helper(FloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertBlockContainsConditionCheck(0);
    }

    @Test
    public void recognizedBlockSequenceOfAliasedFloatWithDefaultIsCorrecdt() {
        final Helper h = new Helper(AliasedFloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertNumberOfFoundControlFlowBlocks(5);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertFirstIsPredecessorOfSecond(2, 3);
        h.assertFirstIsPredecessorOfSecond(3, 4);
        h.assertFirstIsDirectPredecessorOfSecond(1, 4);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(1, 2);
        h.assertSecondIsSuccessorOfFirst(1, 4);
        h.assertSecondIsSuccessorOfFirst(1, 3);
    }

    @Test
    public void findBlockWithEffectiveAssignmentInsnForAliasedFloatWithDefault() {
        final Helper h = new Helper(AliasedFloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertBlockContainsEffectiveAssignmentInsn(3);
    }

    @Test
    public void findblockWithConditionCheckForAliasedFloatwithDefault() {
        final Helper h = new Helper(AliasedFloatWithDefault.class, "hash", "hashCodeFloat");
        h.assertBlockContainsConditionCheck(1);
    }

}
