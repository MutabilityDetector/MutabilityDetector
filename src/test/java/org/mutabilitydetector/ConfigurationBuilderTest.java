package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.AnalysisResult.definitelyImmutable;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Map;

import org.junit.Test;
import org.mutabilitydetector.locations.Dotted;

public class ConfigurationBuilderTest {

    @Test
    public void canMergeResultsFromExistingConfiguration() throws Exception {
        final Configuration existing = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(definitelyImmutable("hardcoded.in.other.Configuration"));
            }
        }.build();
        
        final Configuration current = new ConfigurationBuilder() {
            @Override public void configure() {
                mergeHardcodedResultsFrom(existing);
            }
        }.build();
        
        assertThat(current.hardcodedResults(), hasKey(dotted("hardcoded.in.other.Configuration")));
    }

    @Test
    public void mergeReplacesExistingHardcodedResultForClassWithCurrentHardcodedResult() throws Exception {
        final Configuration existing = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(definitelyImmutable("hardcoded.in.both.Configurations"));
                hardcodeResult(definitelyImmutable("only.in.existing.Configuration"));
            }
        }.build();
        
        final AnalysisResult resultInCurrentConfig = analysisResult("hardcoded.in.both.Configurations", 
                                                              IsImmutable.NOT_IMMUTABLE, 
                                                              TestUtil.unusedMutableReasonDetail());
        
        final Configuration current = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(resultInCurrentConfig);
                hardcodeResult(definitelyImmutable("only.in.current.Configuration"));

                mergeHardcodedResultsFrom(existing);
            }
        }.build();
        
        Map<Dotted, AnalysisResult> hardcodedResults = current.hardcodedResults();
        
        assertThat(hardcodedResults.size(), is(3));
        assertThat(hardcodedResults, hasEntry(dotted("hardcoded.in.both.Configurations"), resultInCurrentConfig));
        assertThat(hardcodedResults, hasEntry(dotted("only.in.existing.Configuration"), definitelyImmutable("only.in.existing.Configuration")));
        assertThat(hardcodedResults, hasEntry(dotted("only.in.current.Configuration"), definitelyImmutable("only.in.current.Configuration")));
    }
}
