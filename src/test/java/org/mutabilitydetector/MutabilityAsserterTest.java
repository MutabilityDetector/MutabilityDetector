/*
 *    Copyright (c) 2008-2013 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector;

import java.util.Date;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.mutabilitydetector.unittesting.MutabilityMatchers;

public class MutabilityAsserterTest {

    @SuppressWarnings("unused")
    private static final class HasADateField {
        private final Date date;
        public HasADateField(Date date) {
            this.date = date;
        }
        
    }
    
    @Test
    public void canBuildConfigurationForAnalysisSessionToIncludeHardcodedResults() throws Exception {
        MutabilityAsserter asserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
            @Override
            public void configure() {
                hardcodeAsDefinitelyImmutable(Date.class);
            }
        });
        
        asserter.assertImmutable(HasADateField.class);
    }

    @Test
    public void assertingOnAHardcodedResultDirectlyPerformsTheRealAnalysis() throws Exception {
        MutabilityAsserter asserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
            @Override
            public void configure() {
                hardcodeAsDefinitelyImmutable(HasADateField.class);
            }
        });
        
        asserter.assertInstancesOf(HasADateField.class, MutabilityMatchers.areNotImmutable());
    }

}
