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

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertThat;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

public class WellKnownJavaTypesTest {
	
	@Ignore("Not final " +
			"Reassigned field " +
			"Mutable type to field (BigInteger, String)")
	@Test public void BigDecimal() {
		assertThat(BigDecimal.class).isImmutable();
	}
	
	
	@Ignore("Not final" +
			"Published fields can be reassigned" +
			"Reassigning field" +
			"Mutable type to field (primitive array)" +
			"Field which is a mutable type")
	@Test public void BigInteger() {
		assertThat(BigInteger.class).isImmutable();
	}
	
	@Ignore("Mutable type to field (primitive array)" +
			"Field which is a mutable type")
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