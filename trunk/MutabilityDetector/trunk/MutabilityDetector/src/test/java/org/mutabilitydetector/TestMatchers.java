package org.mutabilitydetector;

import static java.lang.String.format;
import static org.mutabilitydetector.TestUtil.formatReasons;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.mutabilitydetector.checkers.IMutabilityChecker;

/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

public class TestMatchers {

	
	public static Matcher<? super IMutabilityChecker> hasReasons() {
		return new TypeSafeDiagnosingMatcher<IMutabilityChecker>() {

			@Override protected boolean matchesSafely(IMutabilityChecker item, Description mismatchDescription) {
				
				mismatchDescription.appendText(" got a checker (" + item.toString() + ") containing zero reasons ");
				return !(item.reasons().isEmpty());
			}

			@Override public void describeTo(Description description) {
				description.appendText(" a checker reporting at least one reason ");
			}

		};
	}
	
	public static Matcher<? super IMutabilityChecker> hasNoReasons() {
		return new TypeSafeDiagnosingMatcher<IMutabilityChecker>() {

			@Override protected boolean matchesSafely(IMutabilityChecker checker, Description mismatchDescription) {
				String mismatch = format(" got a checker containing %d reasons, %n%s", 
						checker.reasons().size(), formatReasons(checker.reasons()));
				mismatchDescription.appendText(mismatch);
				return (checker.reasons().isEmpty());
			}

			@Override public void describeTo(Description description) {
				description.appendText(" a checker reporting zero reasons ");
			}

		};
	}
		
	
}
