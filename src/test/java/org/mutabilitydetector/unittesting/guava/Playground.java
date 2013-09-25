package org.mutabilitydetector.unittesting.guava;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

/*
 * AllChecksRunner
 * - MutableTypeToFieldFieldChecker
 * - @L96 CollectionTypeWrappedInUnmodifiableIdiomChecker(
                            fieldInsnNode, typeAssignedToField).checkWrappedInUnmodifiable(
 */

@Immutable
final class ClassContainingImmutableJavaCollection {
	final Collection<String> listy;
	public ClassContainingImmutableJavaCollection(List<String> arg) {
		listy = Collections.unmodifiableList(new ArrayList<String>(arg));
	}
}

@Immutable
final class ClassContainingImmutableGuavaCollection2 {
	final ImmutableCollection<String> list;
	public ClassContainingImmutableGuavaCollection2(List<String> arg) {
		list = ImmutableList.copyOf(arg);
	}
}

public class Playground {

	@Test
	public void shouldRecogniseFinalMemberAssignedJavaCollectionAsImmutable() {
		assertImmutable(ClassContainingImmutableJavaCollection.class);
	}

	@Test
	public void shouldRecogniseFinalMemberAssignedGuavaCollectionAsImmutable() {
		assertImmutable(ClassContainingImmutableGuavaCollection2.class);
	}


	@Test
	public void should_see_guava_immutable_collections_as_immutable_when_we_add_exception() {
		MyAsserter.MUTABILITY.assertImmutable(ClassContainingImmutableGuavaCollection2.class);
	}

	public static class MyAsserter { 
		public static final MutabilityAsserter MUTABILITY = MutabilityAsserter.configured(
				new ConfigurationBuilder() { 
					@Override public void configure() {
						hardcodeAsDefinitelyImmutable(ImmutableList.class); 
						hardcodeAsDefinitelyImmutable(ImmutableCollection.class);
					}
				});
	}

}
