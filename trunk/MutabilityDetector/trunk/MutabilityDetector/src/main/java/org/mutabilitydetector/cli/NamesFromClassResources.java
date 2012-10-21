package org.mutabilitydetector.cli;

import static com.google.common.base.Predicates.containsPattern;
import static java.util.Arrays.asList;
import static org.mutabilitydetector.locations.ClassNameConverter.TO_DOTTED_STRING;
import static org.mutabilitydetector.locations.Dotted.STRING_NAME_TO_DOTTED;

import java.util.List;

import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.FluentIterable;

public final class NamesFromClassResources {

	private final String classNameRegex;

    public NamesFromClassResources(String toAnalyseRegex) {
        this.classNameRegex = toAnalyseRegex;
    }

    public List<Dotted> asDotted(String[] findResources) {
		return FluentIterable.from(asList(findResources))
				.transform(TO_DOTTED_STRING)
				.filter(containsPattern(classNameRegex))
				.transform(STRING_NAME_TO_DOTTED).toImmutableList();
	}

}