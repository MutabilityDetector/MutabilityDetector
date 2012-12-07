/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.12.2012
 */
public enum Opcode implements OpcodeInformation, Comparator<Opcode> {
    NOP,
    ACONST_NULL,
    ICONST_M1,
    ICONST_0,
    ICONST_1,
    ICONST_2,
    ICONST_3,
    ICONST_4,
    ICONST_5,
    LCONST_0,
    LCONST_1,
    FCONST_0,
    FCONST_1,
    FCONST_2,
    DCONST_0,
    DCONST_1,
    BIPUSH,
    SIPUSH,
    LDC,
    LDC_W,
    LDC2_W,
    ILOAD,
    LLOAD,
    FLOAD,
    DLOAD,
    ALOAD,
    ILOAD_0,
    ILOAD_1,
    ILOAD_2,
    ILOAD_3,
    LLOAD_0,
    LLOAD_1,
    LLOAD_2,
    LLOAD_3,
    FLOAD_0,
    FLOAD_1,
    FLOAD_2,
    FLOAD_3,
    DLOAD_0,
    DLOAD_1,
    DLOAD_2,
    DLOAD_3,
    ALOAD_0,
    ALOAD_1,
    ALOAD_2,
    ALOAD_3,
    IALOAD,
    LALOAD,
    FALOAD,
    DALOAD,
    AALOAD,
    BALOAD,
    CALOAD,
    SALOAD,
    ISTORE,
    LSTORE,
    FSTORE,
    DSTORE,
    ASTORE,
    ISTORE_0,
    ISTORE_1,
    ISTORE_2,
    ISTORE_3,
    LSTORE_0,
    LSTORE_1,
    LSTORE_2,
    LSTORE_3,
    FSTORE_0,
    FSTORE_1,
    FSTORE_2,
    FSTORE_3,
    DSTORE_0,
    DSTORE_1,
    DSTORE_2,
    DSTORE_3,
    ASTORE_0,
    ASTORE_1,
    ASTORE_2,
    ASTORE_3,
    IASTORE,
    LASTORE,
    FASTORE,
    DASTORE,
    AASTORE,
    BASTORE,
    CASTORE,
    SASTORE,
    POP,
    POP2,
    DUP,
    DUP_X1,
    DUP_X2,
    DUP2,
    DUP2_X1,
    DUP2_X2,
    SWAP,
    IADD,
    LADD,
    FADD,
    DADD,
    ISUB,
    LSUB,
    FSUB,
    DSUB,
    IMUL,
    LMUL,
    FMUL,
    DMUL,
    IDIV,
    LDIV,
    FDIV,
    DDIV,
    IREM,
    LREM,
    FREM,
    DREM,
    INEG,
    LNEG,
    FNEG,
    DNEG,
    ISHL,
    LSHL,
    ISHR,
    LSHR,
    IUSHR,
    LUSHR,
    IAND,
    LAND,
    IOR,
    LOR,
    IXOR,
    LXOR,
    IINC,
    I2L,
    I2F,
    I2D,
    L2I,
    L2F,
    L2D,
    F2I,
    F2L,
    F2D,
    D2I,
    D2L,
    D2F,
    I2B,
    I2C,
    I2S,
    LCMP,
    FCMPL,
    FCMPG,
    DCMPL,
    DCMPG,
    IFEQ,
    IFNE,
    IFLT,
    IFGE,
    IFGT,
    IFLE,
    IF_ICMPEQ,
    IF_ICMPNE,
    IF_ICMPLT,
    IF_ICMPGE,
    IF_ICMPGT,
    IF_ICMPLE,
    IF_ACMPEQ,
    IF_ACMPNE,
    GOTO,
    JSR,
    RET,
    TABLESWITCH,
    LOOKUPSWITCH,
    IRETURN,
    LRETURN,
    FRETURN,
    DRETURN,
    ARETURN,
    RETURN,
    GETSTATIC,
    PUTSTATIC,
    GETFIELD,
    PUTFIELD,
    INVOKEVIRTUAL,
    INVOKESPECIAL,
    INVOKESTATIC,
    INVOKEINTERFACE,
    INVOKEDYNAMIC,
    NEW,
    NEWARRAY,
    ANEWARRAY,
    ARRAYLENGTH,
    ATHROW,
    CHECKCAST,
    INSTANCEOF,
    MONITORENTER,
    MONITOREXIT,
    WIDE,
    MULTIANEWARRAY,
    IFNULL,
    IFNONNULL,
    GOTO_W,
    JSR_W;

    private final String opcodeAsHex;
    private final SortedSet<Opcode> stack;
    private static final SortedSetMultimap<Category, Opcode> categorised;

    private Opcode() {
        opcodeAsHex = Integer.toHexString(ordinal());
        stack = new TreeSet<Opcode>(this);
        categorised = TreeMultimap.create();
    }
//
    private Opcode(final Category category) {
        this();
        categorised.put(category, value);
    }
    
    @Override
    public int compare(final Opcode o1, final Opcode o2) {
        return Integer.valueOf(o1.ordinal()).compareTo(Integer.valueOf(o2.ordinal()));
    }

    @Override
    public String asHex() {
        return opcodeAsHex;
    }

    @Override
    public SortedSet<Opcode> stack() {
        final ImmutableSortedSet.Builder<Opcode> builder = new ImmutableSortedSet.Builder<Opcode>(this);
        builder.add(POP).add(POP2).add(DUP).add(DUP_X1).add(DUP_X2).add(DUP2).add(DUP2_X1).add(DUP2_X2).add(SWAP);
        return builder.build();
    }

    @Override
    public SortedSet<Opcode> constants() {
        // TODO Auto-generated method stub
        return null;
    }

}
