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

import java.util.Collection;

public interface ClassListToReportCollector {

	/**
	 * 
	 * @return collection of class names
	 * @throws ClassListException
	 */
	Collection<String> classListToReport();

}