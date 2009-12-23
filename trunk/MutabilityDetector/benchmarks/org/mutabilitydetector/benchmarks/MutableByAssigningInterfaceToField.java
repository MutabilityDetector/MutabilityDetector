package org.mutabilitydetector.benchmarks;

public class MutableByAssigningInterfaceToField {

	private CharSequence name;

	public MutableByAssigningInterfaceToField(CharSequence name) {
		this.name = name;
	}
	
	public CharSequence getName() {
		return name;
	}
	

}
