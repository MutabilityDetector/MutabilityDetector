package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.VariableInitialisersAssociation.Initialisers;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 14.02.2013
 */
@NotThreadSafe
final class InitialValueFinder implements Finder<Set<UnknownTypeValue>> {

    @Immutable
    private static final class InitialValueFactory {

        private final FieldNode variable;

        public InitialValueFactory(final FieldNode theVariable) {
            variable = theVariable;
        }

        public UnknownTypeValue getConcreteInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
            final UnknownTypeValue result;
            if (isLdcInsn(variableValueSetupInsn)) {
                result = getInitialValueOfLdcInsn(variableValueSetupInsn);
            } else if (isIntInsn(variableValueSetupInsn)) {
                result = getInitialValueOfIntInsn(variableValueSetupInsn);
            } else if (isStackConstantPushInsn(variableValueSetupInsn)) {
                result = getInitialValueOfStackConstantInsn(variableValueSetupInsn);
            } else {
                result = getInitialValueOfUnknownTypeOfVariable();
            }
            return result;
        }

        private static boolean isLdcInsn(final AbstractInsnNode abstractInsnNode) {
            return AbstractInsnNode.LDC_INSN == abstractInsnNode.getType();
        }

        private static UnknownTypeValue getInitialValueOfLdcInsn(final AbstractInsnNode setupInsn) {
            final LdcInsnNode ldcInsn = (LdcInsnNode) setupInsn;
            final Object cst = ldcInsn.cst;
            return DefaultUnknownTypeValue.getInstance(cst);
        }

        private static boolean isIntInsn(final AbstractInsnNode abstractInsnNode) {
            return AbstractInsnNode.INT_INSN == abstractInsnNode.getType();
        }

        private static UnknownTypeValue getInitialValueOfIntInsn(final AbstractInsnNode setupInsn) {
            final IntInsnNode singleIntOperandInsn = (IntInsnNode) setupInsn;
            final int operand = singleIntOperandInsn.operand;
            return DefaultUnknownTypeValue.getInstance(Integer.valueOf(operand));
        }

        private static boolean isStackConstantPushInsn(final AbstractInsnNode setupInsn) {
            final SortedSet<Opcode> constantsInstructions = Opcode.constants();
            final Opcode opcode = Opcode.forInt(setupInsn.getOpcode());
            return constantsInstructions.contains(opcode);
        }

        private static UnknownTypeValue getInitialValueOfStackConstantInsn(final AbstractInsnNode setupInsn) {
            final Opcode opcode = Opcode.forInt(setupInsn.getOpcode());
            return opcode.stackValue();
        }

        private UnknownTypeValue getInitialValueOfUnknownTypeOfVariable() {
            final Type variableType = Type.getType(variable.desc);
            final int typeSort = variableType.getSort();
            final UnknownTypeValue result;
            if (Type.OBJECT == typeSort || Type.ARRAY == typeSort || Type.METHOD == typeSort) {
                result = DefaultUnknownTypeValue.getInstanceForUnknownReference();
            } else {
                result = DefaultUnknownTypeValue.getInstanceForUnknownPrimitive();
            }
            return result;
        }

