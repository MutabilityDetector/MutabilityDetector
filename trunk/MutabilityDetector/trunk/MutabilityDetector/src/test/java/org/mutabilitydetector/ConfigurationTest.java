package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Map;

import org.junit.Test;
import org.mutabilitydetector.locations.Dotted;

import com.google.common.collect.ImmutableSet;

public class ConfigurationTest {

    @Test
    public void hasHardcodedResultForClass() throws Exception {
        Configuration hasIt = new DefaultConfiguration(ImmutableSet.of(AnalysisResult
                .definitelyImmutable("i.am.hardcoded")));
        Configuration doesNotHaveIt = new DefaultConfiguration(ImmutableSet.of(AnalysisResult
                .definitelyImmutable("i.am.not.the.same.hardcoded.class")));
        Dotted isHardcoded = dotted("i.am.hardcoded");
        Dotted notHardcoded = dotted("i.am.not.hardcoded");

        Map<Dotted, AnalysisResult> hardcodedResults = hasIt.hardcodedResults();
        assertThat(hardcodedResults.containsKey(isHardcoded), is(true));
        assertThat(hardcodedResults.containsKey(notHardcoded), is(false));
        assertThat(doesNotHaveIt.hardcodedResults().containsKey(notHardcoded), is(false));
    }

}
