/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.12.2012
 */
public enum Opcode implements OpcodeInformation {
    NOP(0, Category.NOP),
    ACONST_NULL(1, Category.CONSTANTS),
    ICONST_M1(2, Category.CONSTANTS),
    ICONST_0(3, Category.CONSTANTS),
    ICONST_1(4, Category.CONSTANTS),
    ICONST_2(5, Category.CONSTANTS),
    ICONST_3(6, Category.CONSTANTS),
    ICONST_4(7, Category.CONSTANTS),
    ICONST_5(8, Category.CONSTANTS),
    LCONST_0(9, Category.CONSTANTS),
    LCONST_1(10, Category.CONSTANTS),
    FCONST_0(11, Category.CONSTANTS),
    FCONST_1(12, Category.CONSTANTS),
    FCONST_2(13, Category.CONSTANTS),
    DCONST_0(14, Category.CONSTANTS),
    DCONST_1(15, Category.CONSTANTS),
    BIPUSH(16, Category.CONSTANTS),
    SIPUSH(17, Category.CONSTANTS),
    LDC(18, Category.CONSTANTS),
    LDC_W(19, Category.CONSTANTS),
    LDC2_W(20, Category.CONSTANTS),
    ILOAD(21, Category.LOCAL_VARIABLES),
    LLOAD(22, Category.LOCAL_VARIABLES),
    FLOAD(23, Category.LOCAL_VARIABLES),
    DLOAD(24, Category.LOCAL_VARIABLES),
    ALOAD(25, Category.LOCAL_VARIABLES),
    ILOAD_0(26, Category.LOCAL_VARIABLES),
    ILOAD_1(27, Category.LOCAL_VARIABLES),
    ILOAD_2(28, Category.LOCAL_VARIABLES),
    ILOAD_3(29, Category.LOCAL_VARIABLES),
    LLOAD_0(30, Category.LOCAL_VARIABLES),
    LLOAD_1(31, Category.LOCAL_VARIABLES),
    LLOAD_2(32, Category.LOCAL_VARIABLES),
    LLOAD_3(33, Category.LOCAL_VARIABLES),
    FLOAD_0(34, Category.LOCAL_VARIABLES),
    FLOAD_1(35, Category.LOCAL_VARIABLES),
    FLOAD_2(36, Category.LOCAL_VARIABLES),
    FLOAD_3(37, Category.LOCAL_VARIABLES),
    DLOAD_0(38, Category.LOCAL_VARIABLES),
    DLOAD_1(39, Category.LOCAL_VARIABLES),
    DLOAD_2(40, Category.LOCAL_VARIABLES),
    DLOAD_3(41, Category.LOCAL_VARIABLES),
    ALOAD_0(42, Category.LOCAL_VARIABLES),
    ALOAD_1(43, Category.LOCAL_VARIABLES),
    ALOAD_2(44, Category.LOCAL_VARIABLES),
    ALOAD_3(45, Category.LOCAL_VARIABLES),
    IALOAD(46, Category.ARRAYS),
    LALOAD(47, Category.ARRAYS),
    FALOAD(48, Category.ARRAYS),
    DALOAD(49, Category.ARRAYS),
    AALOAD(50, Category.ARRAYS),
    BALOAD(51, Category.ARRAYS),
    CALOAD(52, Category.ARRAYS),
    SALOAD(53, Category.ARRAYS),
    ISTORE(54, Category.LOCAL_VARIABLES),
    LSTORE(55, Category.LOCAL_VARIABLES),
    FSTORE(56, Category.LOCAL_VARIABLES),
    DSTORE(57, Category.LOCAL_VARIABLES),
    ASTORE(58, Category.LOCAL_VARIABLES),
    ISTORE_0(59, Category.LOCAL_VARIABLES),
    ISTORE_1(60, Category.LOCAL_VARIABLES),
    ISTORE_2(61, Category.LOCAL_VARIABLES),
    ISTORE_3(62, Category.LOCAL_VARIABLES),
    LSTORE_0(63, Category.LOCAL_VARIABLES),
    LSTORE_1(64, Category.LOCAL_VARIABLES),
    LSTORE_2(65, Category.LOCAL_VARIABLES),
    LSTORE_3(66, Category.LOCAL_VARIABLES),
    FSTORE_0(67, Category.LOCAL_VARIABLES),
    FSTORE_1(68, Category.LOCAL_VARIABLES),
    FSTORE_2(69, Category.LOCAL_VARIABLES),
    FSTORE_3(70, Category.LOCAL_VARIABLES),
    DSTORE_0(71, Category.LOCAL_VARIABLES),
    DSTORE_1(72, Category.LOCAL_VARIABLES),
    DSTORE_2(73, Category.LOCAL_VARIABLES),
    DSTORE_3(74, Category.LOCAL_VARIABLES),
    ASTORE_0(75, Category.LOCAL_VARIABLES),
    ASTORE_1(76, Category.LOCAL_VARIABLES),
    ASTORE_2(77, Category.LOCAL_VARIABLES),
    ASTORE_3(78, Category.LOCAL_VARIABLES),
    IASTORE(79, Category.ARRAYS),
    LASTORE(80, Category.ARRAYS),
    FASTORE(81, Category.ARRAYS),
    DASTORE(82, Category.ARRAYS),
    AASTORE(83, Category.ARRAYS),
    BASTORE(84, Category.ARRAYS),
    CASTORE(85, Category.ARRAYS),
    SASTORE(86, Category.ARRAYS),
    POP(87, Category.STACK),
    POP2(88, Category.STACK),
    DUP(89, Category.STACK),
    DUP_X1(90, Category.STACK),
    DUP_X2(91, Category.STACK),
    DUP2(92, Category.STACK),
    DUP2_X1(93, Category.STACK),
    DUP2_X2(94, Category.STACK),
    SWAP(95, Category.STACK),
    IADD(96, Category.ARITHMETIC_AND_LOGIC),
    LADD(97, Category.ARITHMETIC_AND_LOGIC),
    FADD(98, Category.ARITHMETIC_AND_LOGIC),
    DADD(99, Category.ARITHMETIC_AND_LOGIC),
    ISUB(100, Category.ARITHMETIC_AND_LOGIC),
    LSUB(101, Category.ARITHMETIC_AND_LOGIC),
    FSUB(102, Category.ARITHMETIC_AND_LOGIC),
    DSUB(103, Category.ARITHMETIC_AND_LOGIC),
    IMUL(104, Category.ARITHMETIC_AND_LOGIC),
    LMUL(105, Category.ARITHMETIC_AND_LOGIC),
    FMUL(106, Category.ARITHMETIC_AND_LOGIC),
    DMUL(107, Category.ARITHMETIC_AND_LOGIC),
    IDIV(108, Category.ARITHMETIC_AND_LOGIC),
    LDIV(109, Category.ARITHMETIC_AND_LOGIC),
    FDIV(110, Category.ARITHMETIC_AND_LOGIC),
    DDIV(111, Category.ARITHMETIC_AND_LOGIC),
    IREM(112, Category.ARITHMETIC_AND_LOGIC),
    LREM(113, Category.ARITHMETIC_AND_LOGIC),
    FREM(114, Category.ARITHMETIC_AND_LOGIC),
    DREM(115, Category.ARITHMETIC_AND_LOGIC),
    INEG(116, Category.ARITHMETIC_AND_LOGIC),
    LNEG(117, Category.ARITHMETIC_AND_LOGIC),
    FNEG(118, Category.ARITHMETIC_AND_LOGIC),
    DNEG(119, Category.ARITHMETIC_AND_LOGIC),
    ISHL(120, Category.ARITHMETIC_AND_LOGIC),
    LSHL(121, Category.ARITHMETIC_AND_LOGIC),
    ISHR(122, Category.ARITHMETIC_AND_LOGIC),
    LSHR(123, Category.ARITHMETIC_AND_LOGIC),
    IUSHR(124, Category.ARITHMETIC_AND_LOGIC),
    LUSHR(125, Category.ARITHMETIC_AND_LOGIC),
    IAND(126, Category.ARITHMETIC_AND_LOGIC),
    LAND(127, Category.ARITHMETIC_AND_LOGIC),
    IOR(128, Category.ARITHMETIC_AND_LOGIC),
    LOR(129, Category.ARITHMETIC_AND_LOGIC),
    IXOR(130, Category.ARITHMETIC_AND_LOGIC),
    LXOR(131, Category.ARITHMETIC_AND_LOGIC),
    IINC(132, Category.LOCAL_VARIABLES),
    I2L(133, Category.CASTS),
    I2F(134, Category.CASTS),
    I2D(135, Category.CASTS),
    L2I(136, Category.CASTS),
    L2F(137, Category.CASTS),
    L2D(138, Category.CASTS),
    F2I(139, Category.CASTS),
    F2L(140, Category.CASTS),
    F2D(141, Category.CASTS),
    D2I(142, Category.CASTS),
    D2L(143, Category.CASTS),
    D2F(144, Category.CASTS),
    I2B(145, Category.CASTS),
    I2C(146, Category.CASTS),
    I2S(147, Category.CASTS),
    LCMP(148, Category.ARITHMETIC_AND_LOGIC),
    FCMPL(149, Category.ARITHMETIC_AND_LOGIC),
    FCMPG(150, Category.ARITHMETIC_AND_LOGIC),
    DCMPL(151, Category.ARITHMETIC_AND_LOGIC),
    DCMPG(152, Category.ARITHMETIC_AND_LOGIC),
    IFEQ(153, Category.JUMPS),
    IFNE(154, Category.JUMPS),
    IFLT(155, Category.JUMPS),
    IFGE(156, Category.JUMPS),
    IFGT(157, Category.JUMPS),
    IFLE(158, Category.JUMPS),
    IF_ICMPEQ(159, Category.JUMPS),
    IF_ICMPNE(160, Category.JUMPS),
    IF_ICMPLT(161, Category.JUMPS),
    IF_ICMPGE(162, Category.JUMPS),
    IF_ICMPGT(163, Category.JUMPS),
    IF_ICMPLE(164, Category.JUMPS),
    IF_ACMPEQ(165, Category.JUMPS),
    IF_ACMPNE(166, Category.JUMPS),
    GOTO(167, Category.JUMPS),
    JSR(168, Category.JUMPS),
    RET(169, Category.JUMPS),
    TABLESWITCH(170, Category.JUMPS),
    LOOKUPSWITCH(171, Category.JUMPS),
    IRETURN(172, Category.RETURN),
    LRETURN(173, Category.RETURN),
    FRETURN(174, Category.RETURN),
    DRETURN(175, Category.RETURN),
    ARETURN(176, Category.RETURN),
    RETURN(177, Category.RETURN),
    GETSTATIC(178, Category.FIELDS),
    PUTSTATIC(179, Category.FIELDS),
    GETFIELD(180, Category.FIELDS),
    PUTFIELD(181, Category.FIELDS),
    INVOKEVIRTUAL(182, Category.METHODS),
    INVOKESPECIAL(183, Category.METHODS),
    INVOKESTATIC(184, Category.METHODS),
    INVOKEINTERFACE(185, Category.METHODS),
    INVOKEDYNAMIC(186, Category.METHODS),
    NEW(187, Category.OBJECTS),
    NEWARRAY(188, Category.ARRAYS),
    ANEWARRAY(189, Category.ARRAYS),
    ARRAYLENGTH(190, Category.ARRAYS),
    ATHROW(191, Category.RETURN),
    CHECKCAST(192, Category.CASTS),
    INSTANCEOF(193, Category.OBJECTS),
    MONITORENTER(194, Category.OBJECTS),
    MONITOREXIT(195, Category.OBJECTS),
    WIDE(196, Category.LOCAL_VARIABLES),
    MULTIANEWARRAY(197, Category.ARRAYS),
    IFNULL(198, Category.JUMPS),
    IFNONNULL(199, Category.JUMPS),
    GOTO_W(200, Category.JUMPS),
    JSR_W(201, Category.JUMPS);

