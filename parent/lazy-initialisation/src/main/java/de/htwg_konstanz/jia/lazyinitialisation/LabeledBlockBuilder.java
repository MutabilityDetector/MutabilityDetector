/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.Opcode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
@NotThreadSafe
final class LabeledBlockBuilder {

    private final AtomicInteger positionNumber;
    private final AtomicReference<LabelNode> labelNode;
    private final AtomicReference<FieldNode> affectedVariable;
    private final List<AbstractInsnNode> instructions;

    public LabeledBlockBuilder() {
        positionNumber = new AtomicInteger(-1);
        labelNode = new AtomicReference<LabelNode>(null);
        affectedVariable = new AtomicReference<FieldNode>(null);
        instructions = new ArrayList<AbstractInsnNode>();
    }

    public LabeledBlockBuilder setLabel(final LabelNode theLabelNode) {
        labelNode.set(theLabelNode);
        return this;
    }

    public LabeledBlockBuilder setAffectedVariable(final FieldNode variable) {
        affectedVariable.set(variable);
        return this;
    }

    public LabeledBlockBuilder addInstruction(final AbstractInsnNode instruction) {
        instructions.add(instruction);
        return this;
    }

    public LabeledBlock build() {
        LabeledBlock result = NullBlock.getInstance();
        if (isAliasCreationBlock()) {
//            result = new AliasCreationBlock();
        } else if (isConditionBlock()) {
//            result = new ConditionBlock();
        }
        final int currentPositionNumber = positionNumber.getAndIncrement();
        final LabelNode currentLabelNode = labelNode.getAndSet(null);
        final FieldNode currentAffectedVariable = affectedVariable.getAndSet(null);
        instructions.clear();
        // TODO Method implementieren.
        return result;
    }

    private boolean isAliasCreationBlock() {
        if (0 < positionNumber.get()) {
            return false;
        }
        boolean containsGetfieldInstruction = false;
        for (final AbstractInsnNode instruction : instructions) {
            final Opcode opcode = Opcode.forInt(instruction.getOpcode());
            if (GETFIELD == opcode) {
                containsGetfieldInstruction = true;
            } else if (containsGetfieldInstruction && isInclusiveBetweenIstoreAndAstore3(opcode)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInclusiveBetweenIstoreAndAstore3(final Opcode opcode) {
        final int opcodeAsInt = opcode.asInt();
        return opcodeAsInt >= ISTORE.asInt() && opcodeAsInt <= ASTORE_3.asInt();
    }

    private boolean isConditionBlock() {
        boolean containsAload0 = false;
//        boolean contains
        return false;
    }

}
