package org.mutabilitydetector.unittesting.guava;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

@Immutable
final class ClassContainingImmutableCollection {
	final ImmutableCollection<String> list;
	public ClassContainingImmutableCollection() {
		list = ImmutableList.of("a", "b", "c");
	}
}

public class GuavaTest {

	@Test (expected=MutabilityAssertionError.class)
	public void shouldRecogniseFinalMemberAssignedGuavaCollectionAsImmutable() {
		assertImmutable(ClassContainingImmutableCollection.class);
	}

}




