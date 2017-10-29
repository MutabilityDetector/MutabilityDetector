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


import com.google.common.collect.Ordering;
import org.mutabilitydetector.AnalysisError;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.misc.TimingUtil;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;

@Immutable
public final class SessionResultsFormatter {

    private final boolean verbose;
    private final boolean showSummary;
    private final ReportMode reportMode;
    private final Collection<Dotted> classesToReport;
    private final BatchAnalysisOptions options;
    private final TimingUtil timingUtil;
    
    public SessionResultsFormatter(BatchAnalysisOptions options, ClassListReaderFactory readerFactory, TimingUtil timingUtil) {
        this.options = options;
        this.verbose = options.verbose();
        this.showSummary = options.showSummary();
        this.reportMode = options.reportMode();
        this.classesToReport = getClassesToReport(options.isUsingClassList(), readerFactory);
        this.timingUtil = timingUtil;
    }

    public StringBuilder format(Iterable<AnalysisResult> results, Iterable<AnalysisError> errors) {
        StringBuilder output = new StringBuilder();

        appendErrors(errors, output);
        appendAnalysisResults(results, output);

        return output;
    }

    private Collection<Dotted> getClassesToReport(boolean isUsingClassList, ClassListReaderFactory readerFactory) {
        return isUsingClassList ? readerFactory.createReader().classListToReport() : Collections.<Dotted> emptySet();
    }

    private void appendErrors(Iterable<AnalysisError> errors, StringBuilder output) {

        if (!options.reportErrors()) return;

        for (AnalysisError error : errors) {

            String message = String.format("Error while running %s on class %s.%n", error.checkerName, error.onClass);
            output.append(message);
            if (verbose) {
                String description = String.format("\t%s%n", error.description);
                output.append(description);
            }
        }

    }

    private void appendAnalysisResults(Iterable<AnalysisResult> results, StringBuilder output) {
        List<AnalysisResult> sortedList = sortByClassname(results);

        int total = 0;
        int totalNotImmutable = 0;
        for (AnalysisResult result : sortedList) {
            IsImmutable isImmutable = result.isImmutable;
            if (isImmutable.equals(NOT_IMMUTABLE)) {
                totalNotImmutable++;
            }
            total++;
            addResultForClass(output, result, isImmutable);
        }

        if (showSummary) {
            int totalImmutable = total - totalNotImmutable;
            appendSummaryOfResults(output, total, totalImmutable, totalNotImmutable);
        }

    }

    private void appendSummaryOfResults(StringBuilder output, int total, int totalImmutable, int totalMutable) {
        output.append(String.format("%n\t%d %s%n", total, "Total number of classes scanned."));
        output.append(String.format("\t%d %s%n", totalImmutable, "IMMUTABLE class(es)."));
        output.append(String.format("\t%d %s%n", totalMutable,  "NOT_IMMUTABLE class(es)."));
        final long processRuntime = timingUtil.getCurrentTimeMillis() - timingUtil.getVMStartTimeMillis();
        output.append(String.format("\t%d %s%n", processRuntime/1000, "seconds runtime."));
    }

    private List<AnalysisResult> sortByClassname(Iterable<AnalysisResult> sessionResults) {
        return Ordering.from(new ClassnameComparator()).sortedCopy(sessionResults);
    }

    private void addResultForClass(StringBuilder output, AnalysisResult result, IsImmutable isImmutable) {

        if (options.isUsingClassList() && !classesToReport.contains(result.className)) return;

        if (reportMode.equals(ReportMode.ALL)) {
            appendClassResult(output, result, isImmutable);
        } else if (reportMode.equals(ReportMode.IMMUTABLE)) {
            if (result.isImmutable.equals(IMMUTABLE)) {
                appendClassResult(output, result, isImmutable);
            }
        } else if (reportMode.equals(ReportMode.MUTABLE)) {
            if (result.isImmutable.equals(NOT_IMMUTABLE)) {
                appendClassResult(output, result, isImmutable);
            }
        }

    }

    private void appendClassResult(StringBuilder output, AnalysisResult result, IsImmutable isImmutable) {
        output.append(String.format("%s is %s%n", result.className, isImmutable.name()));
        if (!result.isImmutable.equals(IMMUTABLE)) {
            addReasons(result, output);
        }
    }

    private void addReasons(AnalysisResult result, StringBuilder output) {
        if (!verbose) return;

        for (MutableReasonDetail reasonDetail : result.reasons) {
            output.append(String.format("\t%10s %s%n", reasonDetail.message(), reasonDetail.codeLocation()
                    .prettyPrint()));
        }
    }

    private static final class ClassnameComparator implements Comparator<AnalysisResult>, Serializable {
        private static final long serialVersionUID = 1865374158214841422L;

        @Override
        public int compare(AnalysisResult first, AnalysisResult second) {
            return first.className.asString().compareToIgnoreCase(second.className.asString());
        }

    }

}
