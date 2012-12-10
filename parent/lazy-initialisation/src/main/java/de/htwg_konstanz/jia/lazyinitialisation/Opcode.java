/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static de.htwg_konstanz.jia.lazyinitialisation.Category.*;

import java.util.Comparator;
import java.util.SortedSet;

import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.12.2012
 */
public enum Opcode implements OpcodeInformation {
    NOP(0, Category.NOP),
    ACONST_NULL(1, CONSTANTS),
    ICONST_M1(2, CONSTANTS),
    ICONST_0(3, CONSTANTS),
    ICONST_1(4, CONSTANTS),
    ICONST_2(5, CONSTANTS),
    ICONST_3(6, CONSTANTS),
    ICONST_4(7, CONSTANTS),
    ICONST_5(8, CONSTANTS),
    LCONST_0(9, CONSTANTS),
    LCONST_1(10, CONSTANTS),
    FCONST_0(11, CONSTANTS),
    FCONST_1(12, CONSTANTS),
    FCONST_2(13, CONSTANTS),
    DCONST_0(14, CONSTANTS),
    DCONST_1(15, CONSTANTS),
    BIPUSH(16, CONSTANTS),
    SIPUSH(17, CONSTANTS),
    LDC(18, CONSTANTS),
    LDC_W(19, CONSTANTS),
    LDC2_W(20, CONSTANTS),
    ILOAD(21, LOCAL_VARIABLES),
    LLOAD(22, LOCAL_VARIABLES),
    FLOAD(23, LOCAL_VARIABLES),
    DLOAD(24, LOCAL_VARIABLES),
    ALOAD(25, LOCAL_VARIABLES),
    ILOAD_0(26, LOCAL_VARIABLES),
    ILOAD_1(27, LOCAL_VARIABLES),
    ILOAD_2(28, LOCAL_VARIABLES),
    ILOAD_3(29, LOCAL_VARIABLES),
    LLOAD_0(30, LOCAL_VARIABLES),
    LLOAD_1(31, LOCAL_VARIABLES),
    LLOAD_2(32, LOCAL_VARIABLES),
    LLOAD_3(33, LOCAL_VARIABLES),
    FLOAD_0(34, LOCAL_VARIABLES),
    FLOAD_1(35, LOCAL_VARIABLES),
    FLOAD_2(36, LOCAL_VARIABLES),
    FLOAD_3(37, LOCAL_VARIABLES),
    DLOAD_0(38, LOCAL_VARIABLES),
    DLOAD_1(39, LOCAL_VARIABLES),
    DLOAD_2(40, LOCAL_VARIABLES),
    DLOAD_3(41, LOCAL_VARIABLES),
    ALOAD_0(42, LOCAL_VARIABLES),
    ALOAD_1(43, LOCAL_VARIABLES),
    ALOAD_2(44, LOCAL_VARIABLES),
    ALOAD_3(45, LOCAL_VARIABLES),
    IALOAD(46, ARRAYS),
    LALOAD(47, ARRAYS),
    FALOAD(48, ARRAYS),
    DALOAD(49, ARRAYS),
    AALOAD(50, ARRAYS),
    BALOAD(51, ARRAYS),
    CALOAD(52, ARRAYS),
    SALOAD(53, ARRAYS),
    ISTORE(54, LOCAL_VARIABLES),
    LSTORE(55, LOCAL_VARIABLES),
    FSTORE(56, LOCAL_VARIABLES),
    DSTORE(57, LOCAL_VARIABLES),
    ASTORE(58, LOCAL_VARIABLES),
    ISTORE_0(59, LOCAL_VARIABLES),
    ISTORE_1(60, LOCAL_VARIABLES),
    ISTORE_2(61, LOCAL_VARIABLES),
    ISTORE_3(62, LOCAL_VARIABLES),
    LSTORE_0(63, LOCAL_VARIABLES),
    LSTORE_1(64, LOCAL_VARIABLES),
    LSTORE_2(65, LOCAL_VARIABLES),
    LSTORE_3(66, LOCAL_VARIABLES),
    FSTORE_0(67, LOCAL_VARIABLES),
    FSTORE_1(68, LOCAL_VARIABLES),
    FSTORE_2(69, LOCAL_VARIABLES),
    FSTORE_3(70, LOCAL_VARIABLES),
    DSTORE_0(71, LOCAL_VARIABLES),
    DSTORE_1(72, LOCAL_VARIABLES),
    DSTORE_2(73, LOCAL_VARIABLES),
    DSTORE_3(74, LOCAL_VARIABLES),
    ASTORE_0(75, LOCAL_VARIABLES),
    ASTORE_1(76, LOCAL_VARIABLES),
    ASTORE_2(77, LOCAL_VARIABLES),
    ASTORE_3(78, LOCAL_VARIABLES),
    IASTORE(79, ARRAYS),
    LASTORE(80, ARRAYS),
    FASTORE(81, ARRAYS),
    DASTORE(82, ARRAYS),
    AASTORE(83, ARRAYS),
    BASTORE(84, ARRAYS),
    CASTORE(85, ARRAYS),
    SASTORE(86, ARRAYS),
    POP(87, STACK),
    POP2(88, STACK),
    DUP(89, STACK),
    DUP_X1(90, STACK),
    DUP_X2(91, STACK),
    DUP2(92, STACK),
    DUP2_X1(93, STACK),
    DUP2_X2(94, STACK),
    SWAP(95, STACK),
    IADD(96, ARITHMETIC_AND_LOGIC),
    LADD(97, ARITHMETIC_AND_LOGIC),
    FADD(98, ARITHMETIC_AND_LOGIC),
    DADD(99, ARITHMETIC_AND_LOGIC),
    ISUB(100, ARITHMETIC_AND_LOGIC),
    LSUB(101, ARITHMETIC_AND_LOGIC),
    FSUB(102, ARITHMETIC_AND_LOGIC),
    DSUB(103, ARITHMETIC_AND_LOGIC),
    IMUL(104, ARITHMETIC_AND_LOGIC),
    LMUL(105, ARITHMETIC_AND_LOGIC),
    FMUL(106, ARITHMETIC_AND_LOGIC),
    DMUL(107, ARITHMETIC_AND_LOGIC),
    IDIV(108, ARITHMETIC_AND_LOGIC),
    LDIV(109, ARITHMETIC_AND_LOGIC),
    FDIV(110, ARITHMETIC_AND_LOGIC),
    DDIV(111, ARITHMETIC_AND_LOGIC),
    IREM(112, ARITHMETIC_AND_LOGIC),
    LREM(113, ARITHMETIC_AND_LOGIC),
    FREM(114, ARITHMETIC_AND_LOGIC),
    DREM(115, ARITHMETIC_AND_LOGIC),
    INEG(116, ARITHMETIC_AND_LOGIC),
    LNEG(117, ARITHMETIC_AND_LOGIC),
    FNEG(118, ARITHMETIC_AND_LOGIC),
    DNEG(119, ARITHMETIC_AND_LOGIC),
    ISHL(120, ARITHMETIC_AND_LOGIC),
    LSHL(121, ARITHMETIC_AND_LOGIC),
    ISHR(122, ARITHMETIC_AND_LOGIC),
    LSHR(123, ARITHMETIC_AND_LOGIC),
    IUSHR(124, ARITHMETIC_AND_LOGIC),
    LUSHR(125, ARITHMETIC_AND_LOGIC),
    IAND(126, ARITHMETIC_AND_LOGIC),
    LAND(127, ARITHMETIC_AND_LOGIC),
    IOR(128, ARITHMETIC_AND_LOGIC),
    LOR(129, ARITHMETIC_AND_LOGIC),
    IXOR(130, ARITHMETIC_AND_LOGIC),
    LXOR(131, ARITHMETIC_AND_LOGIC),
    IINC(132, LOCAL_VARIABLES),
    I2L(133, CASTS),
    I2F(134, CASTS),
    I2D(135, CASTS),
    L2I(136, CASTS),
    L2F(137, CASTS),
    L2D(138, CASTS),
    F2I(139, CASTS),
    F2L(140, CASTS),
    F2D(141, CASTS),
    D2I(142, CASTS),
    D2L(143, CASTS),
    D2F(144, CASTS),
    I2B(145, CASTS),
    I2C(146, CASTS),
    I2S(147, CASTS),
    LCMP(148, ARITHMETIC_AND_LOGIC),
    FCMPL(149, ARITHMETIC_AND_LOGIC),
    FCMPG(150, ARITHMETIC_AND_LOGIC),
    DCMPL(151, ARITHMETIC_AND_LOGIC),
    DCMPG(152, ARITHMETIC_AND_LOGIC),
    IFEQ(153, JUMPS),
    IFNE(154, JUMPS),
    IFLT(155, JUMPS),
    IFGE(156, JUMPS),
    IFGT(157, JUMPS),
    IFLE(158, JUMPS),
    IF_ICMPEQ(159, JUMPS),
    IF_ICMPNE(160, JUMPS),
    IF_ICMPLT(161, JUMPS),
    IF_ICMPGE(162, JUMPS),
    IF_ICMPGT(163, JUMPS),
    IF_ICMPLE(164, JUMPS),
    IF_ACMPEQ(165, JUMPS),
    IF_ACMPNE(166, JUMPS),
    GOTO(167, JUMPS),
    JSR(168, JUMPS),
    RET(169, JUMPS),
    TABLESWITCH(170, JUMPS),
    LOOKUPSWITCH(171, JUMPS),
    IRETURN(172, Category.RETURN),
    LRETURN(173, Category.RETURN),
    FRETURN(174, Category.RETURN),
    DRETURN(175, Category.RETURN),
    ARETURN(176, Category.RETURN),
    RETURN(177, Category.RETURN),
    GETSTATIC(178, FIELDS),
    PUTSTATIC(179, FIELDS),
    GETFIELD(180, FIELDS),
    PUTFIELD(181, FIELDS),
    INVOKEVIRTUAL(182, METHODS),
    INVOKESPECIAL(183, METHODS),
    INVOKESTATIC(184, METHODS),
    INVOKEINTERFACE(185, METHODS),
    INVOKEDYNAMIC(186, METHODS),
    NEW(187, OBJECTS),
    NEWARRAY(188, ARRAYS),
    ANEWARRAY(189, ARRAYS),
    ARRAYLENGTH(190, ARRAYS),
    ATHROW(191, Category.RETURN),
    CHECKCAST(192, CASTS),
    INSTANCEOF(193, OBJECTS),
    MONITORENTER(194, OBJECTS),
    MONITOREXIT(195, OBJECTS),
    WIDE(196, LOCAL_VARIABLES),
    MULTIANEWARRAY(197, ARRAYS),
    IFNULL(198, JUMPS),
    IFNONNULL(199, JUMPS),
    GOTO_W(200, JUMPS),
    JSR_W(201, JUMPS);

