package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.*;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
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
    static final class BaseInitialValue implements InitialValue {

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
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((baseValue == null) ? 0 : baseValue.hashCode());
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
            if (!(obj instanceof BaseInitialValue)) {
                return false;
            }
            final BaseInitialValue other = (BaseInitialValue) obj;
            if (baseValue == null) {
                if (other.baseValue != null) {
                    return false;
                }
            } else if (!baseValue.equals(other.baseValue)) {
                return false;
            }
            return true;
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
    private final Set<InitialValue> possibleInitialValues;

    private InitialValueFinder(final FieldNode theVariable, final Setters theSetters) {
        variable = theVariable;
        setters = theSetters;
        final byte supposedMaximumOfPossibleInitialValues = 5;
        possibleInitialValues = new HashSet<InitialValue>(supposedMaximumOfPossibleInitialValues);
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
            final EffectivePutfieldInsnFinder putfieldFinder = EffectivePutfieldInsnFinder.newInstance(
                    variable, constructor.instructions);
            final AssignmentInsn effectivePutfieldInstruction = putfieldFinder.getEffectivePutfieldInstruction();
            final int indexOfAssignmentInstruction = effectivePutfieldInstruction.getIndexOfAssignmentInstruction();
            addPossibleInitialValueFor(insns[indexOfAssignmentInstruction - 1]);
        }
    }

    private void addPossibleInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
        final InitialValueFactory factory = new InitialValueFactory();
        final InitialValue initialValue = factory.getConcreteInitialValueFor(variableValueSetupInsn);
        if (null != initialValue) {
            possibleInitialValues.add(initialValue);
        }
    }

    public Set<InitialValue> getPossibleInitialValues() {
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
