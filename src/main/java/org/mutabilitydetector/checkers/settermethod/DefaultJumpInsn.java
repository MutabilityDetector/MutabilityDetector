/**
 * 
 */package org.mutabilitydetector.checkers.settermethod;

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



import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
@NotThreadSafe
final class DefaultJumpInsn implements JumpInsn {

    private final JumpInsnNode jumpInsnNode;
    private final int indexWithinBlock;
    private final int indexWithinMethod;

    private DefaultJumpInsn(final JumpInsnNode theJumpInsnNode,
            final int theIndexWithinBlock,
            final int theIndexWithinMethod) {
        jumpInsnNode = theJumpInsnNode;
        indexWithinBlock = theIndexWithinBlock;
        indexWithinMethod = theIndexWithinMethod;
    }

    public static DefaultJumpInsn newInstance(final JumpInsnNode jumpInsnNode,
            final int indexWithinBlock,
            final int indexWithinMethod) {
        return new DefaultJumpInsn(checkNotNull(jumpInsnNode), indexWithinBlock, indexWithinMethod);
    }

    @Override
    public JumpInsnNode getJumpInsnNode() {
        return jumpInsnNode;
    }

    @Override
    public int getIndexWithinBlock() {
        return indexWithinBlock;
    }

    @Override
    public int getIndexWithinMethod() {
        return indexWithinMethod;
    }

    @Override
    public Opcode getOpcode() {
        return Opcode.forInt(jumpInsnNode.getOpcode());
    }

    @Override
    public boolean isAssignmentGuard() {
        return false;
    }

    @Override
    public int compareTo(final JumpInsn o) {
        final Integer thisIndexWithinMethod = Integer.valueOf(indexWithinMethod);
        final Integer otherIndexWithinMethod = Integer.valueOf(o.getIndexWithinMethod());
        return thisIndexWithinMethod.compareTo(otherIndexWithinMethod);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + indexWithinBlock;
        result = prime * result + indexWithinMethod;
        result = prime * result + jumpInsnNode.hashCode();
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
        if (!(obj instanceof DefaultJumpInsn)) {
            return false;
        }
        final DefaultJumpInsn other = (DefaultJumpInsn) obj;
        if (indexWithinMethod != other.indexWithinMethod) {
            return false;
        }
        if (!jumpInsnNode.equals(other.jumpInsnNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" [");
        builder.append("jumpInsnNode=").append(toString(jumpInsnNode));
        builder.append(", indexWithinBlock=").append(indexWithinBlock);
        builder.append(", indexWithinMethod=").append(indexWithinMethod);
        builder.append("]");
        return builder.toString();
    }

    private static String toString(final JumpInsnNode jumpInsnNode) {
        final StringBuilder result = new StringBuilder();
        final Opcode opcode = Opcode.forInt(jumpInsnNode.getOpcode());
        final LabelNode labelNode = jumpInsnNode.label;
        final Label label = labelNode.getLabel();
        result.append("[").append(opcode.toString()).append(", label=").append(label).append("]");
        return result.toString();
    }

}
