/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.locations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mutabilitydetector.locations.SimpleClassLocation.fromInternalName;

import org.junit.Test;
import org.mutabilitydetector.SourceLocation;


public class SimpleClassLocationTest {

	@Test public void testConstructedFromInternalTypeName() throws Exception {
		SourceLocation location = SimpleClassLocation.fromInternalName("some/package/Class");
		assertEquals("some.package.Class", location.typeName());
	}
	
	@Test public void compareTo() throws Exception {
		SourceLocation location = fromInternalName("some/package/Class");
		SourceLocation same = fromInternalName("some/package/Class");
		assertEquals(0, location.compareTo(same));
		
		SourceLocation different = fromInternalName("some/different/Class");
		assertFalse(location.compareTo(different) == 0);
	}
	
}
