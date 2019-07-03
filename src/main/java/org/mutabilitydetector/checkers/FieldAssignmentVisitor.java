package org.mutabilitydetector.checkers;

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



import java.util.ArrayList;
import java.util.List;

import org.mutabilitydetector.asmoverride.AsmCompatibility;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public abstract class FieldAssignmentVisitor extends MethodNode {

    protected List<FieldInsnNode> fieldAssignments = new ArrayList<FieldInsnNode>();
    protected final String owner;
    private final AsmVerifierFactory verifierFactory;

    public FieldAssignmentVisitor(String owner,
            int access,
            String name,
            String desc,
            String signature,
            String[] exceptions,
            AsmVerifierFactory verifierFactory) {
        super(AsmCompatibility.AsmApiVersion, access, name, desc, signature, exceptions);
        this.owner = owner;
        this.verifierFactory = verifierFactory;
    }

    @Override
    public void visitFieldInsn(int opcode, String fieldsOwner, String fieldName, String fieldDesc) {
        super.visitFieldInsn(opcode, fieldsOwner, fieldName, fieldDesc);
        if (opcode == Opcodes.PUTFIELD) {
            fieldAssignments.add((FieldInsnNode) instructions.getLast());
        }

    }

    /**
     *
     * At the end of a method, the frames are analysed to be able to inspect
     * the state of the stack when the field is assigned. This method is
     * called, giving the frame at the time of the assignment, as well as
     * the instruction node.
     *
     * @param assignmentFrame
     * @param fieldInsnNode
     */
    abstract protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue);

    @Override
    public void visitEnd() {
        super.visitEnd();

        if (fieldAssignments.isEmpty()) { return; }

        Analyzer<BasicValue> a = new Analyzer<BasicValue>(verifierFactory.interpreter());
        Frame<BasicValue>[] frames;
        try {
            frames = a.analyze(owner, this);

            for (FieldInsnNode fieldInsnNode : fieldAssignments) {
                Frame<BasicValue> assignmentFrame = frames[instructions.indexOf(fieldInsnNode)];
                int stackSlot = assignmentFrame.getStackSize() - 1;
                BasicValue stackValue = assignmentFrame.getStack(stackSlot);
                visitFieldAssignmentFrame(assignmentFrame, fieldInsnNode, stackValue);
            }
        } catch (AnalyzerException forwarded) {
            throw new RuntimeException(forwarded);
        }
    }

    protected boolean isInvalidStackValue(BasicValue stackValue) {
        return stackValue == null || "Lnull;".equals(stackValue.getType().toString());
    }

    protected BasicValue getStackValue(Frame<BasicValue> assignmentFrame) {
        int stackSlot = assignmentFrame.getStackSize() - 1;
        return assignmentFrame.getStack(stackSlot);
    }
}
