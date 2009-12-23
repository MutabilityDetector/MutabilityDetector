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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.IAnalysisSession.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.ResultCalculator;


public class AllChecksRunner {

	private IsImmutable isImmutable;
	private final String toAnalyse;
	private final Collection<String> reasons = new ArrayList<String>();
	private IMutabilityCheckerFactory factory;
	private final ICheckerRunnerFactory checkerRunnerFactory;

	public AllChecksRunner(IMutabilityCheckerFactory factory, ICheckerRunnerFactory checkerRunnerFactory,
			Class<?> toAnalyse) {
		this.factory = factory;
		this.checkerRunnerFactory = checkerRunnerFactory;
		this.toAnalyse = toAnalyse.getCanonicalName();
	}

	public AllChecksRunner(MutabilityCheckerFactory factory, ICheckerRunnerFactory checkerRunnerFactory,
			String className) {
		this.factory = factory;
		this.checkerRunnerFactory = checkerRunnerFactory;
		this.toAnalyse = className;

	}

	public IsImmutable isImmutable() {
		return isImmutable;
	}

	public void runCheckers(IAnalysisSession analysisSession) {
		Map<IsImmutable, Integer> results = new HashMap<IsImmutable, Integer>();
		
		Collection<IMutabilityChecker> checkers = factory.createInstances(analysisSession);
		for (IMutabilityChecker checker : checkers) {
			checkerRunnerFactory.createRunner().run(analysisSession, checker, toAnalyse);
			IsImmutable result = checker.result();
			results.put(result, getNewCount(results, result));
			reasons.addAll(checker.reasons());
		}
		
		isImmutable = new ResultCalculator().calculateImmutableStatus(results);
			
		AnalysisResult result = new AnalysisResult(toAnalyse, isImmutable, reasons);
		analysisSession.addAnalysisResult(result);
	}

	private Integer getNewCount(Map<IsImmutable, Integer> results, IsImmutable result) {
		Integer oldCount = results.get(result);
		if(oldCount == null) oldCount = Integer.valueOf(0);
		return Integer.valueOf( oldCount + Integer.valueOf(1));
	}



	public Collection<String> reasons() {
		return reasons;
	}

}
