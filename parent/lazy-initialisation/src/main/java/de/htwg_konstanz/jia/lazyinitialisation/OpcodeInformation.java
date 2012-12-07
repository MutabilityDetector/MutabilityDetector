/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.SortedSet;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 07.12.2012
 */
public interface OpcodeInformation {

    String asHex();

    /**
     * These instructions are used to manipulate values on the stack:
     * {@code POP} pops the value on top of the stack, {@code DUP} pushes a copy
     * of the top stack value, {@code SWAP} pops two values and pushes them in
     * the reverse order, etc.
     * 
     * @return a set of opcodes which have the above mentioned characteristics.
     */
    SortedSet<Opcode> stack();

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
    SortedSet<Opcode> constants();

}
