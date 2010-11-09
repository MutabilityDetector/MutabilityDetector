/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.asm.Opcodes.AASTORE;
import static org.mockito.asm.Opcodes.ACC_STATIC;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import org.junit.Test;
import org.mockito.asm.Opcodes;


public class AccessModifierQueryTest {

	@Test public void isPrivate_true() throws Exception {
		assertTrue("Expected access to be private.", method(ACC_PRIVATE).isPrivate());
	}
	
	@Test public void isPrivate_false() throws Exception {
		assertFalse("Expected access to not be private.", method(ACC_ABSTRACT).isPrivate());
	}
	
	@Test public void isFinal_true() throws Exception {
		assertTrue("Expected access to be final.", method(ACC_FINAL).isFinal());
	}
	
	@Test public void isFinal_false() throws Exception {
		assertFalse("Expected access to not be final.", method(ACC_ABSTRACT).isFinal());
	}
	
	@Test public void isAbstract_true() throws Exception {
		assertTrue("Expected access to be abstract.", method(ACC_ABSTRACT).isAbstract());
	}
	
	@Test public void isAbstract_false() throws Exception {
		assertFalse("Expected access to not be abstract.", method(AASTORE).isAbstract());
	}	
	
	@Test public void isInterface_true() throws Exception {
		assertTrue("Expected access to be interface.", method(Opcodes.ACC_INTERFACE).isInterface());
	}
	
	@Test public void isinterface_false() throws Exception {
		assertFalse("Expected access to not be interface.", method(ACC_ABSTRACT).isInterface());
	}
	
	@Test public void isStatic_true() throws Exception {
		assertTrue("Expected access to be static.", method(ACC_STATIC).isStatic());
	}
	
	@Test public void isStatic_false() throws Exception {
		assertFalse("Expected access to not be static.", method(ACC_ABSTRACT).isStatic());
	}
	
}
