/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mutabilitydetector.locations.ClassNameConvertor;

/**
 * @author Graham Allan (grundlefleck@gmail.com)
 * @date 8 Apr 2010
 * 
 */
public class ClassNameConverterTest {

	private static final ClassNameConvertor CONVERTOR = new ClassNameConvertor();

	@Test
	public void dottedClassNamesRemainTheSame() throws Exception {
		String dotted = "some.dotted.ClassName";
		assertEquals(dotted, CONVERTOR.dotted(dotted));
	}
	
	@Test
	public void slashedClassNameIsReturnedDotted() throws Exception {
		String slashed = "some/slashed/ClassName";
		assertEquals("some.slashed.ClassName", CONVERTOR.dotted(slashed));
	}
	
	@Test
	public void dotClassSuffixIsRemoved() throws Exception {
		String dotClass = "some/slashed/ClassName.class";
		assertEquals("some.slashed.ClassName", CONVERTOR.dotted(dotClass));
	}
	
}
