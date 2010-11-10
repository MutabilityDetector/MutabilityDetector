/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.junit.assertionbenchmarks;

//import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssert;


public class SubjectClassTest {

//	@Test @Ignore
	public void testAssertSubjectClassIsImmutable() throws Exception {
		MutabilityAssert.assertImmutable(SubjectClass.class);
	}
	
	public static void main(String[] args) throws Exception {
		new SubjectClassTest().testAssertSubjectClassIsImmutable();
	}
	
}
