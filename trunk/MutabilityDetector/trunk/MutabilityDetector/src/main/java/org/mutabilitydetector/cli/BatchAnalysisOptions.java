package org.mutabilitydetector.cli;

import java.io.File;

import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;

public interface BatchAnalysisOptions {

    public abstract String classpath();

    public abstract String match();

    public abstract boolean verbose();

    public abstract ReportMode reportMode();

    public abstract File classListFile();

    public abstract boolean isUsingClassList();

    public abstract boolean reportErrors();

}
