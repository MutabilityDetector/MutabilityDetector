/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import org.mutabilitydetector.benchmarks.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.cli.CommandLineOptions;
import org.mutabilitydetector.cli.RunMutabilityDetector;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public class CheckSomeClass {

	public static void main(String[] args) {
		checkClass(MutableByHavingArrayTypeAsField.class);
	}

	private static void checkClass(Class<?> toAnalyse) {
		
		ClassPath cp = new ClassPathFactory().createFromJVM();
		CommandLineOptions options = new CommandLineOptions("-verbose", "-match", toAnalyse.getName());
		new RunMutabilityDetector(cp, options).run();
	}
	
	

}
