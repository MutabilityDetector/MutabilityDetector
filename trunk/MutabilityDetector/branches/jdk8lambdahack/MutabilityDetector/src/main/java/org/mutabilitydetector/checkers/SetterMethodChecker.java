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
package org.mutabilitydetector.checkers;

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Slashed.slashed;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.VarStack.VarStackSnapshot;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

/**
 * This class checks, for each field, that there is no method available which can change the reference of the field.
 * 
 * The check will pass iff there is no method available to change a reference for ANY field.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 */
public final class SetterMethodChecker extends AbstractMutabilityChecker {

    private final PrivateMethodInvocationInformation privateMethodInvocationInfo;
    private final AsmVerifierFactory verifierFactory;

    private SetterMethodChecker(PrivateMethodInvocationInformation privateMethodInvocationInfo, 
                                 AsmVerifierFactory verifierFactory) {
        this.privateMethodInvocationInfo = privateMethodInvocationInfo;
        this.verifierFactory = verifierFactory;
    }

    public static SetterMethodChecker newSetterMethodChecker(PrivateMethodInvocationInformation privateMethodInvocationInfo, AsmVerifierFactory verifierFactory) {
        return new SetterMethodChecker(privateMethodInvocationInfo, verifierFactory);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new SetterAssignmentVisitor(ownerClass,
                access,
                name,
                desc,
                signature,
                exceptions, 
                verifierFactory);
    }

    class SetterAssignmentVisitor extends FieldAssignmentVisitor {

        private final VarStack varStack = new VarStack();

        public SetterAssignmentVisitor(String ownerName,
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions, 
                AsmVerifierFactory verifierFactory) {
            super(ownerName, access, name, desc, signature, exceptions, verifierFactory);
        }

        @Override
        protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
            if (MethodIs.aConstructor(name) || isInvalidStackValue(stackValue)) { return; }

            if (method(access).isStatic()) {
                detectInStaticMethod(fieldInsnNode);
            } else {
                detectInInstanceMethod(fieldInsnNode);
            }

        }

        private boolean isOnlyCalledFromConstructor() {
            MethodIdentifier methodId = forMethod(slashed(this.owner), name + ":" + desc);
            return privateMethodInvocationInfo.isOnlyCalledFromConstructor(methodId);
        }

        private void detectInStaticMethod(FieldInsnNode fieldInsnNode) {
            String ownerOfReassignedField = fieldInsnNode.owner;
            if (reassignedIsThisType(ownerOfReassignedField) && assignmentIsNotOnAParameter(fieldInsnNode)) {
                setIsImmutableResult(fieldInsnNode.name);
            }
        }

        private boolean assignmentIsNotOnAParameter(FieldInsnNode fieldInsnNode) {
            /*
             * This is a temporary hack/workaround. It's quite difficult to tell for sure if the owner of the reassigned
             * field is a parameter. But if the type is not included in the parameter list, we can guess it's not
             * (though it still may be).
             */
            boolean reassignmentIsOnATypeIncludedInParameters = this.desc.contains(fieldInsnNode.owner);

            return reassignmentIsOnATypeIncludedInParameters;
        }

        private boolean reassignedIsThisType(String ownerOfReassignedField) {
            return this.owner.compareTo(ownerOfReassignedField) == 0;
        }

        private void detectInInstanceMethod(FieldInsnNode fieldInsnNode) {
            if (isOnlyCalledFromConstructor()) { return; }

            VarStackSnapshot varStackSnapshot = varStack.next();
            if (varStackSnapshot.thisObjectWasAddedToStack()) {
                // Throwing an NPE, assuming it's mutable for now.
                setIsImmutableResult(fieldInsnNode.name);
                
//                int indexOfOwningObject = varStackSnapshot.indexOfOwningObject();
//                if (isThisObject(indexOfOwningObject)) {
//                    setIsImmutableResult(fieldInsnNode.name);
//                } else {
//                    // Setting field on other instance of 'this' type
//                }

            }
        }

        @SuppressWarnings("unused")
        private boolean isThisObject(int indexOfOwningObject) {
            return indexOfOwningObject == 0;
        }

        @Override
        public void visitFieldInsn(int opcode, String fieldsOwner, String fieldName, String fieldDesc) {
            super.visitFieldInsn(opcode, fieldsOwner, fieldName, fieldDesc);
            if (opcode == Opcodes.PUTFIELD) {
                varStack.takeSnapshotOfVarsAtPutfield();
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            varStack.visitVarInsn(var);
        }

        private void setIsImmutableResult(String fieldName) {
            String message = format("Field [%s] can be reassigned within method [%s]", fieldName, this.name);
            addResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }

    }

}
