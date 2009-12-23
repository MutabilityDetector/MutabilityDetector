package se.mutabilitydetector;

import se.mutabilitydetector.IAnalysisSession.IsImmutable;

public class TestUtil {
	public static IsImmutable getResultOfAnalysis(Class<?> toAnalyse) {
		IsImmutable result = new AnalysisSession().isImmutable(toAnalyse.getName());
		return result;
	}
}
