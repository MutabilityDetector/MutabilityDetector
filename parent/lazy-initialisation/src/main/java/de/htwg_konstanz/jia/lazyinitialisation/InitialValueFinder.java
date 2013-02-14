package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
@NotThreadSafe
final class InitialValueFinder implements Runnable {

    public interface InitialValue {
        boolean asBoolean();

        byte asByte();

        char asChar();

        short asShort();

        int asInt();

        long asLong();

        float asFloat();

        double asDouble();

        String asString();

        Object asObject();

        <T> T asConcreteObject(Class<T> targetTypeClass);
    } // interface InitialValue


    @NotThreadSafe
    private static final class BaseInitialValue implements InitialValue {

        private final Object baseValue;

        private BaseInitialValue(final Object theBaseValue) {
            baseValue = theBaseValue;
        }

        public static InitialValue getInstance(final Object baseValue) {
            return new BaseInitialValue(baseValue);
        }

        @Override
        public boolean asBoolean() {
            final Boolean result = tryToCast(baseValue, Boolean.class);
            return result.booleanValue();
        }

        private static <T> T tryToCast(final Object value, final Class<T> targetClass) {
            try {
                return cast(value, targetClass);
            } catch (final ClassCastException e) {
                final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
                final Logger logger = LoggerFactory.getLogger(BaseInitialValue.class);
                logger.error(msg, e);
            }
            return null;
        }

        private static <T> T cast(final Object value, final Class<T> targetClass) {
            final T result = targetClass.cast(value);
            if (null == result) {
                final String msg = String.format("Unable to cast '%s' to target class '%s'.", value, targetClass);
                throw new NullPointerException(msg);
            }
            return result;
        }

        @Override
        public byte asByte() {
            return tryToCastToNumber().byteValue();
        }

        private Number tryToCastToNumber() {
            return tryToCast(baseValue, Number.class);
        }

        @Override
        public char asChar() {
            final Character result = tryToCast(baseValue, Character.class);
            return result.charValue();
        }

        @Override
        public short asShort() {
            return tryToCastToNumber().shortValue();
        }

        @Override
        public int asInt() {
            return tryToCastToNumber().intValue();
        }

        @Override
        public long asLong() {
            return tryToCastToNumber().longValue();
        }

        @Override
        public float asFloat() {
            return tryToCastToNumber().floatValue();
        }

        @Override
        public double asDouble() {
            return tryToCastToNumber().doubleValue();
        }

        @Override
        public String asString() {
            final String result = tryToCast(baseValue, String.class);
            return result;
        }

        @Override
        public Object asObject() {
            return baseValue;
        }

