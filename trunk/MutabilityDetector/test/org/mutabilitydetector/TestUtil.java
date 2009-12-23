package org.mutabilitydetector;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public class TestUtil {
	public static IsImmutable getResultOfAnalysis(Class<?> toAnalyse) {
		IsImmutable result = new AnalysisSession().isImmutable(toAnalyse.getName());
		return result;
	}
}
