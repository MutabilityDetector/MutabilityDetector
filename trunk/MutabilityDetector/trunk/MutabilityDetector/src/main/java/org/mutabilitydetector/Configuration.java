package org.mutabilitydetector;

import static com.google.common.collect.Iterables.tryFind;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

public final class Configuration {

	public final Set<AnalysisResult> hardcodedResults;

	public Configuration(Set<AnalysisResult> predefinedResults) {
		this.hardcodedResults = ImmutableSet.<AnalysisResult>copyOf(predefinedResults);
	}
	
	public static final Configuration NO_CONFIGURATION = new Configuration(Collections.<AnalysisResult>emptySet());

	public Optional<AnalysisResult> hardcodedResultFor(String className) {
		return tryFind(hardcodedResults, isResultFor(className));
	}

	private Predicate<AnalysisResult> isResultFor(final String className) {
		return new Predicate<AnalysisResult>() {
			@Override public boolean apply(AnalysisResult input) {
				return input.dottedClassName.equals(className);
			}
		};
	}
	
}