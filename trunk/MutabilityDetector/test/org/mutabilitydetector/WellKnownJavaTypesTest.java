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

import static org.mutabilitydetector.junit.MutabilityAssert.assertThat;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

public class WellKnownJavaTypesTest {
	
	@Ignore
	@Test public void BigDecimal() {
		assertThat(BigDecimal.class).isImmutable();
	}
	
	@Ignore
	@Test public void String() {
		assertThat(String.class).isImmutable();
	}
	
	@Test public void Integer() {
		assertThat(Integer.class).isImmutable();
	}
	
	@Test public void Array() {
		assertThat(Array.class).isImmutable();
	}
	
	@Test public void primitive_int() {
		assertThat(int.class).isImmutable();
	}
	
	@Test public void Date() {
		assertThat(Date.class).isNotImmutable();
	}
	
}