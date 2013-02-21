package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.Map;

import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;

/**
 * TODO Klasse löschen?
 *
 * @author Juergen Fickel
 * @version 21.02.2013
 */
final class ControlFlowBlockManager {

    private final Map<Integer, ControlFlowBlock> controlFlowBlocks;

    private ControlFlowBlockManager(final String owner, final MethodNode setter) {
        final ControlFlowBlockFactory factory = ControlFlowBlockFactory.newInstance(owner, setter);
        controlFlowBlocks = factory.getAllControlFlowBlocksForMethodInMap();
    }

    public static ControlFlowBlockManager newInstance(final String owner, final MethodNode setter) {
        return new ControlFlowBlockManager(notEmpty(owner), notNull(setter));
    }

    public Map<Integer, ControlFlowBlock> getAllControlFlowBlocks() {
        return Collections.unmodifiableMap(controlFlowBlocks);
    }

    public ControlFlowBlock getBlockWithNumber(final int blockNumber) {
        return controlFlowBlocks.get(Integer.valueOf(blockNumber));
    }

}
