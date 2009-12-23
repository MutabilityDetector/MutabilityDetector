package org.mutabilitydetector;

import java.util.Collection;
import java.util.Collections;

public interface IAnalysisSession {

	public IsImmutable isImmutable(String className);
	
	public void addAnalysisResult(AnalysisResult result);
	public void addAnalysisError(AnalysisError error);
	
	public void runAnalysis(Collection<String> classNames);
	
	public Collection<AnalysisResult> getResults();
	public Collection<AnalysisError> getErrors();
	
	public static enum IsImmutable {
		COULD_NOT_ANALYSE,
		DEFINITELY,
		PROBABLY,
		MAYBE,
		DEFINITELY_NOT;
	}

	public static final class AnalysisResult {
		public final String dottedClassName;
		public final IsImmutable isImmutable;
		public final Collection<String> reasons;
		public AnalysisResult(String dottedClassName, IsImmutable isImmutable, Collection<String> reasons) {
			this.dottedClassName = dottedClassName;
			this.isImmutable = isImmutable;
			this.reasons = Collections.unmodifiableCollection(reasons);
		}
	}
	
	public static final class AnalysisError {
		public final String checkerName;
		public final String description;
		public final String onClass;
		public AnalysisError(String onClass, String checkerName, String errorDescription) {
			this.onClass = onClass;
			this.checkerName = checkerName;
			this.description = errorDescription;
			
		}
	}
}


