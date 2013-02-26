package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
@NotThreadSafe
final class InitialValueFinder implements Runnable {

    @Immutable
    private static final class InitialValueFactory {
        public UnknownTypeValue getConcreteInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
            UnknownTypeValue result = null;
            if (isLdcInsn(variableValueSetupInsn)) {
                result = getInitialValueOfLdcInsn(variableValueSetupInsn);
            } else if (isIntInsn(variableValueSetupInsn)) {
                result = getInitialValueOfIntInsn(variableValueSetupInsn);
            } else if (isStackConstantPushInsn(variableValueSetupInsn)) {
                result = getInitialValueOfStackConstantInsn(variableValueSetupInsn);
            }
            return result;
        }

        private static boolean isLdcInsn(final AbstractInsnNode abstractInsnNode) {
            return AbstractInsnNode.LDC_INSN == abstractInsnNode.getType();
        }

        private static UnknownTypeValue getInitialValueOfLdcInsn(final AbstractInsnNode variableValueSetupInsn) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) variableValueSetupInsn;
            final Object cst = ldcInsn.cst;
            return UnknownTypeValueDefault.getInstance(cst);
        }

        private static boolean isIntInsn(final AbstractInsnNode abstractInsnNode) {
            return AbstractInsnNode.INT_INSN == abstractInsnNode.getType();
        }

        private static UnknownTypeValue getInitialValueOfIntInsn(final AbstractInsnNode variableValueSetupInsn) {
            final IntInsnNode singleIntOperandInsn = (IntInsnNode) variableValueSetupInsn;
            final int operand = singleIntOperandInsn.operand;
            return UnknownTypeValueDefault.getInstance(Integer.valueOf(operand));
        }

        private static boolean isStackConstantPushInsn(final AbstractInsnNode abstractInsnNode) {
            final SortedSet<Opcode> constantsInstructions = Opcode.constants();
            final Opcode opcode = Opcode.forInt(abstractInsnNode.getOpcode());
            return constantsInstructions.contains(opcode);
        }

        private static UnknownTypeValue getInitialValueOfStackConstantInsn(final AbstractInsnNode variableValueSetupInsn) {
            final Opcode opcode = Opcode.forInt(variableValueSetupInsn.getOpcode());
            return opcode.stackValue();
        }

        public UnknownTypeValue getJvmDefaultInitialValueFor(final Type type) {
            final UnknownTypeValue result;
            final int sort = type.getSort();
            if (Type.BOOLEAN == sort) {
                result = UnknownTypeValueDefault.getInstance(Boolean.FALSE);
            } else if (Type.BYTE == sort) {
                result = UnknownTypeValueDefault.getInstance(Byte.valueOf((byte) 0));
            } else if (Type.CHAR == sort) {
                result = UnknownTypeValueDefault.getInstance(Character.valueOf((char) 0));
            } else if (Type.SHORT == sort) {
                result = UnknownTypeValueDefault.getInstance(Short.valueOf((short) 0));
            } else if (Type.INT == sort) {
                result = UnknownTypeValueDefault.getInstance(Integer.valueOf(0));
            } else if (Type.LONG == sort) {
                result = UnknownTypeValueDefault.getInstance(Long.valueOf(0L));
            } else if (Type.FLOAT == sort) {
                result = UnknownTypeValueDefault.getInstance(Float.valueOf(0.0F));
            } else if (Type.DOUBLE == sort) {
                result = UnknownTypeValueDefault.getInstance(Double.valueOf(0.0D));
            } else {
                result = UnknownTypeValueDefault.getInstance(null);
            }
            return result;
        }
    } // class JvmInitialValueFactory


    private volatile boolean isRun;
    private final FieldNode variable;
    private final Setters setters;
    private final Set<UnknownTypeValue> possibleInitialValues;

    private InitialValueFinder(final FieldNode theVariable, final Setters theSetters) {
        isRun = false;
        variable = theVariable;
        setters = theSetters;
        final byte supposedMaximumOfPossibleInitialValues = 5;
        possibleInitialValues = new HashSet<UnknownTypeValue>(supposedMaximumOfPossibleInitialValues);
    }

    /**
     * Factory method for this class. None of the parameters must be
     * {@code null}.
     * 
     * @param variable
     *            the variable to find the initial value for.
     * @param setters
     *            the setters for {@code variable}.
     * @return a new instance of this class.
     */
    public static InitialValueFinder newInstance(final FieldNode variable, final Setters setters) {
        return new InitialValueFinder(notNull(variable), notNull(setters));
    }

    @Override
    public void run() {
        if (hasNoConstructors()) {
            addJvmInitialValueForVariable();
        } else {
            addConcreteInitialValuesByConstructor();
        }
        isRun = true;
    }

    private boolean hasNoConstructors() {
        final List<MethodNode> constructors = setters.constructors();
        return constructors.isEmpty();
    }

    private void addJvmInitialValueForVariable() {
        final InitialValueFactory factory = new InitialValueFactory();
        final Type type = Type.getType(variable.desc);
        possibleInitialValues.add(factory.getJvmDefaultInitialValueFor(type));
    }

    private void addConcreteInitialValuesByConstructor() {
        for (final MethodNode constructor : setters.constructors()) {
            final AbstractInsnNode[] insns = constructor.instructions.toArray();
            final EffectivePutfieldInsnFinder putfieldFinder = EffectivePutfieldInsnFinder.newInstance(
                    variable, constructor.instructions);
            final AssignmentInsn effectivePutfieldInstruction = putfieldFinder.getEffectivePutfieldInstruction();
            final int indexOfAssignmentInstruction = effectivePutfieldInstruction.getIndexOfAssignmentInstruction();
            addPossibleInitialValueFor(insns[indexOfAssignmentInstruction - 1]);
        }
    }

    private void addPossibleInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
        final InitialValueFactory factory = new InitialValueFactory();
        final UnknownTypeValue initialValue = factory.getConcreteInitialValueFor(variableValueSetupInsn);
        if (null != initialValue) {
            possibleInitialValues.add(initialValue);
        }
    }

    /**
     * Gets all possible values the given variable may have after initialisation
     * of its class. {@link #run()} has to be invoked beforehand!
     * 
     * @return all possible values the given variable may have after
     *         initialisation of its class. This is never {@code null}.
     * @throws IllegalStateException
     *             if {@code run} was not invoked before this method.
     */
    public Set<UnknownTypeValue> getPossibleInitialValues() {
        if (!isRun) {
            throw new IllegalStateException("Method 'run' has to be invoked before 'getPossibleInitialValues'!");
        }
        return Collections.unmodifiableSet(possibleInitialValues);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variable", variable).append("setters", setters);
        builder.append("possibleInitialValues", possibleInitialValues);
        return builder.toString();
    }

}