        @Override
        public <T> T asConcreteObject(final Class<T> targetTypeClass) {
            final T result = tryToCast(baseValue, targetTypeClass);
            return result;
        }

        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append(baseValue);
            return builder.toString();
        }
    } // class BaseInitialValue

    
    @Immutable
    private static final class InitialValueFactory {
        public InitialValue getConcreteInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
            InitialValue result = null;
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

        private static InitialValue getInitialValueOfLdcInsn(final AbstractInsnNode variableValueSetupInsn) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) variableValueSetupInsn;
            final Object cst = ldcInsn.cst;
            return BaseInitialValue.getInstance(cst);
        }

        private static boolean isIntInsn(final AbstractInsnNode abstractInsnNode) {
            return AbstractInsnNode.INT_INSN == abstractInsnNode.getType();
        }

        private static InitialValue getInitialValueOfIntInsn(final AbstractInsnNode variableValueSetupInsn) {
            final IntInsnNode singleIntOperandInsn = (IntInsnNode) variableValueSetupInsn;
            final int operand = singleIntOperandInsn.operand;
            return BaseInitialValue.getInstance(Integer.valueOf(operand));
        }

        private static boolean isStackConstantPushInsn(final AbstractInsnNode abstractInsnNode) {
            final SortedSet<Opcode> constantsInstructions = Opcode.constants();
            final Opcode opcode = Opcode.forInt(abstractInsnNode.getOpcode());
            return constantsInstructions.contains(opcode);
        }

        private static InitialValue getInitialValueOfStackConstantInsn(final AbstractInsnNode variableValueSetupInsn) {
            final Opcode opcode = Opcode.forInt(variableValueSetupInsn.getOpcode());
            return BaseInitialValue.getInstance(opcode.stackValue());
        }

        public InitialValue getJvmDefaultInitialValueFor(final Type type) {
            final InitialValue result;
            final int sort = type.getSort();
            if (Type.BOOLEAN == sort) {
                result = BaseInitialValue.getInstance(Boolean.FALSE);
            } else if (Type.BYTE == sort) {
                result = BaseInitialValue.getInstance(Byte.valueOf((byte) 0));
            } else if (Type.CHAR == sort) {
                result = BaseInitialValue.getInstance(Character.valueOf((char) 0));
            } else if (Type.SHORT == sort) {
                result = BaseInitialValue.getInstance(Short.valueOf((short) 0));
            } else if (Type.INT == sort) {
                result = BaseInitialValue.getInstance(Integer.valueOf(0));
            } else if (Type.LONG == sort) {
                result = BaseInitialValue.getInstance(Long.valueOf(0L));
            } else if (Type.FLOAT == sort) {
                result = BaseInitialValue.getInstance(Float.valueOf(0.0F));
            } else if (Type.DOUBLE == sort) {
                result = BaseInitialValue.getInstance(Double.valueOf(0.0D));
            } else {
                result = BaseInitialValue.getInstance(null);
            }
            return result;
        }
    } // class JvmInitialValueFactory


    private final FieldNode variable;
    private final Setters setters;
    private final List<InitialValue> possibleInitialValues;

    private InitialValueFinder(final FieldNode theVariable, final Setters theSetters) {
        variable = theVariable;
        setters = theSetters;
        final byte supposedMaximumOfPossibleInitialValues = 5;
        possibleInitialValues = new ArrayList<InitialValue>(supposedMaximumOfPossibleInitialValues);
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
            final Map<Integer, FieldInsnNode> putfieldInsns = findPutfieldInstructionsForVariable(insns);
            final int effectivePutfieldInstruction = getNumberOfEffectivePutfieldInstruction(putfieldInsns);
            addPossibleInitialValueFor(insns[effectivePutfieldInstruction - 1]);
        }
    }

    private Map<Integer, FieldInsnNode> findPutfieldInstructionsForVariable(final AbstractInsnNode[] instructions) {
        final Map<Integer, FieldInsnNode> result = new HashMap<Integer, FieldInsnNode>(instructions.length);
        for (int i = 0; i < instructions.length; i++) {
            final AbstractInsnNode abstractInsnNode = instructions[i];
            addIfPutfieldInstructionForVariable(abstractInsnNode, i, result);
        }
        return result;
    }

    private void addIfPutfieldInstructionForVariable(final AbstractInsnNode abstractInsnNode,
            final int numberOfInsn,
            final Map<Integer, FieldInsnNode> putfieldInsnsForVariable) {
        if (Opcodes.PUTFIELD == abstractInsnNode.getOpcode()) {
            final FieldInsnNode putfieldInstruction = (FieldInsnNode) abstractInsnNode;
            if (putfieldInstruction.name.equals(variable.name)) {
                putfieldInsnsForVariable.put(Integer.valueOf(numberOfInsn), putfieldInstruction);
            }
        }
    }

    /*
     * The effective putfield instruction is the last one in the
     * sequence of instructions which puts a value to the target
     * variable. Thus the highest instruction number indicates the
     * position of the effective putfield instruction.
     */
    private int getNumberOfEffectivePutfieldInstruction(final Map<Integer, FieldInsnNode> putfieldInstructions) {
        int maxInstructionNumber = -1;
        final Set<Integer> instructionNumbers = putfieldInstructions.keySet();
        for (final Integer currentInstructionNumber : instructionNumbers) {
            maxInstructionNumber = Math.max(maxInstructionNumber, currentInstructionNumber);
        }
        return maxInstructionNumber;
    }

    private void addPossibleInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
        final InitialValueFactory factory = new InitialValueFactory();
        final InitialValue initialValue = factory.getConcreteInitialValueFor(variableValueSetupInsn);
        if (null != initialValue) {
            possibleInitialValues.add(initialValue);
        }
    }

    public List<InitialValue> getPossibleInitialValues() {
        return Collections.unmodifiableList(possibleInitialValues);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variable", variable).append("setters", setters);
        builder.append("possibleInitialValues", possibleInitialValues);
        return builder.toString();
    }

}
