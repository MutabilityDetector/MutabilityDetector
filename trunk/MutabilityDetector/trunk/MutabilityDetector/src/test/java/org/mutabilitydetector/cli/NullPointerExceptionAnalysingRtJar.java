/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Ignore;
import org.junit.Test;

import com.google.classpath.ClassPathFactory;


public class NullPointerExceptionAnalysingRtJar {

	private final PrintStream errorStream = new PrintStream(new OutputStream() {
		@Override public void write(int b) throws IOException {
			// suppress output in tests
		}
	});
	
	@Ignore @Test public void
	checkNullPointerExceptionIsNotThrown() {
		String rtJarPath = System.getProperty("java.home") + "/lib/rt.jar";
		CommandLineOptions options = new CommandLineOptions(errorStream, "-cp", rtJarPath);
		new RunMutabilityDetector(new ClassPathFactory().createFromPath(rtJarPath), options).run();
	}
	
}
