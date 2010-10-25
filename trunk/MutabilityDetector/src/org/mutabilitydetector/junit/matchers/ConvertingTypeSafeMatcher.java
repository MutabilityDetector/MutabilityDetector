/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.junit.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public abstract class ConvertingTypeSafeMatcher<FROM, TO> extends TypeSafeDiagnosingMatcher<FROM> {

	protected boolean matchesSafely(FROM item, Description mismatchDescription) {
		return matchesConverted(convertTo(item), mismatchDescription);
	};
	
	public abstract boolean matchesConverted(TO item, Description mismatchDescription);
	public abstract TO convertTo(FROM from);

}
