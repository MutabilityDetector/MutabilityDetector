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



import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.AccessModifierQuery.type;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.objectweb.asm.MethodVisitor;

public final class CanSubclassChecker extends AsmMutabilityChecker {

    private boolean isFinal = true;
    private boolean hasOnlyPrivateConstructors = true;
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        if (type(access).isNotFinal()) {
            isFinal = false;
        }
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (MethodIs.aConstructor(name) && (method(access).isNotPrivate()) && method(access).isNotSynthetic()) {
            hasOnlyPrivateConstructors = false;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
    public void visitEnd() {
        if (!(isFinal || hasOnlyPrivateConstructors)) {
            setResult("Can be subclassed, therefore parameters declared to be this type " + "could be mutable subclasses at runtime.",
                      ClassLocation.fromInternalName(ownerClass),
                      MutabilityReason.CAN_BE_SUBCLASSED);
        }
     }

}
