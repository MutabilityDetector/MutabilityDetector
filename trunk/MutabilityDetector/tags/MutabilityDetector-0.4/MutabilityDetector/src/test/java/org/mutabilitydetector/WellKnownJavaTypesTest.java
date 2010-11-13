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

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.matchers.MutabilityMatchers.areNotImmutable;

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
		assertInstancesOf(BigDecimal.class, areImmutable());
	}
	
	@Ignore("Not final" +
			"Published fields can be reassigned" +
			"Reassigning field" +
			"Mutable type to field (primitive array)" +
			"Field which is a mutable type")
	@Test public void BigInteger() {
		assertInstancesOf(BigInteger.class, areImmutable());
	}
	
	@Ignore("Mutable type to field (primitive array)" +
			"Field which is a mutable type")
	@Test public void String() {
		assertInstancesOf(String.class, areImmutable());
	}
	
	@Test public void Integer() {
		assertInstancesOf(Integer.class, areImmutable());
	}
	
	@Test public void Array() {
		assertInstancesOf(Array.class, areImmutable());
	}
	
	@Test public void primitive_int() {
		assertInstancesOf(int.class, areImmutable());
	}
	
	@Test public void Date() {
		assertInstancesOf(Date.class, areNotImmutable());
	}
	
}