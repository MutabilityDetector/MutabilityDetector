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
import org.mutabilitydetector.checkers.VarStack.VarStackSnapshot;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class checks, for each field, that there is no method available which
 * can change the reference of the field.
 * 
 * The check will pass iff there is no method available to change a reference
 * for ANY field.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 */
public final class SetterMethodChecker extends AbstractMutabilityChecker {

    private final class SetterAssignmentVisitor extends FieldAssignmentVisitor {
    
        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final VarStack varStack = new VarStack();
    
        public SetterAssignmentVisitor(final String ownerName, final int access, final String name, final String desc,
                final String signature, final String[] exceptions) {
            super(ownerName, access, name, desc, signature, exceptions);
        }
    
        @Override
        protected void visitFieldAssignmentFrame(final Frame assignmentFrame, final FieldInsnNode fieldInsnNode,
                final BasicValue stackValue) {
            logger.debug(
                    "Parameters: assignmentFrame: {}, fieldInsnNode, desc: {}, name: {}, owner: {}, stackValue: {}.",
                    assignmentFrame, fieldInsnNode.desc, fieldInsnNode.name, fieldInsnNode.owner, stackValue);
            if (MethodIs.aConstructor(name) || isInvalidStackValue(stackValue)) {
                return;
            }
            if (method(access).isStatic()) {
                detectInStaticMethod(fieldInsnNode);
            } else {
                detectInInstanceMethod(fieldInsnNode);
            }
        }
    
        private boolean isOnlyCalledFromConstructor() {
            final MethodIdentifier methodId = forMethod(slashed(owner), name + ":" + desc);
            return privateMethodInvocationInfo.isOnlyCalledFromConstructor(methodId);
        }
    
        private void detectInStaticMethod(final FieldInsnNode fieldInsnNode) {
            final String ownerOfReassignedField = fieldInsnNode.owner;
            if (reassignedIsThisType(ownerOfReassignedField) && assignmentIsNotOnAParameter(fieldInsnNode)) {
                setIsImmutableResult(fieldInsnNode.name);
            }
        }
    
        private boolean assignmentIsNotOnAParameter(final FieldInsnNode fieldInsnNode) {
            /*
             * This is a temporary hack/workaround. It's quite difficult to tell
             * for sure if the owner of the reassigned field is a parameter. But
             * if the type is not included in the parameter list, we can guess
             * it's not (though it still may be).
             */
            final boolean reassignmentIsOnATypeIncludedInParameters = desc.contains(fieldInsnNode.owner);
    
            return reassignmentIsOnATypeIncludedInParameters;
        }
    
        private boolean reassignedIsThisType(final String ownerOfReassignedField) {
            return owner.compareTo(ownerOfReassignedField) == 0;
        }
    
        private void detectInInstanceMethod(final FieldInsnNode fieldInsnNode) {
            logger.debug("Parameter fieldInsnNode, desc: {}, name: {}, owner: {}.", fieldInsnNode.desc,
                    fieldInsnNode.name, fieldInsnNode.owner);
            if (isOnlyCalledFromConstructor()) {
                return;
            }
            final VarStackSnapshot varStackSnapshot = varStack.next();
            if (varStackSnapshot.thisObjectWasAddedToStack()) {
                // Throwing an NPE, assuming it's mutable for now.
                setIsImmutableResult(fieldInsnNode.name);
            }
        }
    
        @SuppressWarnings("unused")
        private boolean isThisObject(final int indexOfOwningObject) {
            return indexOfOwningObject == 0;
        }
    
        // Wird vor `visitFieldAssignmentFrame` aufgerufen.
        @Override
        public void visitFieldInsn(final int opcode, final String fieldsOwner, final String fieldName,
                final String fieldDesc) {
            logger.debug("Parameters: opcode: {}, fieldsOwner: {}, fieldName: {}, fieldDesc: {}.", opcode, fieldsOwner,
                    fieldName, fieldDesc);
            super.visitFieldInsn(opcode, fieldsOwner, fieldName, fieldDesc);
            if (opcode == Opcodes.PUTFIELD) {
                varStack.takeSnapshotOfVarsAtPutfield();
            }
        }
    
        @Override
        public void visitVarInsn(final int opcode, final int var) {
            logger.debug("Parameters: opcode: {}, var: {}.", opcode, var);
            super.visitVarInsn(opcode, var);
            varStack.visitVarInsn(var);
        }
    
        private void setIsImmutableResult(final String fieldName) {
            final String message = format("Field [%s] can be reassigned within method [%s]", fieldName, name);
            logger.debug(message);
            addResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }
    
    } // class SetterAssignmentVisitor


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PrivateMethodInvocationInformation privateMethodInvocationInfo;

    /**
     * @see #newSetterMethodChecker(PrivateMethodInvocationInformation)
     */
    private SetterMethodChecker(final PrivateMethodInvocationInformation privateMethodInvocationInfo) {
        this.privateMethodInvocationInfo = privateMethodInvocationInfo;
    }

    public static SetterMethodChecker newSetterMethodChecker(
            final PrivateMethodInvocationInformation privateMethodInvocationInfo) {
        return new SetterMethodChecker(privateMethodInvocationInfo);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
            final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
            final String[] exceptions) {
        logger.debug("Parameters: access: {}, name: '{}', desc: '{}', signature: '{}', exceptions: {}", access, name,
                desc, signature, exceptions);
        return new SetterAssignmentVisitor(ownerClass, access, name, desc, signature, exceptions);
    }

}
