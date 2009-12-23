package se.mutabilitydetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public class AnalysisSession implements IAnalysisSession {

	private final Map<String, AnalysisResult> analysedClasses = new HashMap<String, AnalysisResult>();
	private final List<AnalysisError> analysisErrors = new ArrayList<AnalysisError>();
	private final MutabilityCheckerFactory checkerFactory = new MutabilityCheckerFactory();
	private final CheckerRunnerFactory checkerRunnerFactory;
	private final List<String> requestedAnalysis = new ArrayList<String>();

	public AnalysisSession(ClassPath classpath) {
		checkerRunnerFactory = new CheckerRunnerFactory(classpath);
	}
	
	public AnalysisSession() {
		ClassPath classpath = new ClassPathFactory().createFromJVM();
		checkerRunnerFactory = new CheckerRunnerFactory(classpath);
	}

	@Override
	public IsImmutable isImmutable(String className) {
		AnalysisResult resultForClass = analysedClasses.get(className);
		if (resultForClass != null) {
			return resultForClass.isImmutable;
		} else {
			return requestAnalysis(className);
		}
	}

	private IsImmutable requestAnalysis(String className) {
		if (requestedAnalysis.contains(className)) {
			// isImmutable has already been called for this class, and the
			// result not yet generated
			return IsImmutable.MAYBE;
		} else {
			requestedAnalysis.add(className);
			new AllChecksRunner(checkerFactory, checkerRunnerFactory, className).runCheckers(this);
			return isImmutable(className);
		}
	}

	@Override
	public void runAnalysis(Collection<String> classNames) {
		for (String resource : classNames) {
			resource = resource.replace("/", ".");
			if(resource.endsWith(".class")) {
				resource = resource.substring(0, resource.lastIndexOf(".class"));
			}
			isImmutable(resource);
		}

	}
	
	@Override
	public void addAnalysisResult(AnalysisResult result) {
		requestedAnalysis.remove(result.dottedClassName);
		analysedClasses.put(result.dottedClassName, result);
	}
	
	@Override
	public void addAnalysisError(AnalysisError error) {
		requestedAnalysis.remove(error.onClass);
		analysisErrors.add(error);
		
	}

	@Override
	public Collection<AnalysisResult> getResults() {
		return Collections.unmodifiableCollection(analysedClasses.values());
	}



	@Override
	public Collection<AnalysisError> getErrors() {
		return Collections.unmodifiableCollection(analysisErrors);
	}

}
