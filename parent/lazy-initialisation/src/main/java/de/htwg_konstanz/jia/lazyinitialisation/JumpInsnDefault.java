/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import javax.annotation.concurrent.Immutable;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@Immutable
final class JumpInsnDefault implements JumpInsn {

    private final JumpInsnNode jumpInsnNode;
    private final int indexWithinBlock;
    private final int indexWithinMethod;

    private JumpInsnDefault(final JumpInsnNode theJumpInsnNode,
            final int theIndexWithinBlock,
            final int theIndexWithinMethod) {
        jumpInsnNode = deepCopy(theJumpInsnNode);
        indexWithinBlock = theIndexWithinBlock;
        indexWithinMethod = theIndexWithinMethod;
    }

    private static JumpInsnNode deepCopy(final JumpInsnNode source) {
        final int resultOpcode = source.getOpcode();
        final LabelNode sourceLabelNode = source.label;
        final LabelNode resultLabelNode = new LabelNode(sourceLabelNode.getLabel());
        return new JumpInsnNode(resultOpcode, resultLabelNode);
    }

    public static JumpInsnDefault newInstance(final JumpInsnNode jumpInsnNode,
            final int indexWithinBlock,
            final int indexWithinMethod) {
        return new JumpInsnDefault(notNull(jumpInsnNode), indexWithinBlock, indexWithinMethod);
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return deepCopy(jumpInsnNode);
    }

    @Override
    public int getIndexWithinBlock() {
        return indexWithinBlock;
    }

    @Override
    public int getIndexWithinMethod() {
        return indexWithinMethod;
    }

    @Override
    public LabelNode getLabelNodeOfJumpTarget() {
        return new LabelNode(jumpInsnNode.label.getLabel());
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isAssignmentGuard() {
        return false;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        final Integer thisIndexWithinMethod = Integer.valueOf(indexWithinMethod);
        final Integer otherIndexWithinMethod = Integer.valueOf(o.getIndexWithinMethod());
        return thisIndexWithinMethod.compareTo(otherIndexWithinMethod);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + indexWithinBlock;
        result = prime * result + indexWithinMethod;
        result = prime * result + jumpInsnNode.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JumpInsnDefault)) {
            return false;
        }
        final JumpInsnDefault other = (JumpInsnDefault) obj;
        if (indexWithinMethod != other.indexWithinMethod) {
            return false;
        }
        if (!jumpInsnNode.equals(other.jumpInsnNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("jumpInsnNode=").append(toString(jumpInsnNode));
        builder.append(", indexWithinBlock=").append(indexWithinBlock);
        builder.append(", indexWithinMethod=").append(indexWithinMethod);
        builder.append("]");
        return builder.toString();
    }

    private static String toString(final JumpInsnNode jumpInsnNode) {
        final StringBuilder result = new StringBuilder();
        final Opcode opcode = Opcode.forInt(jumpInsnNode.getOpcode());
        final LabelNode labelNode = jumpInsnNode.label;
        final Label label = labelNode.getLabel();
        result.append("[").append(opcode.toString()).append(", label=").append(label).append("]");
        return result.toString();
    }

}
