package org.mutabilitydetector.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.TestUtil.unusedCheckerReasonDetails;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;

public class SessionResultsFormatterTest {

	@Test
	public void printsReadableMessage() throws Exception {
		ClassListReaderFactory unusedReaderFactory = null;
		
		BatchAnalysisOptions options = mock(BatchAnalysisOptions.class);
		when(options.reportMode()).thenReturn(ReportMode.ALL);
		when(options.isUsingClassList()).thenReturn(false);
		
		IAnalysisSession analysisSession = mock(IAnalysisSession.class);
		Collection<AnalysisResult> analysisResults = Arrays.asList(
				new AnalysisResult("a.b.c", IsImmutable.IMMUTABLE, unusedCheckerReasonDetails()),
				new AnalysisResult("d.e.f", IsImmutable.EFFECTIVELY_IMMUTABLE, unusedCheckerReasonDetails()),
				new AnalysisResult("g.h.i", IsImmutable.NOT_IMMUTABLE, unusedCheckerReasonDetails()));
		when(analysisSession.getResults()).thenReturn(analysisResults);
		
		SessionResultsFormatter formatter = new SessionResultsFormatter(options, unusedReaderFactory);
		
		
		StringBuilder result = formatter.format(analysisSession);
		
		assertThat(result.toString(), 
				   allOf(containsString("a.b.c is IMMUTABLE\n"), 
						 containsString("d.e.f is EFFECTIVELY_IMMUTABLE\n"),
						 containsString("g.h.i is NOT_IMMUTABLE\n")));
	}
}
