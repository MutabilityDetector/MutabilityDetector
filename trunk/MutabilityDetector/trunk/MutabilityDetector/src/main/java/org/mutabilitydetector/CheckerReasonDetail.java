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

import static java.lang.Integer.toHexString;
import static java.lang.String.format;

import org.mutabilitydetector.locations.CodeLocation;

public class CheckerReasonDetail {

	private final String message;
	private final CodeLocation<?> location;
	private final Reason reason;
	
	public CheckerReasonDetail(String message, CodeLocation<?> location, Reason reason) {
		this.message = message;
		this.location = location;
		this.reason = reason;

	}

	public Reason reason() { return reason;}
	
	public CodeLocation<?> codeLocation() { return location; }
	
	public String message() { return message; }
	
	@Override
	public String toString() {
		return format("CheckerReasonDetail@%s[%s, %s, %s]", toHexString(hashCode()), message, reason, location);
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CheckerReasonDetail other = (CheckerReasonDetail) obj;
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (reason == null) {
			if (other.reason != null) {
				return false;
			}
		} else if (!reason.equals(other.reason)) {
			return false;
		}
		return true;
	}
	
}
