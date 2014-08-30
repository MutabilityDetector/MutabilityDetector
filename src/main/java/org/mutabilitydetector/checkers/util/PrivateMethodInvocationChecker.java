package org.mutabilitydetector.checkers.util;

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
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MethodIs;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.checkers.info.MethodIdentifier;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public final class PrivateMethodInvocationChecker extends AbstractMutabilityChecker {

    private final Map<MethodIdentifier, Boolean> privateMethodCalledFromConstructorMap = new HashMap<MethodIdentifier, Boolean>();

    public boolean isPrivateMethodCalledOnlyFromConstructor(String methodDescriptor) {
        MethodIdentifier identifier = makeMethodIdentifier(methodDescriptor);
        if (privateMethodCalledFromConstructorMap.containsKey(identifier)) { return privateMethodCalledFromConstructorMap.get(identifier); }

        String message = format("Could not find method descriptor %s. Available descriptors are: %n%s",
                identifier,
                privateMethodCalledFromConstructorMap.keySet().toString());
        throw new MutabilityAnalysisException(message);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        super.visitMethod(access, name, desc, signature, exceptions);

        boolean isPrivate = method(access).isPrivate();
        String methodDescriptor = makeMethodDescriptor(name, desc);
        privateMethodCalledFromConstructorMap.put(makeMethodIdentifier(methodDescriptor), isPrivate);

        return new MethodInvocationVisitor(access, name, desc, signature, exceptions);
    }

    private String makeMethodDescriptor(String methodName, String methodDesc) {
        return format("%s:%s", methodName, methodDesc);
    }

    private MethodIdentifier makeMethodIdentifier(String desc) {
        return forMethod(slashed(ownerClass), desc);
    }

    private class MethodInvocationVisitor extends MethodNode {

        public MethodInvocationVisitor(int access, String name, String desc, String signature, String[] exceptions) {
            super(Opcodes.ASM5, access, name, desc, signature, exceptions);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc, boolean isInterface) {
            if (MethodIs.aConstructor(this.name)) { return; }
            
            MethodIdentifier identifier = makeMethodIdentifier(makeMethodDescriptor(methodName, methodDesc));
            privateMethodCalledFromConstructorMap.put(identifier, false);
        }
    }

}
