/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.03.2013
 */
final class AssignmentGuard implements JumpInsn {

    /**
     * Builder for instances of {@link AssignmentGuard}.
     */
    @NotThreadSafe
    public static final class Builder {

        private final JumpInsn jumpInstruction;
        private final ArrayList<AbstractInsnNode> predecessorInstructions;

        public Builder(final JumpInsn theJumpInstruction) {
            jumpInstruction = theJumpInstruction;
            predecessorInstructions = new ArrayList<AbstractInsnNode>();
        }

        public void addPredecessorInstruction(final AbstractInsnNode predecessor) {
            predecessorInstructions.add(predecessor);
        }

        public AssignmentGuard build() {
            predecessorInstructions.trimToSize();
            return AssignmentGuard.newInstance(jumpInstruction, predecessorInstructions);
        }

        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            b.append(getDeclaringClassPrefix()).append(getClass().getSimpleName());
            b.append(" [jumpInstruction=").append(jumpInstruction);
            b.append(", predecessorInstructions=").append(predecessorInstructions).append("]");
            return b.toString();
        }

        private String getDeclaringClassPrefix() {
            final String result;
            final Class<?> superclass = getClass().getDeclaringClass();
            if (null != superclass) {
                final StringBuilder b = new StringBuilder();
                b.append(superclass.getSimpleName()).append(".");
                result = b.toString();
            } else {
                result = "";
            }
            return result;
        }

    } // class AssignmentGuardBuilder


    private final JumpInsn delegationTarget;
    private final List<AbstractInsnNode> predecessorInstructions;

    private AssignmentGuard(final JumpInsn theDelegationTarget,
            final List<AbstractInsnNode> thePredecessorInstructions) {
        delegationTarget = theDelegationTarget;
        predecessorInstructions = thePredecessorInstructions;
    }

    public static AssignmentGuard newInstance(final JumpInsn delegationTarget,
            final List<AbstractInsnNode> predecessorInstructions) {
        validateArguments(delegationTarget, predecessorInstructions);
        return new AssignmentGuard(delegationTarget, predecessorInstructions);
    }

    private static void validateArguments(final JumpInsn delegationTarget,
            final List<AbstractInsnNode> predecessorInstructions) {
        final String msg = "Argument '{}' must not be {}!";
        notNull(delegationTarget, msg, "delegationTarget", "null");
        notNull(predecessorInstructions, msg, "predecessorInstructions", "null");
        notEmpty(predecessorInstructions, msg, "predecessorInstructions", "empty");
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return delegationTarget.getJumpInsnNode();
    }

    @Override
    public int getIndexWithinBlock() {
        return delegationTarget.getIndexWithinBlock();
    }

    @Override
    public int getIndexWithinMethod() {
        return delegationTarget.getIndexWithinMethod();
    }

    @Override
    public LabelNode getLabelNodeOfJumpTarget() {
        return delegationTarget.getLabelNodeOfJumpTarget();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isAssignmentGuard() {
        return true;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        return delegationTarget.compareTo(o);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + delegationTarget.hashCode();
        result = prime * result + predecessorInstructions.hashCode();
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
        if (!(obj instanceof AssignmentGuard)) {
            return false;
        }
        final AssignmentGuard other = (AssignmentGuard) obj;
        if (!delegationTarget.equals(other.delegationTarget)) {
            return false;
        }
        if (!predecessorInstructions.equals(other.predecessorInstructions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [delegationTarget=").append(delegationTarget);
        b.append(", predecessorInstructions=").append(predecessorInstructions).append("]");
        return b.toString();
    }

}
