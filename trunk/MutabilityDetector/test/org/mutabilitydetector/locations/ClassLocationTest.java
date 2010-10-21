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
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;

import org.junit.Test;
import org.mutabilitydetector.CodeLocation;


public class ClassLocationTest {

	@Test public void testConstructedFromInternalTypeName() throws Exception {
		CodeLocation location = ClassLocation.fromInternalName("some/package/Class");
		assertEquals("some.package.Class", location.typeName());
	}
	
	@Test public void compareTo() throws Exception {
		CodeLocation location = fromInternalName("some/package/Class");
		CodeLocation same = fromInternalName("some/package/Class");
		assertEquals(0, location.compareTo(same));
		
		CodeLocation different = fromInternalName("some/different/Class");
		assertFalse(location.compareTo(different) == 0);
	}
	
}
