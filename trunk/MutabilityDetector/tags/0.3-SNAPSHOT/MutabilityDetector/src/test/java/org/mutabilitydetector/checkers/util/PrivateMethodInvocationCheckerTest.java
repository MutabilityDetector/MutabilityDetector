/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;


public class PrivateMethodInvocationCheckerTest {

	@Test public void testCanQueryIfAMethodIsOnlyCalledFromTheConstructor() throws Exception {
		PrivateMethodInvocationChecker checker = PrivateMethodInvocationChecker.newChecker();
		CheckerRunner.createWithCurrentClasspath().run(checker, PrivateMethodsCalledOnlyInConstructor.class);
		
		String methodDescriptor = "privateMethod:()V";
		boolean result = checker.isPrivateMethodCalledOnlyFromConstructor(methodDescriptor);
		assertTrue("Should report private method is only called from constructor.", result);
	}
	
	@Test public void isPrivateMethodCalledOnlyFromConstructorReturnsFalseWhenPrivateMethodIsInvokedInPublicMethod() throws Exception {
		PrivateMethodInvocationChecker checker = PrivateMethodInvocationChecker.newChecker();
		CheckerRunner.createWithCurrentClasspath().run(checker, PrivateMethodsCalledOutsideConstructor.class);
		
		String methodDescriptor = "privateMethod:()V";
		boolean result = checker.isPrivateMethodCalledOnlyFromConstructor(methodDescriptor);
		assertFalse("Should report private method is only called from constructor.", result);
	}
	
	
	private static class PrivateMethodsCalledOnlyInConstructor {
		@SuppressWarnings("unused") public PrivateMethodsCalledOnlyInConstructor() {
			privateMethod();
		}

		private void privateMethod() {}
	}
	
	@SuppressWarnings("unused") private static class PrivateMethodsCalledOutsideConstructor {
		private void privateMethod() {}
		public void callsThePrivateMethod() {
			privateMethod();
		}
	}
}
