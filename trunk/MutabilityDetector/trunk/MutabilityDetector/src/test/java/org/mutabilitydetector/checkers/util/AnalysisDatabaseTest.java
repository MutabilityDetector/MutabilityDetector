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

import static org.junit.Assert.assertSame;
import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.PRIVATE_METHOD_INVOCATION;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.TYPE_STRUCTURE;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.AnalysisInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase.InfoKey;

public class AnalysisDatabaseTest {

	private AnalysisDatabase db;
	
	@Before public void setUp() {
		ISessionCheckerRunner sessionRunner = new SessionCheckerRunner(createWithCurrentClassPath(), createWithCurrentClasspath());
		db = newAnalysisDatabase(sessionRunner);
	}

	private void assertCachedCorrectly(InfoKey<? extends AnalysisInformation> infoKey) {
		assertSame("Should be the same instance from each invocation.", 
				db.requestInformation(infoKey), 
				db.requestInformation(infoKey));
	}
	
	private void assertNotNull(InfoKey<? extends AnalysisInformation> infoKey) {
		Assert.assertNotNull("Should return an instance of requested information.", db.requestInformation(infoKey));
	}
	

	@Test public void canRequestPrivateMethodInvocationInformation() throws Exception {
		assertNotNull(PRIVATE_METHOD_INVOCATION);
	}
	
	@Test public void sameInstanceOfPrivateMethodInvocationInformationIsUsed() throws Exception {
		assertCachedCorrectly(PRIVATE_METHOD_INVOCATION);
	}

	@Test public void canRequestInformationOnTypeStructureInformation() throws Exception {
		assertNotNull(TYPE_STRUCTURE);
	}
	
	@Test public void sameInstanceOfTypeStructureInformationIsUsed() throws Exception {
		assertCachedCorrectly(TYPE_STRUCTURE);
	}
	
}
