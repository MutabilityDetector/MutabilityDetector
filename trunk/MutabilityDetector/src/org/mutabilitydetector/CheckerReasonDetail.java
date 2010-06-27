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

public class CheckerReasonDetail {

	private final String message;
	private final SourceLocation location;
	private final Reason reason;
	
	public CheckerReasonDetail(String message, SourceLocation location, Reason reason) {
		this.message = message;
		this.location = location;
		this.reason = reason;

	}

	public Reason reason() { return reason;}
	
	public SourceLocation sourceLocation() { return location; }
	
	public String message() { return message; }
	
}
