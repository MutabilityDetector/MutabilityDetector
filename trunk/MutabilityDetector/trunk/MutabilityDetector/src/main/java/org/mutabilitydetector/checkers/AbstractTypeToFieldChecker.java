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

import org.mutabilitydetector.AnalysisClassLoader;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.info.TypeStructureInformation;
import org.mutabilitydetector.locations.ClassLocation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class AbstractTypeToFieldChecker extends AbstractMutabilityChecker {

    private final TypeStructureInformation typeStructureInformation;
    private final AnalysisClassLoader analysisClassLoader;

    public AbstractTypeToFieldChecker(TypeStructureInformation typeStructureInformation, AnalysisClassLoader analysisClassLoader) {
        this.typeStructureInformation = typeStructureInformation;
        this.analysisClassLoader = analysisClassLoader;
    }

    public static AbstractTypeToFieldChecker newAbstractTypeToFieldChecker(TypeStructureInformation requestInformation, AnalysisClassLoader analysisClassLoader) {
        return new AbstractTypeToFieldChecker(requestInformation, analysisClassLoader);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new AssignAbstractTypeVisitor(ownerClass, access, name, desc, signature, exceptions, analysisClassLoader);
    }

    private class AssignAbstractTypeVisitor extends FieldAssignmentVisitor {

        public AssignAbstractTypeVisitor(String owner,
                int access,
                String name,
                String desc,
                String signature,
                String[] exceptions, 
                AnalysisClassLoader fallbackClassLoader) {
            super(owner, access, name, desc, signature, exceptions, fallbackClassLoader);
        }

        @Override
        protected void visitFieldAssignmentFrame(Frame<BasicValue> assignmentFrame,
                FieldInsnNode fieldInsnNode,
                BasicValue stackValue) {
            if (isInvalidStackValue(stackValue)) { return; }
            checkIfClassIsAbstract(fieldInsnNode.name, stackValue.getType());
        }

        void checkIfClassIsAbstract(String fieldName, Type objectType) {
            int sort = objectType.getSort();
            if (sort != Type.OBJECT) { return; }
            String dottedClassName = dottedClassName(objectType);
            boolean isAbstract = typeStructureInformation.isTypeAbstract(dotted(dottedClassName));

            if (isAbstract) {
                addResult(format("Field can have an abstract type (%s) assigned to it.", dottedClassName),
                        fieldLocation(fieldName, ClassLocation.fromInternalName(ownerClass)),
                        MutabilityReason.ABSTRACT_TYPE_TO_FIELD);
            }
        }
    }
}
