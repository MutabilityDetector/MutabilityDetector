package se.mutabilitydetector.benchmarks;

public final class MutableByHavingSetterMethod {

	@SuppressWarnings("unused")
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNameIndirectly(String name) {
		this.setName(name);
	}
	
}
