/*
 *    Copyright (c) 2008-2013 Graham Allan
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

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.checkers.CollectionField.GenericType;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.MutableTypeInformation.MutabilityLookup;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public final class CollectionWithMutableElementTypeToFieldChecker extends AbstractMutabilityChecker {

    private final MutableTypeInformation mutableTypeInfo;
    private final AsmVerifierFactory verifierFactory;
    private final JdkCollectionTypes jdkCollectionTypes = new JdkCollectionTypes();
    
    private final Map<String, String> fieldSignatures = newHashMap();
    
    public CollectionWithMutableElementTypeToFieldChecker(MutableTypeInformation mutableTypeInfo, 
                                                          AsmVerifierFactory verifierFactory) {
        this.mutableTypeInfo = mutableTypeInfo;
        this.verifierFactory = verifierFactory;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        fieldSignatures.put(name, signature);
        return super.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new AssignCollectionToFieldVisitor(ownerClass, access, name, desc, signature, exceptions, verifierFactory);
    }

    class AssignCollectionToFieldVisitor extends FieldAssignmentVisitor {

        public AssignCollectionToFieldVisitor(String owner,
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
            
            Type typeAssignedToField = stackValue.getType();
            if (typeAssignedToField.getSort() == Type.OBJECT) {
                checkIfClassIsMutable(fieldInsnNode, typeAssignedToField);
            }
        }

        private void checkIfClassIsMutable(FieldInsnNode fieldInsnNode, Type typeAssignedToField) {
            Dotted fieldClass = dotted(typeAssignedToField.getInternalName());
            
            if (jdkCollectionTypes.isCollectionType(fieldClass)) {
                String fieldName = fieldInsnNode.name;
                String fieldSignature = fieldSignatures.get(fieldName);
                CollectionField collectionField = CollectionField.from(fieldInsnNode.desc, fieldSignature);
                
                Iterable<GenericType> genericParameters = collectionField.genericParameterTypes;
                
                if (!collectionField.isGeneric() || anyGenericParameterTypesAreMutable(genericParameters)) {
                    setResult(format("Field can have collection with mutable element type (%s) assigned to it.", collectionField.asString()),
                              fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                              MutabilityReason.COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE);
                }
            }
        }
        
        private boolean anyGenericParameterTypesAreMutable(Iterable<GenericType> genericParameters) {
            for (GenericType genericType : genericParameters) {
                if (genericType.equals(GenericType.wildcard())) {
                    return true;
                } 
                
                MutabilityLookup mutabilityLookup = mutableTypeInfo.resultOf(dotted(ownerClass), genericType.type);
                
                if (mutabilityLookup.foundCyclicReference || !mutabilityLookup.result.isImmutable.equals(IMMUTABLE)) {
                    return true;
                }
            }
            return false;
        }
    }
}
