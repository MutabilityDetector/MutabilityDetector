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



import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

/**
 * Used to check for the existence of an access flag used in ASM visitors.
 * 
 * The class is designed to be used in a fluent way, a typical usage would be:
 * 
 * <code>
 * 
 * {@link #method(int)}.isPrivate();
 * {@link #field(int)}.isStatic();
 * </code>
 * 
 */
public class AccessModifierQuery {
    private int access;

    private AccessModifierQuery(int access) {
        this.access = access;
    }

    private boolean includesAccess(int accessFlag) {
        return (this.access & accessFlag) != 0;
    }

    public static AccessModifierQuery method(int access) {
        return new AccessModifierQuery(access);
    }

    public static AccessModifierQuery type(int access) {
        return new AccessModifierQuery(access);
    }

    public static AccessModifierQuery field(int access) {
        return new AccessModifierQuery(access);
    }

    private boolean is(int flag) {
        return includesAccess(flag);
    }

    public boolean isPrivate() {
        return is(ACC_PRIVATE);
    }

    public boolean isNotPrivate() {
        return !is(ACC_PRIVATE);
    }

    public boolean isFinal() {
        return is(ACC_FINAL);
    }

    public boolean isNotFinal() {
        return !is(ACC_FINAL);
    }
    
    public boolean isSynthetic() {
        return is(ACC_SYNTHETIC);
    }
    
    public boolean isNotSynthetic() {
        return !is(ACC_SYNTHETIC);
    }
    

    public boolean isAbstract() {
        return is(ACC_ABSTRACT);
    }

    public boolean isInterface() {
        return is(ACC_INTERFACE);
    }

    public boolean isStatic() {
        return is(ACC_STATIC);
    }

    public boolean isNotStatic() {
        return !is(ACC_STATIC);
    }

}
