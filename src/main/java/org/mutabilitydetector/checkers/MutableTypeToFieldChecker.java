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



import static java.lang.String.format;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_COLLECTION_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import java.util.List;
import java.util.Map;

import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.MutableTypeInformation.MutabilityLookup;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class MutableTypeToFieldChecker extends AbstractMutabilityChecker {

    private final TypeStructureInformation typeStructureInformation;
    private final MutableTypeInformation mutableTypeInfo;
    private final AsmVerifierFactory verifierFactory;
    private final List<String> genericTypesOfClass = Lists.newLinkedList();
    private final Map<String, String> genericFields = Maps.newHashMap();

    public MutableTypeToFieldChecker(TypeStructureInformation info,
                                     MutableTypeInformation mutableTypeInfo,
                                     AsmVerifierFactory verifierFactory) {
        this.typeStructureInformation = info;
        this.mutableTypeInfo = mutableTypeInfo;
        this.verifierFactory = verifierFactory;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        if (signature == null) { return; }

        new SignatureReader(signature).accept(new SignatureVisitor(Opcodes.ASM5) {
            @Override public void visitFormalTypeParameter(String name) {
                genericTypesOfClass.add(name);
            }
        });
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (signature == null) { return null ; }

        GenericFieldVisitor visitor = new GenericFieldVisitor();
        new SignatureReader(signature).acceptType(visitor);

        Optional<String> declaredType = visitor.declaredType();

        if (declaredType.isPresent() && genericTypesOfClass.contains(declaredType.get())) {
            genericFields.put(name, declaredType.get());
        }

        return super.visitField(access, name, desc, signature, value);
    }

    static final class GenericFieldVisitor extends SignatureVisitor {
        private String declaredType;
        private boolean fieldIsOfGenericType = true;

        public GenericFieldVisitor() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitTypeVariable(String name) {
            declaredType = name;
        }

        @Override
        public void visitClassType(String name) {
            fieldIsOfGenericType = false;
        }

        public Optional<String> declaredType() {
            return fieldIsOfGenericType ? Optional.of(declaredType) : Optional.<String>absent();
        }
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
            FieldLocation fieldLocation = fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass));

            switch (sort) {
            case Type.OBJECT:
                Dotted assignedToField = dotted(typeAssignedToField.getInternalName());

                if (isAssigningToGenericField(fieldName)) {
                    setAssigningToGenericFieldResult(fieldName, fieldLocation);
                    break;
                }

                MutabilityLookup mutabilityLookup = mutableTypeInfo.resultOf(dotted(ownerClass), assignedToField);

                if (mutabilityLookup.foundCyclicReference) {
                    setCircularReferenceResult(fieldLocation);
                    break;
                } else if (!mutabilityLookup.result.isImmutable.equals(IMMUTABLE) && isConcreteType(assignedToField)) {
                    setMutableFieldAssignmentResult(fieldLocation, assignedToField);
                    break;
                } else if(!isConcreteType(assignedToField)) {

                    UnmodifiableWrapResult unmodifiableWrapResult = new CollectionTypeWrappedInUnmodifiableIdiomChecker(
                            fieldInsnNode, typeAssignedToField, mutableTypeInfo.hardcodedCopyMethods()).checkWrappedInUnmodifiable();

                    if (!unmodifiableWrapResult.canBeWrapped) {
                        setAbstractFieldAssignmentResult(fieldLocation, assignedToField);
                        break;
                    } else if (unmodifiableWrapResult.canBeWrapped && unmodifiableWrapResult.invokesWhitelistedWrapperMethod) {
                        if (unmodifiableWrapResult.safelyCopiesBeforeWrapping) {
                            break;
                        } else {
                            setWrappingWithoutFirstCopyingResult(fieldLocation);
                            break;
                        }
                    } else if (unmodifiableWrapResult.canBeWrapped && !unmodifiableWrapResult.invokesWhitelistedWrapperMethod) {
                        setUnsafeWrappingResult(fieldLocation);
                    }
                }
                break;
            case Type.ARRAY:
                setResult("Field can have a mutable type (an array) assigned to it.",
                        fieldLocation, MUTABLE_TYPE_TO_FIELD);
                break;
            default:
                return;
            }
        }

        private void setAssigningToGenericFieldResult(String fieldName, FieldLocation fieldLocation) {
            setResult(String.format("Field can have a generic type (%s) assigned to it.", genericTypeOf(fieldName)),
                    fieldLocation, MUTABLE_TYPE_TO_FIELD);
        }

        private String genericTypeOf(String fieldName) {
            return genericFields.get(fieldName);
        }

        private boolean isAssigningToGenericField(String fieldName) {
            return genericFields.containsKey(fieldName);
        }

        private boolean isConcreteType(Dotted className) {
            return !(typeStructureInformation.isTypeAbstract(className) || typeStructureInformation.isTypeInterface(className));
        }

        private void setUnsafeWrappingResult(FieldLocation fieldLocation) {
            setResult("Attempts to wrap mutable collection type using a non-whitelisted unmodifiable wrapper method.",
                      fieldLocation, ABSTRACT_COLLECTION_TYPE_TO_FIELD);
        }

        private void setWrappingWithoutFirstCopyingResult(FieldLocation fieldLocation) {
            setResult("Attempts to wrap mutable collection type without safely performing a copy first.",
                    fieldLocation, ABSTRACT_COLLECTION_TYPE_TO_FIELD);
        }

        private void setAbstractFieldAssignmentResult(FieldLocation fieldLocation, Dotted assignedToField) {
            setResult(format("Field can have an abstract type (%s) assigned to it.", assignedToField),
                    fieldLocation, ABSTRACT_TYPE_TO_FIELD);
        }

        private void setMutableFieldAssignmentResult(FieldLocation fieldLocation, Dotted assignedToField) {
            setResult("Field can have a mutable type (" + assignedToField + ") " + "assigned to it.",
                    fieldLocation, MUTABLE_TYPE_TO_FIELD);
        }

        private void setCircularReferenceResult(FieldLocation fieldLocation) {
            setResult("There is a field assigned which creates a circular reference.",
                      fieldLocation, MUTABLE_TYPE_TO_FIELD);
        }


    }
}
