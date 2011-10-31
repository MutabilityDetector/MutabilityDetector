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

import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public enum MutabilityReason implements Reason {
	
	CANNOT_ANALYSE("Class could not be analysed. Possible reasons include not being able to load the class correctly.",
			IsImmutable.COULD_NOT_ANALYSE),
			
	ABSTRACT_TYPE_TO_FIELD("For an object to be immutable, its fields must also be immutable. "
			+ "By assigning an abstract type to a field, "
			+ "it is not known if the concrete fields supplied will be immutable or not.",
			IsImmutable.NOT_IMMUTABLE),
			
	NOT_DECLARED_FINAL("Class is not declared final. While this specific class may still be immutable, " +
			"it is recommended that the class be declared final if possible. This will allow clients to " +
			"be confident that parameters declared to be this type will indeed be of this type at runtime, " +
			"not an instance of a mutable subclass.",
			IsImmutable.EFFECTIVELY_IMMUTABLE),
			
	ABSTRACT_TYPE_INHERENTLY_MUTABLE("Abstract types (interfaces or abstract classes) are considered to be " +
			"\"Inherently Mutable\" in particular cases. Because the concrete implementation cannot be known" +
			"until compile-time, instances of abstract types could be either mutable or immutable.",
			IsImmutable.NOT_IMMUTABLE),
	
	ARRAY_TYPE_INHERENTLY_MUTABLE("Since a primitive array can be mutated after construction " +
			"(by modifying what it contains) they are inherently mutable. However, since it is possible " +
			"that a field which is an array type is never mutated after construction, it is still possible " +
			"for the containing type to be immutable.",
			IsImmutable.EFFECTIVELY_IMMUTABLE),
			
	MUTABLE_TYPE_TO_FIELD("A mutable type can be assigned to a field. Since references to the mutable field " +
			"may be kept, the containing type can be mutated after construction.",
			IsImmutable.NOT_IMMUTABLE),
			
	PUBLISHED_NON_FINAL_FIELD("Class has a published, non-final field. Fields of an immutable class may not be " +
			"reassigned after an instance is constructed. If an accessible field is not made final, it can be reassigned."
			, IsImmutable.NOT_IMMUTABLE),
			
	FIELD_CAN_BE_REASSIGNED("For a class to be immutable, fields cannot be reassigned once an instance is " +
			"constructed.",
			IsImmutable.NOT_IMMUTABLE),
			
	NULL_REASON("Placeholder reason for a null checker.", IsImmutable.COULD_NOT_ANALYSE);

	private final String description;
	private final String code;
	private final IsImmutable createsResult;

	MutabilityReason(String description, IsImmutable createsResult) {
		this.description = description;
		this.code = this.name();
		this.createsResult = createsResult;
	}
	
	@Override
	public String description() {
		return description;
	}

	@Override
	public String code() {
		return code;
	}
	
	@Override
	public IsImmutable createsResult() {
		return createsResult;
	}

}