    private static final Comparator<Opcode> opcodeComparator = new Comparator<Opcode>() {
        @Override
        public int compare(final Opcode o1, final Opcode o2) {
            return Integer.valueOf(o1.ordinal()).compareTo(Integer.valueOf(o2.ordinal()));
        }
    };

    public static enum Category {
        LOCAL_VARIABLES,
        STACK,
        CONSTANTS,
        ARITHMETIC_AND_LOGIC,
        CASTS,
        OBJECTS,
        FIELDS,
        METHODS,
        ARRAYS,
        JUMPS,
        RETURN,
        NOP;
    }

    private final int opcodeAsInt;
    private final String opcodeAsHex;
    private final Category category;

    private Opcode(final int opcodeAsInt, final Category category) {
        this.category = category;
        this.opcodeAsInt = opcodeAsInt;
        opcodeAsHex = Integer.toHexString(opcodeAsInt);
    }

    @Override
    public int asInt() {
        return opcodeAsInt;
    }

    @Override
    public String asHex() {
        return opcodeAsHex;
    }

    @Override
    public Category category() {
        return category;
    }

    public static SortedSet<Opcode> localVariables() {
        return getAllOpcodesFor(Category.LOCAL_VARIABLES);
    }
    
    private static SortedSet<Opcode> getAllOpcodesFor(final Category category) {
        final ImmutableSortedSet.Builder<Opcode> builder = new ImmutableSortedSet.Builder<Opcode>(opcodeComparator);
        for (final Opcode opcode : Opcode.values()) {
            if (isSameCategory(category, opcode)) {
                builder.add(opcode);
            }
        }
        return builder.build();
    }
    
