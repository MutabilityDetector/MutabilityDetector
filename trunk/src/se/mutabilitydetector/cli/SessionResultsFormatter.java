package se.mutabilitydetector.cli;

import static se.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static se.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.mutabilitydetector.IAnalysisSession;
import se.mutabilitydetector.IAnalysisSession.AnalysisError;
import se.mutabilitydetector.IAnalysisSession.AnalysisResult;
import se.mutabilitydetector.IAnalysisSession.IsImmutable;
import se.mutabilitydetector.cli.CommandLineOptions.ReportMode;

public class SessionResultsFormatter {

	private boolean verbose;
	private ReportMode reportMode;

	public SessionResultsFormatter(CommandLineOptions options) {
		this.verbose = options.verbose();
		this.reportMode = options.reportMode();
	}

	public StringBuilder format(IAnalysisSession completedSession) {
		StringBuilder output = new StringBuilder();

		appendErrors(completedSession, output);
		appendAnalysisResults(completedSession, output);

		return output;
	}

	private void appendErrors(IAnalysisSession completedSession, StringBuilder output) {

		for (AnalysisError error : completedSession.getErrors()) {

			String message = String.format("Error while running %s on class %s.\n", 
					error.checkerName, error.onClass);
			output.append(message);
			if (verbose) {
				String description = String.format("\t%s\n", error.description);
				output.append(description);
			}
		}

	}

	private void appendAnalysisResults(IAnalysisSession completedSession, StringBuilder output) {
		List<AnalysisResult> sortedList = sortByClassname(completedSession.getResults());

		for (AnalysisResult result : sortedList) {
			IsImmutable isImmutable = result.isImmutable;
			addResultForClass(output, result, isImmutable);
		}
	}

	private List<AnalysisResult> sortByClassname(Collection<AnalysisResult> sessionResults) {
		List<AnalysisResult> sortedList = new ArrayList<AnalysisResult>(sessionResults);
		Collections.sort(sortedList, new ClassnameComparator());
		return sortedList;
	}

	private void addResultForClass(StringBuilder output, AnalysisResult result, IsImmutable isImmutable) {
		if (reportMode.equals(ReportMode.ALL)) {
			appendClassResult(output, result, isImmutable);
		} else if (reportMode.equals(ReportMode.IMMUTABLE)) {
			if (result.isImmutable.equals(DEFINITELY)) {
				appendClassResult(output, result, isImmutable);
			}
		} else if (reportMode.equals(ReportMode.MUTABLE)) {
			if (result.isImmutable.equals(DEFINITELY_NOT)) {
				appendClassResult(output, result, isImmutable);
			}
		}

	}

	private void appendClassResult(StringBuilder output, AnalysisResult result, IsImmutable isImmutable) {
		output.append(String.format("%s is %s\n", result.dottedClassName, isImmutable.name()));
		if (!result.isImmutable.equals(DEFINITELY)) {
			addReasons(result, output);
		}
	}

	private void addReasons(AnalysisResult result, StringBuilder output) {
		if (!verbose)
			return;

		for (String reason : result.reasons) {
			output.append(String.format("\t%10s\n", reason));
		}
	}

	private static final class ClassnameComparator implements Comparator<AnalysisResult> {

		@Override
		public int compare(AnalysisResult o1, AnalysisResult o2) {
			return o1.dottedClassName.compareToIgnoreCase(o2.dottedClassName);
		}

	}

}
