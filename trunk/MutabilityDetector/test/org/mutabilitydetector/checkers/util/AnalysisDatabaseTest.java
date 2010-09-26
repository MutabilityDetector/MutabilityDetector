/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.PRIVATE_METHOD_INVOCATION;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInfo;

public class AnalysisDatabaseTest {

	private AnalysisDatabase db;
	
	@Before public void setUp() {
		db = newAnalysisDatabase(createWithCurrentClasspath());
	}

	@Test public void canRequestPrivateMethodInvocationInformation() throws Exception {
		PrivateMethodInvocationInfo info = db.requestInformation(PRIVATE_METHOD_INVOCATION);
		assertNotNull("Should return an instance of requested information.", info);
	}
	
	@Test public void sameInstanceOfPrivateMethodInvocationInformationIsUsed() throws Exception {
		PrivateMethodInvocationInfo info = db.requestInformation(PRIVATE_METHOD_INVOCATION);
		assertSame("Should be the same instance from each invocation.", info, db.requestInformation(PRIVATE_METHOD_INVOCATION));
	}
	
}
