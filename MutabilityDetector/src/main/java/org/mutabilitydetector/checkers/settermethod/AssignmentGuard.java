/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.objectweb.asm.tree.AbstractInsnNode.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.tree.*;

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


    @Immutable
    private static final class InstructionNodesHashCodeCalculator {

        private static final byte INITIAL_RESULT = 1;

        private final int prime;

        public InstructionNodesHashCodeCalculator(final int thePrime) {
            prime = thePrime;
        }

        public int hashCode(final AbstractInsnNode insn) {
            final int result;
            final int nodeType = insn.getType();
            if (FIELD_INSN == nodeType) {
                result = hashCode((FieldInsnNode) insn);
            } else if (VAR_INSN == nodeType) {
                result = hashCode((VarInsnNode) insn);
            } else if (INSN == nodeType) {
                result = hashCode((InsnNode) insn);
            } else if (INT_INSN == nodeType) {
                result = hashCode((IntInsnNode) insn);
            } else if (INVOKE_DYNAMIC_INSN == nodeType) {
                result = hashCode((InvokeDynamicInsnNode) insn);
            } else if (JUMP_INSN == nodeType) {
                result = hashCode((JumpInsnNode) insn);
            } else if (LABEL == nodeType) {
                result = hashCode((LabelNode) insn);
            } else if (LDC_INSN == nodeType) {
                result = hashCode((LdcInsnNode) insn);
            } else if (LINE == nodeType) {
                result = hashCode((LineNumberNode) insn);
            } else if (METHOD_INSN == nodeType) {
                result = hashCode((MethodInsnNode) insn);
            } else if (IINC_INSN == nodeType) {
                result = hashCode((IincInsnNode) insn);
            } else if (LOOKUPSWITCH_INSN == nodeType) {
                result = hashCode((LookupSwitchInsnNode) insn);
            } else if (FRAME == nodeType) {
                result = hashCode((FrameNode) insn);
            } else if (TYPE_INSN == nodeType) {
                result = hashCode((TypeInsnNode) insn);
            } else if (MULTIANEWARRAY_INSN == nodeType) {
                result = hashCode((MultiANewArrayInsnNode) insn);
            } else if (TABLESWITCH_INSN == nodeType) {
                result = hashCode((TableSwitchInsnNode) insn);
            } else {
                result = insn.hashCode();
            }
            return result;
        }

        private int hashCode(final FieldInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.desc.hashCode());
            result = hashCode(result, insn.name.hashCode());
            result = hashCode(result, insn.owner.hashCode());
            return result;
        }

        private final int hashCode(final int preliminaryResult, final int hashCode) {
            return prime * preliminaryResult + hashCode;
        }

        private int hashCode(final VarInsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.var);
        }

        private int hashCode(final InsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.hashCode());
        }

        private int hashCode(final IntInsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.operand);
        }

        private int hashCode(final InvokeDynamicInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.name.hashCode());
            result = hashCode(result, insn.desc.hashCode());
            result = hashCode(result, insn.bsm.hashCode());
            result = hashCode(result, insn.hashCode());
            return result;
        }

        private int hashCode(final JumpInsnNode insn) {
            return hashCode(INITIAL_RESULT, 0);
        }

        private int hashCode(final LabelNode insn) {
            return hashCode(INITIAL_RESULT, 0);
        }

        private int hashCode(final LdcInsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.cst.hashCode());
        }

        private int hashCode(final LineNumberNode insn) {
            return hashCode(INITIAL_RESULT, 0);
        }

        private int hashCode(final MethodInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.desc.hashCode());
            result = hashCode(result, insn.name.hashCode());
            result = hashCode(result, insn.owner.hashCode());
            return result;
        }

        private int hashCode(final IincInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.var);
            result = hashCode(result, insn.incr);
            return result;
        }

        private int hashCode(final LookupSwitchInsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.keys.hashCode());
        }

        private int hashCode(final FrameNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.local.hashCode());
            result = hashCode(result, insn.stack.hashCode());
            return result;
        }

        private int hashCode(final TypeInsnNode insn) {
            return hashCode(INITIAL_RESULT, insn.desc.hashCode());
        }

        private int hashCode(final MultiANewArrayInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.dims);
            result = hashCode(result, insn.desc.hashCode());
            return result;
        }

        private int hashCode(final TableSwitchInsnNode insn) {
            int result = INITIAL_RESULT;
            result = hashCode(result, insn.min);
            result = hashCode(result, insn.max);
            return result;
        }

    } // class InstructionNodeHashCodeCalculator


    @Immutable
    private static final class InstructionNodesComparator {

        public boolean equals(final FieldInsnNode f1, final FieldInsnNode f2) {
            return f1.desc.equals(f2.desc) && f1.name.equals(f2.name) && f1.owner.equals(f2.owner);
        }

        public boolean equals(final VarInsnNode insnThis, final VarInsnNode insnOther) {
            return insnThis.var == insnOther.var;
        }

        public boolean equals(final InsnNode insnThis, final InsnNode insnOther) {
            return true;
        }

        public boolean equals(final IntInsnNode insnThis, final IntInsnNode insnOther) {
            return insnThis.operand == insnOther.operand;
        }

        public boolean equals(final InvokeDynamicInsnNode i1, final InvokeDynamicInsnNode i2) {
            return i1.name.equals(i2.name) && i1.desc.equals(i2.desc) && i1.bsm.equals(i2.bsm)
                    && Arrays.equals(i1.bsmArgs, i2.bsmArgs);
        }

        public boolean equals(final JumpInsnNode insnThis, final JumpInsnNode insnOther) {
            return true;
        }

        public boolean equals(final LabelNode insnThis, final LabelNode insnOther) {
            return true;
        }

        public boolean equals(final LdcInsnNode insnThis, final LdcInsnNode insnOther) {
            return insnThis.cst.equals(insnOther.cst);
        }

        public boolean equals(final LineNumberNode insnThis, final LineNumberNode insnOther) {
            return true;
        }

        public boolean equals(final MethodInsnNode m1, final MethodInsnNode m2) {
            return m1.desc.equals(m2.name) && m1.name.equals(m2.name) && m1.owner.equals(m2.owner);
        }

        public boolean equals(final IincInsnNode i1, final IincInsnNode i2) {
            return i1.var == i2.var && i1.incr == i2.incr;
        }

        public boolean equals(final LookupSwitchInsnNode l1, final LookupSwitchInsnNode l2) {
            return l1.keys.equals(l2.keys);
        }

        public boolean equals(final FrameNode f1, final FrameNode f2) {
            return f1.local.equals(f2.local) && f1.stack.equals(f2.stack);
        }

        public boolean equals(final TypeInsnNode t1, final TypeInsnNode t2) {
            return t1.desc.equals(t2.desc);
        }

        public boolean equals(final MultiANewArrayInsnNode m1, final MultiANewArrayInsnNode m2) {
            return m1.desc.equals(m2.desc) && m1.dims == m2.dims;
        }

        public boolean equals(final TableSwitchInsnNode t1, final TableSwitchInsnNode t2) {
            return t1.min == t2.min && t1.max == t2.max;
        }

    } // class InstructionNodesComparator


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
        final String msg = "Argument '%s' must not be %s!";
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
        final JumpInsnNode jumpInsnNode = getJumpInsnNode();
        result = prime * result + jumpInsnNode.getOpcode();
        final InstructionNodesHashCodeCalculator hcc = new InstructionNodesHashCodeCalculator(prime);
        for (final AbstractInsnNode predecessor : predecessorInstructions) {
            final int hashCodeOfPredecessor = hcc.hashCode(predecessor);
            result = prime * result + hashCodeOfPredecessor;
        }
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
        final JumpInsnNode jumpInsnNodeThis = delegationTarget.getJumpInsnNode();
        final JumpInsnNode jumpInsnNodeOther = other.getJumpInsnNode();
        if (jumpInsnNodeThis.getOpcode() != jumpInsnNodeOther.getOpcode()) {
            return false;
        }
        if (predecessorInstructions.size() != other.predecessorInstructions.size()) {
            return false;
        }
        final InstructionNodesComparator inc = new InstructionNodesComparator();
        for (int i = 0; i < predecessorInstructions.size(); i++) {
            final AbstractInsnNode insnThis = predecessorInstructions.get(i);
            final AbstractInsnNode insnOther = other.predecessorInstructions.get(i);
            if (insnThis.getOpcode() != insnOther.getOpcode()) {
                return false;
            }
            if (instructionsAreUnequal(insnThis, insnOther, inc)) {
                return false;
            }
        }
        return true;
    }

    private static boolean instructionsAreUnequal(final AbstractInsnNode insnThis,
            final AbstractInsnNode insnOther,
            final InstructionNodesComparator inc) {
        final int nodeType = insnThis.getType();
        final boolean result;
        if (FIELD_INSN == nodeType) {
            result = inc.equals((FieldInsnNode) insnThis, (FieldInsnNode) insnOther);
        } else if (VAR_INSN == nodeType) {
            result = inc.equals((VarInsnNode) insnThis, (VarInsnNode) insnOther);
        } else if (INSN == nodeType) {
            result = inc.equals((InsnNode) insnThis, (InsnNode) insnOther);
        } else if (INT_INSN == nodeType) {
            result = inc.equals((IntInsnNode) insnThis, (IntInsnNode) insnOther);
        } else if (INVOKE_DYNAMIC_INSN == nodeType) {
            result = inc.equals((InvokeDynamicInsnNode) insnThis, (InvokeDynamicInsnNode) insnOther);
        } else if (JUMP_INSN == nodeType) {
            result = inc.equals((JumpInsnNode) insnThis, (JumpInsnNode) insnOther);
        } else if (LABEL == nodeType) {
            result = inc.equals((LabelNode) insnThis, (LabelNode) insnOther);
        } else if (LDC_INSN == nodeType) {
            result = inc.equals((LdcInsnNode) insnThis, (LdcInsnNode) insnOther);
        } else if (LINE == nodeType) {
            result = inc.equals((LineNumberNode) insnThis, (LineNumberNode) insnOther);
        } else if (METHOD_INSN == nodeType) {
            result = inc.equals((MethodInsnNode) insnThis, (MethodInsnNode) insnOther);
        } else if (IINC_INSN == nodeType) {
            result = inc.equals((IincInsnNode) insnThis, (IincInsnNode) insnOther);
        } else if (LOOKUPSWITCH_INSN == nodeType) {
            result = inc.equals((LookupSwitchInsnNode) insnThis, (LookupSwitchInsnNode) insnOther);
        } else if (FRAME == nodeType) {
            result = inc.equals((FrameNode) insnThis, (FrameNode) insnOther);
        } else if (TYPE_INSN == nodeType) {
            result = inc.equals((TypeInsnNode) insnThis, (TypeInsnNode) insnOther);
        } else if (MULTIANEWARRAY_INSN == nodeType) {
            result = inc.equals((MultiANewArrayInsnNode) insnThis, (MultiANewArrayInsnNode) insnOther);
        } else if (TABLESWITCH_INSN == nodeType) {
            result = inc.equals((TableSwitchInsnNode) insnThis, (TableSwitchInsnNode) insnOther);
        } else {
            result = false;
        }
        return !result;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append(" [delegationTarget=").append(delegationTarget);
        b.append(", predecessorInstructions=").append(predecessorInstructions).append("]");
        return b.toString();
    }

}
