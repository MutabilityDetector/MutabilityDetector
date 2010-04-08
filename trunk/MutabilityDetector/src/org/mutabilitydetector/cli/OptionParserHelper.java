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
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class OptionParserHelper {
	
	private Options options;
	private String[] args;

	public OptionParserHelper(Options options, String[] args) {
		this.options = options;
		this.args = args;
	}

	public void parseOptions(ParsingAction action) {
		try {
			CommandLineParser parser = new GnuParser();
			CommandLine line = parser.parse(options, args);
			action.doParsingAction(line);
		} catch (ParseException e) {
			System.out.println("Parsing command line options failed.\nReason: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
	}
}

interface ParsingAction {
	void doParsingAction(CommandLine line);
}