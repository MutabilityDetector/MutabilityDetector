package de.htwg_konstanz.jia.lazyinitialisation;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * TODO Klasse löschen?
 *
 * @author Juergen Fickel
 * @version 21.02.2013
 */
public final class ControlFlowBlockManagerTest {

    private ControlFlowBlockManager manager = null;

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void numberOfBlocksIsExpected() {
        final byte expected = 3;
        initialiseManagerWith(WithoutAlias.WithJvmInitialValue.IntegerValid.class, "hashCode");
        final Map<Integer, ControlFlowBlock> controlFlowBlocks = manager.getAllControlFlowBlocks();
        assertEquals(expected, controlFlowBlocks.size());
    }

    private void initialiseManagerWith(final Class<?> klasse, final String methodName) {
        final ConvenienceClassNode ccn = createConvenienceClassNodeFor(klasse);
        final String owner = ccn.name();
        final MethodNode methodNode = ccn.findMethodWithName(methodName);
        manager = ControlFlowBlockManager.newInstance(owner, methodNode);        
    }

    private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        return factory.convenienceClassNodeFor(klasse);
    }

    @Test
    public void gotBlockByNumber() {
        final byte blockNumber = 1;
        initialiseManagerWith(String.class, "hashCode");
        final ControlFlowBlock block = manager.getBlockWithNumber(blockNumber);
        assertEquals(blockNumber, block.getBlockNumber());
    }

}
