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
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class AbstractTypeToFieldChecker extends AbstractMutabilityChecker {

    private final TypeStructureInformation typeStructureInformation;
    private final AsmVerifierFactory verifierFactory;

    private AbstractTypeToFieldChecker(TypeStructureInformation typeStructureInformation, AsmVerifierFactory verifierFactory) {
        this.typeStructureInformation = typeStructureInformation;
        this.verifierFactory = verifierFactory;
    }

    public static AbstractTypeToFieldChecker newAbstractTypeToFieldChecker(TypeStructureInformation requestInformation, AsmVerifierFactory verifierFactory) {
        return new AbstractTypeToFieldChecker(requestInformation, verifierFactory);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new AssignAbstractTypeVisitor(ownerClass, access, name, desc, signature, exceptions, verifierFactory);
    }

    private class AssignAbstractTypeVisitor extends FieldAssignmentVisitor {


        public AssignAbstractTypeVisitor(String owner,
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions, 
                AsmVerifierFactory verifierFactory) {
            super(owner, access, name, desc, signature, exceptions, verifierFactory);
        }

        @Override
        protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame,
                FieldInsnNode fieldInsnNode,
                BasicValue stackValue) {
            if (isInvalidStackValue(stackValue)) { return; }
            if (!isReferenceType(stackValue.getType())) { return; }
            
            checkIfClassIsAbstract(fieldInsnNode.name, stackValue.getType(), fieldInsnNode);
        }

        private boolean isReferenceType(Type objectType) {
            return objectType.getSort() == Type.OBJECT;
        }

        void checkIfClassIsAbstract(String fieldName, Type objectType, FieldInsnNode fieldInsnNode) {
            Dotted className = dotted(objectType.getInternalName());
            boolean isAbstract = typeStructureInformation.isTypeAbstract(className);
            
            if (isAbstract) {
                UnmodifiableWrapResult unmodifiableWrapResult = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode).checkWrappedInUnmodifiable();
                
                if (unmodifiableWrapResult.canBeWrapped && unmodifiableWrapResult.isWrapped) {
                    if (unmodifiableWrapResult.safelyCopiesBeforeWrapping) {
                        return;
                    } else {
                        addResult("Attempts to wrap mutable collection type without perfoming a copy first.",
                                fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                                MutabilityReason.ABSTRACT_TYPE_TO_FIELD);
                        return;
                    }
                } else {
                    addResult(format("Field can have an abstract type (%s) assigned to it.", className),
                            fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                            MutabilityReason.ABSTRACT_TYPE_TO_FIELD);
                }
            }
        }

    }
}
