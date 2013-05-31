package org.mutabilitydetector.checkers.settermethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithAlias;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;
import org.mutabilitydetector.checkers.settermethod.ControlFlowBlock.ControlFlowBlockFactory;
import org.mutabilitydetector.checkers.settermethod.ControlFlowBlock.Range;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * 
 * 
 * @author Juergen Fickel
 * @version 19.02.2013
 */
public final class ControlFlowBlockTest {

    private static final class Helper {

        private final Class<?> klasse;
        private final String methodName;
        private final List<ControlFlowBlock> controlFlowBlocks;

        private Helper(final Class<?> theKlasse, final String theMethodName) {
            super();
            klasse = theKlasse;
            methodName = theMethodName;
            controlFlowBlocks = getControlFlowBlocksFor(klasse, methodName);
        }

        private static List<ControlFlowBlock> getControlFlowBlocksFor(final Class<?> klasse, final String methodName) {
            List<ControlFlowBlock> result = Collections.emptyList();
            final EnhancedClassNode ccn = createConvenienceClassNodeFor(klasse);
            final List<MethodNode> methods = ccn.findMethodByName(methodName);
            final MethodNode method = methods.get(0);
            if (isNotNull(method)) {
                result = createControlFlowBlocksFor(ccn.getName(), method);
            }
            return result;
        }

        private static EnhancedClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.getConvenienceClassNodeFor(klasse);
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

    private void initialiseCfbWith(final String id, final int first, final int... further) {
        final SortedSet<Integer> rangeItems = new TreeSet<Integer>();
        rangeItems.add(Integer.valueOf(first));
        for (final int f : further) {
            rangeItems.add(Integer.valueOf(f));
        }
        cfb = ControlFlowBlock.newInstance(0, id, new InsnList(), Range.newInstance(rangeItems));
    }

    private void assertCfbDoesNotCover(final int notCovered) {
        assertFalse(cfb.covers(notCovered));
    }

    private void assertCfbCovers(final int covered) {
        assertTrue(cfb.covers(covered));
    }

    @Test
    public void recognizedBlockSequenceOfValidIntegerWithJvmInitialValueIsCorrect() {
        final Helper h = new Helper(WithoutAlias.WithJvmInitialValue.IntegerValid.class, "hashCode");
        h.assertNumberOfFoundControlFlowBlocks(3);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(0, 2);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(0, 2);
        h.assertSecondIsSuccessorOfFirst(1, 2);
    }

    @Test
    @Ignore("Bytecode of String seems to be platform dependent.")
    public void recognizedBlockSequenceOfJavaLangStringIsCorrect() {
        final Helper h = new Helper(String.class, "hashCode");
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
    public void recognizedBlockSequenceOfValidFloatWithJvmInitialValueIsCorrect() {
        final Helper h = new Helper(WithoutAlias.WithJvmInitialValue.FloatValid.class, "hashCodeFloat");
        h.assertNumberOfFoundControlFlowBlocks(3);
        h.assertFirstIsPredecessorOfSecond(0, 1);
        h.assertFirstIsPredecessorOfSecond(0, 2);
        h.assertFirstIsPredecessorOfSecond(1, 2);
        h.assertSecondIsSuccessorOfFirst(0, 1);
        h.assertSecondIsSuccessorOfFirst(0, 2);
        h.assertSecondIsSuccessorOfFirst(1, 2);
    }

    @Test
    public void recognizedBlockSequenceOfAliasedValidFloatWithJvmInitialValueIsCorrect() {
        final Helper h = new Helper(WithAlias.WithJvmInitialValue.FloatValid.class, "hashCodeFloat");
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

}
