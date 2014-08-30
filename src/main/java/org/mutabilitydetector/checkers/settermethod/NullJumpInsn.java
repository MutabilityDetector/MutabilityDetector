package org.mutabilitydetector.checkers.settermethod;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;

/**
 * @author Juergen Fickel
 * @version 19.02.2013
 */
final class NullJumpInsn implements JumpInsn {

    private static final class InstanceHolder {
        private static final JumpInsn INSTANCE = new NullJumpInsn();
    }

    private static final JumpInsnNode EMPTY_JUMP_INSN_NODE = new JumpInsnNode(Opcodes.NOP, null);

    private NullJumpInsn() {
        super();
    }

    public static JumpInsn getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        return this.equals(o) ? 0 : -1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return EMPTY_JUMP_INSN_NODE;
    }

    @Override
    public int getIndexWithinBlock() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getIndexWithinMethod() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Opcode getOpcode() {
        return Opcode.NOP;
    }

    @Override
    public boolean isAssignmentGuard() {
        return false;
    }

    @Override
    public String toString() {
        return "NullJumpInsn []";
    }

}