    private static final Comparator<Opcode> opcodeComparator = new Comparator<Opcode>() {
        @Override
        public int compare(final Opcode o1, final Opcode o2) {
            return Integer.valueOf(o1.ordinal()).compareTo(Integer.valueOf(o2.ordinal()));
        }
    };

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

    public static SortedSet<Opcode> localVariables() {
        return getAllOpcodesFor(LOCAL_VARIABLES);
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

    /**
     * These instructions are used to manipulate values on the stack:
     * {@code POP} pops the value on top of the stack, {@code DUP} pushes a copy
     * of the top stack value, {@code SWAP} pops two values and pushes them in
     * the reverse order, etc.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    public static SortedSet<Opcode> stack() {
        return getAllOpcodesFor(STACK);
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
        return getAllOpcodesFor(CONSTANTS);
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
        return getAllOpcodesFor(ARITHMETIC_AND_LOGIC);
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
        return getAllOpcodesFor(CASTS);
    }

    public static SortedSet<Opcode> objects() {
        return getAllOpcodesFor(OBJECTS);
    }

    public static SortedSet<Opcode> fields() {
        return getAllOpcodesFor(FIELDS);
    }

    public static SortedSet<Opcode> methods() {
        return getAllOpcodesFor(METHODS);
    }

    public static SortedSet<Opcode> arrays() {
        return getAllOpcodesFor(ARRAYS);
    }

    public static SortedSet<Opcode> jumps() {
        return getAllOpcodesFor(JUMPS);
    }

    public static SortedSet<Opcode> returns() {
        return getAllOpcodesFor(Category.RETURN);
    }

    public static SortedSet<Opcode> nop() {
        return getAllOpcodesFor(Category.NOP);
    }

}
