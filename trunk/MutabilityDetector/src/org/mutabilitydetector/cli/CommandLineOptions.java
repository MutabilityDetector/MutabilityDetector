/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

public class CommandLineOptions {

	private String classpath;
	private Options options;
	private String match;
	private boolean verbose = false;
	private ReportMode reportMode;

	private final class ParsingActionImplementation implements ParsingAction {
		public void doParsingAction(CommandLine line) {
			printHelpIfRequired(line);
			extractClasspath(line);
			extractMatch(line);
			extractVerboseOption(line);
			extractReportMode(line);
			printHelpIfNoOptionsGiven(line);
		}

	}

	public static enum ReportMode {
		ALL, IMMUTABLE, MUTABLE;

		public static String validModes() {
			StringBuilder modes = new StringBuilder();
			modes.append("[");
			for (ReportMode m : values()) {
				modes.append(m.name());
				modes.append("|");
			}
			modes.deleteCharAt(modes.length() - 1); // Remove last bar
			modes.append("]");
			return modes.toString();
		}
	}

	public CommandLineOptions(String... args) {
		options = createOptions();
		parseOptions(options, args);
	}
	
	private Options createOptions() {
		Options opts = new Options();
		createAndAddOption(opts, "path", "The classpath to be analysed by Mutability Detector", "classpath", "cp");
		createAndAddOption(opts, "regex", "A regular expression used to match class names to analyse. "
				+ "This is matched against the fully qualified class name, minus the .class suffix (i.e. it matches " +
						"against 'java.lang.Object', not 'java/lang/Object.class'). The default is '.*', meaning all " +
						"classes will be analysed.",
				"match", "m");
		opts.addOption("v", "verbose", false, "Print details of analysis and reasons for results.");
		opts.addOption("r", "report", true, "Choose what is reported from the analysis. Valid options are "
						+ ReportMode.validModes()
						+ ". If not specified, or doesn't match an available mode, defaults to 'ALL'");
		opts.addOption("h", "help", false, "print this message");
		return opts;
	}

	@SuppressWarnings("static-access")
	private static void createAndAddOption(Options opts, String argumentName, String description, String argumentFlag,
			String shortFlag) {
		Option newOption = OptionBuilder.withArgName(argumentName).hasArg().withDescription(description).withLongOpt(
				argumentFlag).create(shortFlag);
		opts.addOption(newOption);

	}

	public String classpath() {
		return classpath;
	}

	private void parseOptions(Options options, String[] args) {
		OptionParserHelper parser = new OptionParserHelper(options, args);
		try {
			parser.parseOptions(new ParsingActionImplementation());
		} catch (Exception e) {
			printHelpAndExit();
		}
	}

	private void extractReportMode(CommandLine line) {
		if (line.hasOption("r") || line.hasOption("report")) {
			String mode = line.getOptionValue("report");
			this.reportMode = ReportMode.valueOf(ReportMode.class, mode.toUpperCase());
		} else {
			this.reportMode = ReportMode.ALL;
		}
	}

	private void extractVerboseOption(CommandLine line) {
		if (line.hasOption("v") || line.hasOption("verbose")) {
			verbose = true;
		}

	}

	private void extractClasspath(CommandLine line) {
		this.classpath = line.getOptionValue("classpath", ".");
	}

	private void extractMatch(CommandLine line) {
		this.match = line.getOptionValue("match", ".*");
	}

	private void printHelpIfRequired(CommandLine line) {
		if (line.hasOption("help")) {
			printHelpAndExit();
		}

	}
	
	private void printHelpIfNoOptionsGiven(CommandLine line) {
		if(line.getOptions().length == 0) {
			printHelpAndExit();
		}
	}

	private void printHelpAndExit() {
		HelpFormatter help = new HelpFormatter();
		help.printHelp("MutabilityDetector", options);
		System.exit(0);
	}

	public String match() {
		return match;
	}

	public boolean verbose() {
		return verbose;
	}

	public ReportMode reportMode() {
		return reportMode;
	}
}
