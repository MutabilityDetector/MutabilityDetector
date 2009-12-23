package org.mutabilitydetector.benchmarks;

public final class MutableByAssigningAbstractTypeToField {

	@SuppressWarnings("unused")
	private AbstractStringContainer nameContainer;
	
	public MutableByAssigningAbstractTypeToField(AbstractStringContainer abstractNameContainer) {
		nameContainer = abstractNameContainer;
	}
	
	abstract class AbstractStringContainer {
		protected String name;
	}
	
	final class StringContainer extends AbstractStringContainer {
		
	}
}
