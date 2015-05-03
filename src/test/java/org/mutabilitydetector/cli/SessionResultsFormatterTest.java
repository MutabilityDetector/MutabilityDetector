package org.mutabilitydetector.cli;

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



import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.MutabilityReason.CAN_BE_SUBCLASSED;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.unusedMutableReasonDetails;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.from;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;

public class SessionResultsFormatterTest {

    private final ClassListReaderFactory unusedReaderFactory = null;
    
    private final String newline = System.getProperty("line.separator");

    @Test
    public void printsReadableMessage() throws Exception {
        BatchAnalysisOptions options = mock(BatchAnalysisOptions.class);
        when(options.reportMode()).thenReturn(ReportMode.ALL);
        when(options.isUsingClassList()).thenReturn(false);

        AnalysisSession analysisSession = mock(AnalysisSession.class);
        Collection<AnalysisResult> analysisResults = Arrays.asList(analysisResult("a.b.c",
                                                                                  IsImmutable.IMMUTABLE,
                                                                                  unusedMutableReasonDetails()),
                                                                   analysisResult("d.e.f",
                                                                                  IsImmutable.EFFECTIVELY_IMMUTABLE,
                                                                                  unusedMutableReasonDetails()),
                                                                   analysisResult("g.h.i",
                                                                                  IsImmutable.NOT_IMMUTABLE,
                                                                                  unusedMutableReasonDetails()));
        when(analysisSession.getResults()).thenReturn(analysisResults);

        SessionResultsFormatter formatter = new SessionResultsFormatter(options, unusedReaderFactory);

        StringBuilder result = formatter.format(analysisSession.getResults(), analysisSession.getErrors());

        assertThat(result.toString(),
                   allOf(containsString("a.b.c is IMMUTABLE" + newline),
                         containsString("d.e.f is EFFECTIVELY_IMMUTABLE" + newline),
                         containsString("g.h.i is NOT_IMMUTABLE" + newline)));
    }

    @Test
    public void verboseOutputIncludesDetailedReasonAndPrettyPrintedCodeLocation() throws Exception {
        Collection<MutableReasonDetail> reasons = Arrays.asList(newMutableReasonDetail("1st checker reason message",
                                                                                        from(slashed("path/to/MyClass")),
                                                                                        CAN_BE_SUBCLASSED),
                                                                newMutableReasonDetail("2nd checker reason message",
                                                                                        FieldLocation.fieldLocation("myField",
                                                                                                                    fromInternalName("path/to/OtherClass")),
                                                                                        MUTABLE_TYPE_TO_FIELD));

        BatchAnalysisOptions options = mock(BatchAnalysisOptions.class);
        when(options.reportMode()).thenReturn(ReportMode.ALL);
        when(options.isUsingClassList()).thenReturn(false);
        when(options.verbose()).thenReturn(true);

        AnalysisSession analysisSession = mock(AnalysisSession.class);
        Collection<AnalysisResult> analysisResults = singleton(analysisResult("a.b.c", IsImmutable.NOT_IMMUTABLE, reasons));
        when(analysisSession.getResults()).thenReturn(analysisResults);

        SessionResultsFormatter formatter = new SessionResultsFormatter(options, unusedReaderFactory);

        StringBuilder result = formatter.format(analysisSession.getResults(), analysisSession.getErrors());

        assertThat(result.toString(), 
                   containsString("a.b.c is NOT_IMMUTABLE" + newline + 
                                  "\t1st checker reason message [Class: path.to.MyClass]" + newline + 
                                  "\t2nd checker reason message [Field: myField, Class: path.to.OtherClass]" + newline));
    }
}
