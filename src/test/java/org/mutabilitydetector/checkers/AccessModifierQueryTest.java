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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.AASTORE;

import org.junit.Test;

public class AccessModifierQueryTest {

    @Test
    public void isPrivate_true() throws Exception {
        assertTrue("Expected access to be private.", method(ACC_PRIVATE).isPrivate());
    }

    @Test
    public void isPrivate_false() throws Exception {
        assertFalse("Expected access to not be private.", method(ACC_ABSTRACT).isPrivate());
    }

    @Test
    public void isFinal_true() throws Exception {
        assertTrue("Expected access to be final.", method(ACC_FINAL).isFinal());
    }

    @Test
    public void isFinal_false() throws Exception {
        assertFalse("Expected access to not be final.", method(ACC_ABSTRACT).isFinal());
    }

    @Test
    public void isAbstract_true() throws Exception {
        assertTrue("Expected access to be abstract.", method(ACC_ABSTRACT).isAbstract());
    }

    @Test
    public void isAbstract_false() throws Exception {
        assertFalse("Expected access to not be abstract.", method(AASTORE).isAbstract());
    }

    @Test
    public void isInterface_true() throws Exception {
        assertTrue("Expected access to be interface.", method(ACC_INTERFACE).isInterface());
    }

    @Test
    public void isinterface_false() throws Exception {
        assertFalse("Expected access to not be interface.", method(ACC_ABSTRACT).isInterface());
    }

    @Test
    public void isStatic_true() throws Exception {
        assertTrue("Expected access to be static.", method(ACC_STATIC).isStatic());
    }

    @Test
    public void isStatic_false() throws Exception {
        assertFalse("Expected access to not be static.", method(ACC_ABSTRACT).isStatic());
    }

}
