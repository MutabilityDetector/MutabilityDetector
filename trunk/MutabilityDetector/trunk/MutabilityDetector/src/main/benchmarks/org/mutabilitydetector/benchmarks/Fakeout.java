package org.mutabilitydetector.benchmarks;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Benchmark discussed on the project lombok mailing list, <a href="http://groups.google.com/group/project-lombok/browse_thread/thread/553c771b5006e18f/0393e4c1ae41099c"
 * > here</a>
 * 
 */
public final class Fakeout {
	private static final Map<Fakeout, Integer> map = new IdentityHashMap<Fakeout, Integer>();

	public Fakeout() {
		map.put(this, 0);
	}

	public void setAge(int age) {
		map.put(this, age);
	}

	public int getAge() {
		return map.get(this);
	}
}