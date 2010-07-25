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
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.ClassNameConvertor;
import org.mutabilitydetector.IAnalysisSession;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;

/**
 * Runs an analysis configured by the given classpath and options.
 * 
 * Instances of this class, along with the related analysis, are unlikely to be
 * thread safe. This class uses {@link Thread#currentThread()} to set a
 * class loader which can be used in the analysis. 
 */
public class RunMutabilityDetector implements Runnable, Callable<String> {

	private final ClassPath classpath;
	private final CommandLineOptions options;

	public RunMutabilityDetector(ClassPath classpath, CommandLineOptions options) {
		this.classpath = classpath;
		this.options = options;
	}
	
	/**
	 * Runs mutability detection, printing the results to System.out.
	 */
	@Override
	public void run() {
		StringBuilder output = getResultString();
		System.out.println(output);
	}


	/**
	 * Runs mutability detection, returning the results as a String.
	 */
	@Override
	public String call() throws Exception {
		return getResultString().toString();
	}
	
	private StringBuilder getResultString() {
		setCustomClassLoader(options);
		
		RegExpResourceFilter regExpResourceFilter = new RegExpResourceFilter(ANY, ENDS_WITH_CLASS);
		String[] findResources = classpath.findResources("", regExpResourceFilter);

		IAnalysisSession session = new AnalysisSession(classpath);
		List<String> filtered = getNamesOfClassesToAnalyse(options, findResources);
		session.runAnalysis(filtered);

		ClassListReaderFactory readerFactory = new ClassListReaderFactory(options.classListFile());
		StringBuilder output = new SessionResultsFormatter(options, readerFactory).format(session);
		return output;
	}
	
	
	private void setCustomClassLoader(CommandLineOptions options) {
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
	
	private List<String> getNamesOfClassesToAnalyse(CommandLineOptions options, String[] findResources) {
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


	public static void main(String[] args) {
		CommandLineOptions options = createOptionsFromArgs(args);
		ClassPath classpath = new ClassPathFactory().createFromPath(options.classpath());
		
		new RunMutabilityDetector(classpath, options).run();
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



}
