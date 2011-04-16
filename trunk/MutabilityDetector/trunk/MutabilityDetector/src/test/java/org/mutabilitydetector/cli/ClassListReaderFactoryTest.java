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

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;

public class ClassListReaderFactoryTest {

	private File classListFile;

	@Test
	public void defaultReturnedReaderIsPlainTextClassListReader() throws Exception {
		classListFile = File.createTempFile("somePlainTextClassListFile", "noFileExtension");

		ClassListToReportCollector collector = new ClassListReaderFactory(classListFile).createReader();
		
		assertTrue("Should be a plain text reader.", collector instanceof PlainTextClassListToReportReader);
	}
	
	@Test
	public void returnsAPlainTextReaderWhenFileExtensionIsDotTxt() throws Exception {
		classListFile = File.createTempFile("somePlainTextClassListFile", ".txt");

		ClassListToReportCollector collector = new ClassListReaderFactory(classListFile).createReader();
		
		assertTrue("Should be a plain text reader.", collector instanceof PlainTextClassListToReportReader);		
	}
	
	@After public void cleanUpFileOnExit() {
		if(classListFile != null) classListFile.deleteOnExit();
	}
}
