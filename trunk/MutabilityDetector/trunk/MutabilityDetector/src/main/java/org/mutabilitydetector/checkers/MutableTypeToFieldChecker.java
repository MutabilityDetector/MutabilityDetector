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

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import java.util.Map;

import org.mutabilitydetector.AnalysisSession.RequestedAnalysis;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.CollectionField.GenericType;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult;
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

import com.google.common.base.Optional;

public final class MutableTypeToFieldChecker extends AbstractMutabilityChecker {

    private final TypeStructureInformation typeStructureInformation;
    private final MutableTypeInformation mutableTypeInfo;
    private final AsmVerifierFactory verifierFactory;
    
    private final Map<String, Optional<String>> fieldSignatures = newHashMap();
    
    public MutableTypeToFieldChecker(TypeStructureInformation info, MutableTypeInformation mutableTypeInfo, AsmVerifierFactory verifierFactory) {
        this.typeStructureInformation = info;
        this.mutableTypeInfo = mutableTypeInfo;
        this.verifierFactory = verifierFactory;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        fieldSignatures.put(name, Optional.fromNullable(signature));
        return super.visitField(access, name, desc, signature, value);
    }
    

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new AssignMutableTypeToFieldChecker(ownerClass, access, name, desc, signature, exceptions, verifierFactory);
    }

    class AssignMutableTypeToFieldChecker extends FieldAssignmentVisitor {

        public AssignMutableTypeToFieldChecker(String owner,
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions, 
                AsmVerifierFactory verifierFactory) {
            super(owner, access, name, desc, signature, exceptions, verifierFactory);
        }

        @Override
        protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame, FieldInsnNode fieldInsnNode, BasicValue stackValue) {
            if (isInvalidStackValue(stackValue)) { return; }
            
            checkIfClassIsMutable(fieldInsnNode, stackValue.getType());
        }
        

        
        private void checkIfClassIsMutable(FieldInsnNode fieldInsnNode, Type typeAssignedToField) {
            int sort = typeAssignedToField.getSort();
            String fieldName = fieldInsnNode.name;

            switch (sort) {
            case Type.OBJECT:
                Dotted className = dotted(typeAssignedToField.getInternalName());
                RequestedAnalysis requestedAnalysis = mutableTypeInfo.resultOf(className);
                
                if (!requestedAnalysis.analysisComplete) {
                    setResult("There is a field assigned which creates a circular reference.", 
                              fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                              MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                } else if (!requestedAnalysis.result.isImmutable.equals(IMMUTABLE) && isConcreteType(className)) {
                    setResult("Field can have a mutable type (" + className + ") " + "assigned to it.",
                            fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                            MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                } else if(!isConcreteType(className)) {
                
                    UnmodifiableWrapResult unmodifiableWrapResult = new CollectionTypeWrappedInUmodifiableIdiomChecker(
                            fieldInsnNode, typeAssignedToField).checkWrappedInUnmodifiable();

                    if (!unmodifiableWrapResult.canBeWrapped) {
                        setResult(format("Field can have an abstract type (%s) assigned to it.", className),
                                fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                                MutabilityReason.ABSTRACT_TYPE_TO_FIELD);
                        return;
                    } else if (unmodifiableWrapResult.canBeWrapped) {
                        String fieldSignature = fieldSignatures.get(fieldInsnNode.name).orNull();
                        CollectionField collectionField = CollectionField.from(fieldInsnNode.desc, fieldSignature);
                        
                        Iterable<GenericType> genericParameters = collectionField.genericParameterTypes;
                        
                        if (!collectionField.isGeneric() || anyGenericParameterTypesAreMutable(genericParameters)) {
                            setResult(format("Field can have collection with mutable element type (%s) assigned to it.", collectionField.asString()),
                                    fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                                    MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE);
                            break;
                        }
                    }
                    
                    if (unmodifiableWrapResult.canBeWrapped && unmodifiableWrapResult.invokesWhitelistedWrapperMethod) {
                        if (unmodifiableWrapResult.safelyCopiesBeforeWrapping) {
                            break;
                        } else {
                            setResult("Attempts to wrap mutable collection type without safely performing a copy first.",
                                    fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                                    MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD);
                            break;
                        }
                    }
                }
                break;
            case Type.ARRAY:
                setResult("Field can have a mutable type (an array) assigned to it.",
                        fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                        MutabilityReason.MUTABLE_TYPE_TO_FIELD);
                break;
            default:
                return;
            }
        }

        private boolean anyGenericParameterTypesAreMutable(Iterable<GenericType> genericParameters) {
            for (GenericType genericType : genericParameters) {
                if (genericType.equals(GenericType.wildcard())) {
                    return true;
                } 
                
                RequestedAnalysis requestedAnalysis = mutableTypeInfo.resultOf(genericType.type);
                
                if (!requestedAnalysis.analysisComplete) {
                    // go ape
                } else {
                    if (!requestedAnalysis.result.isImmutable.equals(IMMUTABLE)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isConcreteType(Dotted className) {
            return !(typeStructureInformation.isTypeAbstract(className) || typeStructureInformation.isTypeInterface(className));
        }

    }
}
