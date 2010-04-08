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

import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.ClassNameConvertor;


import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;

public class RunMutabilityDetector {

	public static void main(String[] args) {
		CommandLineOptions options = createOptionsFromArgs(args);
		ClassPath cp = new ClassPathFactory().createFromPath(options.classpath());

		RegExpResourceFilter regExpResourceFilter = new RegExpResourceFilter(ANY, ENDS_WITH_CLASS);
		String[] findResources = cp.findResources("", regExpResourceFilter);

		setCustomClassLoader(options);

		AnalysisSession session = new AnalysisSession(cp);

		List<String> filtered = getNamesOfClassesToAnalyse(options, findResources);

		session.runAnalysis(filtered);

		StringBuilder output = new SessionResultsFormatter(options).format(session);
		System.out.println(output);

	}

	private static CommandLineOptions createOptionsFromArgs(String[] args) {
		try {
			CommandLineOptions options = new CommandLineOptions(args);
			return options;
		} catch (Throwable e) {
			System.out.println("Exiting...");
			System.exit(1);
			return null; // impossible statement
		}
	}

	private static void setCustomClassLoader(CommandLineOptions options) {
		String[] classPathUrls = options.classpath().split(":");

		List<URL> urlList = new ArrayList<URL>();

		for (String classPathUrl : classPathUrls) {
			try {
				URL toAdd = new File(classPathUrl).toURI().toURL();
				urlList.add(toAdd);
			} catch (MalformedURLException e) {
				System.err.printf("Classpath option %s is invalid.", classPathUrl);
			}
		}
		ClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[] {}));
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	private static List<String> getNamesOfClassesToAnalyse(CommandLineOptions options, String[] findResources) {
		List<String> filtered = new ArrayList<String>();
		List<String> classNames = new ArrayList<String>();
		classNames.addAll(Arrays.asList(findResources));
		String matcher = options.match();
		for (String className : classNames) {

			String dottedClassName = new ClassNameConvertor().dotted(className);
			if (Pattern.matches(matcher, dottedClassName)) {
				filtered.add(className);
			}
		}
		return filtered;
	}

}
