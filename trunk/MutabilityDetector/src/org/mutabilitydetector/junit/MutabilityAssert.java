/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.junit;

import static java.lang.String.format;

import java.util.Collection;

import junit.framework.Assert;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class MutabilityAssert {

	private static class AnalysisSessionHolder {
		static final IAnalysisSession assertionAnalysisSession = AnalysisSession.createWithCurrentClassPath();
	}
	
	public static void assertImmutable(Class<?> expectedImmutableClass) {
		String className = expectedImmutableClass.getName();
		AnalysisResult analysisResult = getResultFor(className);
		
		StringBuilder message = new StringBuilder();
		String simpleName = expectedImmutableClass.getSimpleName();
		message.append(format("Expected %s to be %s immutable. Was: %s immutable.%n", 
				simpleName, IsImmutable.DEFINITELY, analysisResult.isImmutable.toString()));
		formatReasons(analysisResult.reasons, message);
		
		Assert.assertTrue(message.toString(), IsImmutable.DEFINITELY == analysisResult.isImmutable);
	}

	private static AnalysisResult getResultFor(String className) {
		AnalysisResult analysisResult = AnalysisSessionHolder.assertionAnalysisSession.resultFor(className);
		return analysisResult;
	}

	public static String formatReasons(Collection<CheckerReasonDetail> reasons) {
		return formatReasons(reasons, new StringBuilder());
	}
	
	private static String formatReasons(Collection<CheckerReasonDetail> reasons, StringBuilder builder) {
		builder.append(format("Reasons:%n"));
		for(CheckerReasonDetail reason: reasons) {
			builder.append(format("%s%n", reason.message()));
		}
		return builder.toString();
	}

	public static void assertImmutableStatusIs(IsImmutable expected, Class<?> forClass) {
		AnalysisResult analysisResult = getResultFor(forClass.getName());
		Assert.assertEquals(expected, analysisResult.isImmutable);
	}
	

}