        public UnknownTypeValue getJvmDefaultInitialValueFor(final Type type) {
            final UnknownTypeValue result;
            final int sort = type.getSort();
            if (Type.BOOLEAN == sort) {
                result = DefaultUnknownTypeValue.getInstance(Boolean.FALSE);
            } else if (Type.BYTE == sort) {
                result = DefaultUnknownTypeValue.getInstance(Byte.valueOf((byte) 0));
            } else if (Type.CHAR == sort) {
                result = DefaultUnknownTypeValue.getInstance(Character.valueOf((char) 0));
            } else if (Type.SHORT == sort) {
                result = DefaultUnknownTypeValue.getInstance(Short.valueOf((short) 0));
            } else if (Type.INT == sort) {
                result = DefaultUnknownTypeValue.getInstance(Integer.valueOf(0));
            } else if (Type.LONG == sort) {
                result = DefaultUnknownTypeValue.getInstance(Long.valueOf(0L));
            } else if (Type.FLOAT == sort) {
                result = DefaultUnknownTypeValue.getInstance(Float.valueOf(0.0F));
            } else if (Type.DOUBLE == sort) {
                result = DefaultUnknownTypeValue.getInstance(Double.valueOf(0.0D));
            } else {
                result = DefaultUnknownTypeValue.getInstanceForNull();
            }
            return result;
        }

    } // class JvmInitialValueFactory


    private final FieldNode variable;
    private final Initialisers setters;
    private final EnhancedClassNode enhancedClassNode;
    private final Set<UnknownTypeValue> possibleInitialValues;
    private volatile boolean arePossibleInitialValuesAlreadyFound;

    private InitialValueFinder(final FieldNode theVariable, final Initialisers theSetters,
            final EnhancedClassNode theEnhancedClassNode) {
        variable = theVariable;
        setters = theSetters;
        enhancedClassNode = theEnhancedClassNode;
        final byte supposedMaximumOfPossibleInitialValues = 5;
        possibleInitialValues = new HashSet<UnknownTypeValue>(supposedMaximumOfPossibleInitialValues);
        arePossibleInitialValuesAlreadyFound = false;
    }

    /**
     * Factory method for this class. None of the parameters must be
     * {@code null}.
     * 
     * @param variable
     *            the variable to find the initial value for.
     * @param initialisers
     *            the initialisers for {@code variable}.
     * @return a new instance of this class.
     */
    public static InitialValueFinder newInstance(final FieldNode variable, final Initialisers initialisers,
            final EnhancedClassNode enhancedClassNode) {
        return new InitialValueFinder(notNull(variable), notNull(initialisers), notNull(enhancedClassNode));
    }

    /**
     * Gets all possible values the given variable may have after initialisation
     * of its class. {@link #run()} has to be invoked beforehand!
     * 
     * @return all possible values the given variable may have after
     *         initialisation of its class. This is never {@code null} .
     * @throws IllegalStateException
     *             if {@code run} was not invoked before this method.
     */
    @Override
    public Set<UnknownTypeValue> find() {
        if (!arePossibleInitialValuesAlreadyFound) {
            findPossibleInitialValues();
            arePossibleInitialValuesAlreadyFound = true;
        }
        return Collections.unmodifiableSet(possibleInitialValues);
    }

    private void findPossibleInitialValues() {
        if (hasNoConstructors()) {
            addJvmInitialValueForVariable();
        } else {
            addConcreteInitialValuesByConstructor();
        }
    }

    private boolean hasNoConstructors() {
        final List<MethodNode> constructors = setters.getConstructors();
        return constructors.isEmpty();
    }

    private void addJvmInitialValueForVariable() {
        final InitialValueFactory factory = new InitialValueFactory(variable);
        final Type type = Type.getType(variable.desc);
        possibleInitialValues.add(factory.getJvmDefaultInitialValueFor(type));
    }

    private void addConcreteInitialValuesByConstructor() {
        for (final MethodNode constructor : setters.getConstructors()) {
            final AbstractInsnNode valueSetUpInsn = findValueSetUpInsnIn(constructor);
            addSupposedInitialValueFor(valueSetUpInsn);
        }
    }

    private AbstractInsnNode findValueSetUpInsnIn(final MethodNode constructor) {
        final List<ControlFlowBlock> blocks = enhancedClassNode.getControlFlowBlocksForMethod(constructor);
        final Finder<AssignmentInsn> f = EffectiveAssignmentInsnFinder.newInstance(variable, blocks);
        final AssignmentInsn effectiveAssignmentInsn = f.find();
        final int indexOfAssignmentInstruction = effectiveAssignmentInsn.getIndexWithinMethod();
        final InsnList instructions = constructor.instructions;
        final AbstractInsnNode result = instructions.get(indexOfAssignmentInstruction - 1);
        return result;
    }

    private void addSupposedInitialValueFor(final AbstractInsnNode variableValueSetupInsn) {
        final InitialValueFactory factory = new InitialValueFactory(variable);
        final UnknownTypeValue initialValue = factory.getConcreteInitialValueFor(variableValueSetupInsn);
        possibleInitialValues.add(initialValue);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("variable", variable).append("setters", setters);
        builder.append("possibleInitialValues", possibleInitialValues);
        return builder.toString();
    }

}
