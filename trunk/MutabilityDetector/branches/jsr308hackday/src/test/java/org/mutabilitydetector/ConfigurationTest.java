package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.ImmutableSet;

public class ConfigurationTest {

	@Test
	public void hasHardcodedResultForClass() throws Exception {
		Configuration hasIt = new Configuration(ImmutableSet.of(AnalysisResult.definitelyImmutable("i.am.hardcoded")));
		Configuration doesNotHaveIt = new Configuration(ImmutableSet.of(AnalysisResult.definitelyImmutable("i.am.not.the.same.hardcoded.class")));
		Dotted isHardcoded = dotted("i.am.hardcoded");
		Dotted notHardcoded = dotted("i.am.not.hardcoded");
		
		
		assertThat(hasIt.hardcodedResultFor(isHardcoded).isPresent(), is(true));
		assertThat(hasIt.hardcodedResultFor(notHardcoded).isPresent(), is(false));
		assertThat(doesNotHaveIt.hardcodedResultFor(notHardcoded).isPresent(), is(false));
	}

}