    private static boolean isSameCategory(final Category expectedCategory, final Opcode opcode) {
        return expectedCategory == opcode.category;
    }

    public List<Opcode> allOfCategory(final Category category) {
        notNull(category);
        final byte maxSize = 40;
        final List<Opcode> result = new ArrayList<Opcode>(maxSize);
        for (final Opcode opcode : Opcode.values()) {
            if (category == opcode.category) {
                result.add(opcode);
            }
        }
        return result;
    }

    /**
     * These instructions are used to manipulate values on the stack:
     * {@code POP} pops the value on top of the stack, {@code DUP} pushes a copy
     * of the top stack value, {@code SWAP} pops two values and pushes them in
     * the reverse order, etc.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    public static SortedSet<Opcode> stack() {
        return getAllOpcodesFor(Category.STACK);
    }

    /**
     * These instructions push a constant value on the operand stack:
     * {@code ACONST_NULL} pushes {@code null}, {@code ICONST_0} pushes the
     * {@code int} value {@code 0}, {@code FCONST_0} pushes {@code 0f},
     * {@code DCONST_0} pushes {@code 0d}, {@code BIPUSH b} pushes the
     * {@code byte} value {@code b}, {@code SIPUSH s} pushes the {@code short}
     * value {@code s}, {@code LDC cst} pushes the arbitrary {@code int},
     * {@code float}, {@code long}, {@code double}, {@code String}, or class1
     * constant cst, etc.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    public static SortedSet<Opcode> constants() {
        return getAllOpcodesFor(Category.CONSTANTS);
    }

    /**
     * These instructions pop numeric values from the operand stack combine them
     * and push the result on the stack. They do not have any argument.
     * {@code xADD}, {@code xSUB}, {@code xMUL}, {@code xDIV} and {@code xREM}
     * correspond to the {@code +, -, *, /} and {@code %} operations, where
     * {@code x} is either {@code I, L, F} or {@code D}. Similarly there are
     * other instructions corresponding to {@code <<}, {@code >>}, {@code >>>},
     * {@code |}, {@code &} and {@code ^}, for {@code int} and {@code long}
     * values.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    public static SortedSet<Opcode> arithmeticAndLogic() {
        return getAllOpcodesFor(Category.ARITHMETIC_AND_LOGIC);
    }

    /**
     * These instructions pop a value from the stack, convert it to another
     * type, and push the result back. They correspond to cast expressions in
     * Java. {@code I2F}, {@code F2D}, {@code L2D}, etc. convert numeric values
     * from one numeric type to another. {@code CHECKCAST} <em>t</em> converts a
     * reference value to the type <em>t</em>.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    public static SortedSet<Opcode> casts() {
        return getAllOpcodesFor(Category.CASTS);
    }

    public static SortedSet<Opcode> objects() {
        return getAllOpcodesFor(Category.OBJECTS);
    }

    public static SortedSet<Opcode> fields() {
        return getAllOpcodesFor(Category.FIELDS);
    }

    public static SortedSet<Opcode> methods() {
        return getAllOpcodesFor(Category.METHODS);
    }

    public static SortedSet<Opcode> arrays() {
        return getAllOpcodesFor(Category.ARRAYS);
    }

    public static SortedSet<Opcode> jumps() {
        return getAllOpcodesFor(Category.JUMPS);
    }

    public static SortedSet<Opcode> returns() {
        return getAllOpcodesFor(Category.RETURN);
    }

    public static SortedSet<Opcode> nop() {
        return getAllOpcodesFor(Category.NOP);
    }

}
