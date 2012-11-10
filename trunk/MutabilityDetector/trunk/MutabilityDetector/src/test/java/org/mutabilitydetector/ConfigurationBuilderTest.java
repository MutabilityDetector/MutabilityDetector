package org.mutabilitydetector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;

public class ConfigurationBuilderTest {

    @Test
    public void canMergeResultsFromExistingConfiguration() throws Exception {
        final Configuration existing = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(AnalysisResult.definitelyImmutable("hardcoded.in.other.Configuration"));
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
                hardcodeResult(AnalysisResult.definitelyImmutable("hardcoded.in.both.Configurations"));
            }
        }.build();
        
        final AnalysisResult resultInCurrentConfig = analysisResult("hardcoded.in.both.Configurations", 
                                                              IsImmutable.NOT_IMMUTABLE, 
                                                              TestUtil.unusedMutableReasonDetail());
        final Configuration current = new ConfigurationBuilder() {
            @Override public void configure() {
                hardcodeResult(resultInCurrentConfig);

                mergeHardcodedResultsFrom(existing);
            }
        }.build();
        
        assertThat(current.hardcodedResults(), hasEntry(dotted("hardcoded.in.both.Configurations"),
                                                        resultInCurrentConfig));
    }
}
