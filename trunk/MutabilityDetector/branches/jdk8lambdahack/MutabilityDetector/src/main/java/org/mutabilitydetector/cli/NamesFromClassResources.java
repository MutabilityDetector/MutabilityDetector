package org.mutabilitydetector.cli;

import static com.google.common.base.Predicates.containsPattern;
import static java.util.Arrays.asList;

import java.util.Arrays;

import org.mutabilitydetector.GuavaToJavaPredicate;
import org.mutabilitydetector.locations.ClassNameConverter;
import org.mutabilitydetector.locations.Dotted;

public final class NamesFromClassResources {

	private final String classNameRegex;

    public NamesFromClassResources(String toAnalyseRegex) {
        this.classNameRegex = toAnalyseRegex;
    }

    public Iterable<Dotted> asDotted(String[] findResources) {
    	return asList(findResources)
				 .map(ClassNameConverter::toDottedString)
				 .filter(GuavaToJavaPredicate.of(containsPattern(classNameRegex)))
				 .map(Dotted::from);
	}

}