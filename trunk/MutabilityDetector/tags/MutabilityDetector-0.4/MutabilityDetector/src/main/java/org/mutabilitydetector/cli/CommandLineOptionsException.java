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

/**
 * Used when creating the options for running MutabilityDetector.
 * 
 * May be used to indicate an error in the arguments, or to correctly terminate
 * execution.
 */
public class CommandLineOptionsException extends RuntimeException {

	private static final long serialVersionUID = -6822667773302851896L;

	public CommandLineOptionsException() {
		super();
	}

	public CommandLineOptionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandLineOptionsException(String message) {
		super(message);
	}

	public CommandLineOptionsException(Throwable cause) {
		super(cause);
	}

}
