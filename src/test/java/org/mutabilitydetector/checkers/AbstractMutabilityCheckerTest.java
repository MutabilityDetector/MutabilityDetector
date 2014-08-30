package org.mutabilitydetector.checkers;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;

import org.junit.Test;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.ClassLocation;

public class AbstractMutabilityCheckerTest {

    private final AsmMutabilityChecker defaultChecker = new AbstractMutabilityChecker() {
    };

    @Test
    public void convertsTheResultOfAnalysisToCouldNotAnalyseWhenAskedToVisitAnAnalysisException() {
        defaultChecker.visit(0, 0, "some/class/ToAnalyse.class", null, null, null);
        defaultChecker.visitAnalysisException(new RuntimeException());

        assertThat(defaultChecker.result(), is(MutabilityReason.CANNOT_ANALYSE.createsResult()));
    }

    @Test
    public void providesADescriptiveReasonAfterVisitingAnAnalysisException() {
        defaultChecker.visit(0, 0, "some/clazz/ToAnalyse.class", null, null, null);
        defaultChecker.visitAnalysisException(new RuntimeException());
        MutableReasonDetail reason = newMutableReasonDetail("Encountered an unhandled error in analysis.",
                ClassLocation.fromInternalName("some/clazz/ToAnalyse.class"),
                MutabilityReason.CANNOT_ANALYSE);
        assertThat(defaultChecker.reasons(), contains(reason));
    }
}
