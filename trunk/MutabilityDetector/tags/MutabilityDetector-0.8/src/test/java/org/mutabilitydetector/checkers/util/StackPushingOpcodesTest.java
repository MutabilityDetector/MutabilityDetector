/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.checkers.util;

import static java.lang.String.format;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.objectweb.asm.Opcodes.*;

import org.junit.Test;

public class StackPushingOpcodesTest {

    StackPushingOpcodes stackPushingOpcodes = new StackPushingOpcodes();

    @Test
    public void returnsTrueForAllOpcodesWhichPushSomethingOntoTheStack() {
        assertPushesOntoStack(
                DUP, DUP2, DUP_X1, DUP_X2, DUP2_X1, DUP2_X2,
                NEW,
                BIPUSH, SIPUSH,
                LDC,
                ALOAD, DLOAD, FLOAD, LLOAD, ILOAD,
                ACONST_NULL,
                ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, ICONST_M1,
                FCONST_0, FCONST_1, FCONST_2,
                DCONST_0, DCONST_1,
                IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
                GETFIELD, GETSTATIC
                );
    }
    
    @Test
    public void returnsFalseForAllOpcodesWhichDoNotPushOntoStack() throws Exception {
        assertDoesNotPushOntoStack(NOP,
                ISTORE, LSTORE, FSTORE, DSTORE, ASTORE,
                IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
                POP, POP2,
                SWAP,
                IADD, LADD, FADD, DADD,
                ISUB, LSUB, FSUB, DSUB,
                IMUL, LMUL, FMUL, DMUL,
                IDIV, LDIV, FDIV, DDIV,
                IREM, LREM, FREM, DREM,
                INEG, LNEG, FNEG, DNEG,
                ISHL, ISHR,
                LSHL, LSHR,
                IUSHR, LUSHR,
                IAND, LAND,
                IOR, LOR, IXOR, LXOR,
                IINC, 
                I2L, I2F, I2D,
                L2I, L2F, L2D,
                F2I, F2L, F2D,
                D2I, D2L, D2F,
                I2B, I2C, I2S,
                LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
                IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE,
                IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE,
                GOTO, JSR, RET, TABLESWITCH, LOOKUPSWITCH, IRETURN, LRETURN, DRETURN, ARETURN, RETURN,
                PUTFIELD, PUTSTATIC,
                INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE, INVOKEDYNAMIC,
                NEWARRAY, ANEWARRAY, MULTIANEWARRAY,
                ARRAYLENGTH, 
                ATHROW, 
                CHECKCAST, INSTANCEOF, 
                MONITORENTER, MONITOREXIT, 
                IFNULL, IFNONNULL);
    }


    private void assertPushesOntoStack(int... opcodes) {
        for (int opcode : opcodes) {
            assertTrue(format("opcode [%d] pushes onto the stack", opcode), stackPushingOpcodes.includes(opcode));
        }
    }
    
    private void assertDoesNotPushOntoStack(int... opcodes) {
        for (int opcode : opcodes) {
            assertFalse(format("opcode [%d] pushes onto the stack", opcode), stackPushingOpcodes.includes(opcode));
        }
    }

}
