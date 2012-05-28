package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class ConfigurationTest {

	@Test
	public void hasHardcodedResultForClass() throws Exception {
		Configuration hasIt = new Configuration(ImmutableSet.of(AnalysisResult.definitelyImmutable("i.am.hardcoded")));
		Configuration doesNotHaveIt = new Configuration(ImmutableSet.of(AnalysisResult.definitelyImmutable("i.am.not.the.same.hardcoded.class")));
		
		assertThat(hasIt.hardcodedResultFor("i.am.hardcoded").isPresent(), is(true));
		assertThat(hasIt.hardcodedResultFor("i.am.not.hardcoded").isPresent(), is(false));
		assertThat(doesNotHaveIt.hardcodedResultFor("i.am.hardcoded").isPresent(), is(false));
	}

}
