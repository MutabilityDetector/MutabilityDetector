package org.mutabilitydetector.unittesting.playground;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.mutabilitydetector.unittesting.internal.CloneList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/*
 * AllChecksRunner
 * - MutableTypeToFieldFieldChecker
 * - @L96 CollectionTypeWrappedInUnmodifiableIdiomChecker(
                            fieldInsnNode, typeAssignedToField).checkWrappedInUnmodifiable(
 */

final class ClassContainingCustomCollection {
	final List<Integer> list;
	public ClassContainingCustomCollection(List<Integer> list) {
		this.list = Collections.unmodifiableList(new CloneList<Integer>(list));
	}
	
}

final class ClassContainingGuavaCollection {
	final List<String> list;
	final Set<String> set;
	final Map<String, String> map;
	public ClassContainingGuavaCollection(List<String> list, Map<String, String> map) {
		this.list = Collections.unmodifiableList(Lists.newArrayList(list));
		this.set = Collections.unmodifiableSet(Sets.newHashSet(list));
		this.map = Collections.unmodifiableMap(Maps.newHashMap(map));
	}
}


public class Playground {

	@Test
	public void shouldSupporteGuavaCollectionsOutOfTheBox() {
		assertImmutable(ClassContainingGuavaCollection.class);
	}

	@Test
	public void shouldRecogniseCustomCollectionConstructorWappedInUnmodifiableListAsImmutable() {
		MutabilityAsserter.configured(
				new ConfigurationBuilder() { 
					@Override public void configure() {
						hardcodeValidCopyMethod(List.class, 
								"org.mutabilitydetector.unittesting.playground.MyList.<init>", List.class);
						mergeHardcodedResultsFrom(Configurations.OUT_OF_THE_BOX_CONFIGURATION);
					}
				}
			).assertImmutable(ClassContainingCustomCollection.class);
	}




}
