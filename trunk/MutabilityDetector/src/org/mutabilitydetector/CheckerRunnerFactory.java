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

import com.google.classpath.ClassPath;

public class CheckerRunnerFactory implements ICheckerRunnerFactory {

	private final ClassPath classpath;

	public CheckerRunnerFactory(ClassPath classpath) {
		this.classpath = classpath;
	}
	
	@Override
	public CheckerRunner createRunner() {
		return new CheckerRunner(classpath);
	}

}
