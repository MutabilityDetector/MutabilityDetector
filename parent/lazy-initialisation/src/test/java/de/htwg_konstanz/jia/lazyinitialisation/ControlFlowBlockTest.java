package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.Range.RangeBuilder;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 19.02.2013
 */
public final class ControlFlowBlockTest {

    private static final class Asserter {

        private Asserter() {
            super();
        }

        public static List<ControlFlowBlock> getControlFlowBlocksFor(final Class<?> klasse, final String methodName) {
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

        public static void assertFirstIsPredecessorOfSecond(final ControlFlowBlock first, final ControlFlowBlock second) {
            assertTrue(first.isPredecessorOf(second));
        }

        public static void assertSecondIsSuccessorOfFirst(final ControlFlowBlock first, final ControlFlowBlock second) {
            assertTrue(second.isSuccessorOf(first));
        }

    } // class Asserter


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
        cfb = ControlFlowBlock.newInstance(0, id, new AbstractInsnNode[0], rb.build());
    }

    private void assertCfbDoesNotCover(final int notCovered) {
        assertFalse(cfb.covers(notCovered));
    }
    
    private void assertCfbCovers(final int covered) {
        assertTrue(cfb.covers(covered));
    }

    @Test
    public void blockSequenceOfIntegerWithDefaultIsCorrect() {
        final List<ControlFlowBlock> cfbs = Asserter.getControlFlowBlocksFor(IntegerWithDefault.class, "hashCode");
        assertEquals(3, cfbs.size());
        Asserter.assertFirstIsPredecessorOfSecond(cfbs.get(0), cfbs.get(1));
        Asserter.assertFirstIsPredecessorOfSecond(cfbs.get(0), cfbs.get(2));
        Asserter.assertFirstIsPredecessorOfSecond(cfbs.get(1), cfbs.get(2));

        Asserter.assertSecondIsSuccessorOfFirst(cfbs.get(0), cfbs.get(1));
        Asserter.assertSecondIsSuccessorOfFirst(cfbs.get(0), cfbs.get(2));
        Asserter.assertSecondIsSuccessorOfFirst(cfbs.get(1), cfbs.get(2));
    }

}
