package org.mutabilitydetector.unittesting.guava;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;

final class ImmutableClassContainingList {
	final List<String> list;
	final Set<String> set;
	final Map<String, String> map;
	public ImmutableClassContainingList(List<String> list, Map<String, String> map) {
		this.list = Collections.unmodifiableList(Lists.newArrayList(list));
		this.set = Collections.unmodifiableSet(Sets.newHashSet(list));
		this.map = Collections.unmodifiableMap(Maps.newHashMap(map));
	}
}
final class ImmutableClassContainingSet {
	public ImmutableClassContainingSet(List<String> source) {
	}
}
final class ImmutableClassContainingMap {
	public ImmutableClassContainingMap(Map<String, String> source) {
	}
}

public class GuavaTest {

	@Test
	public void shouldRecogniseGuavaCollectionWappedInUnmodifiableListAsImmutable() {
		MutabilityAsserter.configured(
			new ConfigurationBuilder() { 
				@Override public void configure() {
					hardcodeValidCopyMethod("java.util.List", "com.google.common.collect.Lists.newArrayList", Iterable.class);
					hardcodeValidCopyMethod("java.util.Set", "com.google.common.collect.Sets.newHashSet", Iterable.class);
					hardcodeValidCopyMethod("java.util.Map", "com.google.common.collect.Maps.newHashMap", Map.class);
					mergeHardcodedResultsFrom(Configurations.OUT_OF_THE_BOX_CONFIGURATION);
				}
			}
		).assertImmutable(ImmutableClassContainingList.class);
	}


}




