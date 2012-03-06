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

import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import org.mutabilitydetector.IAnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public final class MutableTypeToFieldChecker extends AbstractMutabilityChecker {

    private final TypeStructureInformation typeStructureInformation;
    private final MutableTypeInformation mutableTypeInfo;

    public MutableTypeToFieldChecker(TypeStructureInformation info, MutableTypeInformation mutableTypeInfo) {
        this.typeStructureInformation = info;
        this.mutableTypeInfo = mutableTypeInfo;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new AssignMutableTypeToFieldChecker(ownerClass, access, name, desc, signature, exceptions);
    }

    class AssignMutableTypeToFieldChecker extends FieldAssignmentVisitor {

        public AssignMutableTypeToFieldChecker(String owner,
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions) {
            super(owner, access, name, desc, signature, exceptions);
        }

        @Override
        protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
            if (isInvalidStackValue(stackValue)) { return; }
            checkIfClassIsMutable(fieldInsnNode.name, stackValue.getType());
        }

        private void checkIfClassIsMutable(String fieldName, Type type) {
            int sort = type.getSort();
            switch (sort) {
            case Type.OBJECT:
                Dotted className = dotted(dottedClassName(type));
                RequestedAnalysis requestedAnalysis = mutableTypeInfo.resultOf(className);
                
                if (!requestedAnalysis.analysisComplete) {
                    addResult("There is a field assigned which creates a circular reference.", 
                              fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                              MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                } else if (!requestedAnalysis.result.isImmutable.equals(IMMUTABLE) && isConcreteType(className)) {
                    addResult("Field can have a mutable type (" + className + ") " + "assigned to it.",
                            fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                            MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                }
                break;
            case Type.ARRAY:
                addResult("Field can have a mutable type (a primitive array) assigned to it.",
                        fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                        MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                break;
            default:
                return;
            }
        }

        private boolean isConcreteType(Dotted className) {
            return !(typeStructureInformation.isTypeAbstract(className) || typeStructureInformation.isTypeInterface(className));
        }

    }
}
