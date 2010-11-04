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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class is intended to provide an extension point for being able to read
 * in class lists in different formats, e.g. XML.
 */
public class ClassListReaderFactory {

	private final File classListFile;

	public ClassListReaderFactory(File classListFile) {
		this.classListFile = classListFile;

	}

	public ClassListToReportCollector createReader() {
		String fileName = classListFile.getName();

		if (fileName.endsWith(".txt")) {
			return constructPlainTextReader();
		}

		// default case
		return constructPlainTextReader();
	}

	private ClassListToReportCollector constructPlainTextReader() {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(classListFile));
		} catch (FileNotFoundException e) {
			throw new ClassListException("Could not read class names from given file ("
					+ classListFile.getAbsolutePath() + ")", e);
		}
		return new PlainTextClassListToReportReader(br);
	}

}
