package org.mutabilitydetector.benchmarks;

import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;

import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ClassWithAllPrimitives;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.checkers.IMutabilityChecker;
import org.mutabilitydetector.checkers.InherentTypeMutabilityChecker;



public class InherentTypeMutabilityCheckerTest {

	private IMutabilityChecker checker;

	@Before
	public void setUp() {
		checker = new InherentTypeMutabilityChecker();
	}

	@Test
	public void testAbstractTypesAreInherentlyMutable() throws Exception {
		new CheckerRunner(null).run(checker, AbstractType.class);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
	
	@Test
	public void testEnumTypesAreInherentlyImmutable() throws Exception {
		new CheckerRunner(null).run(checker, EnumType.class);
		assertImmutable(checker.result());
	}
	
	@Test
	public void testInterfacesAreInherentlyMutable() throws Exception {
		new CheckerRunner(null).run(checker, InterfaceType.class);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}
	
	@Test
	public void testPrimitiveTypesAreInherentlyImmutable() throws Exception {
		assertImmutableClass(ClassWithAllPrimitives.Boolean.class);
		assertImmutableClass(ClassWithAllPrimitives.Byte.class);
		assertImmutableClass(ClassWithAllPrimitives.Char.class);
		assertImmutableClass(ClassWithAllPrimitives.Short.class);
		assertImmutableClass(ClassWithAllPrimitives.Int.class);
		assertImmutableClass(ClassWithAllPrimitives.Long.class);
		assertImmutableClass(ClassWithAllPrimitives.Float.class);
		assertImmutableClass(ClassWithAllPrimitives.Double.class);
		/* @link InherentTypeMutabilityChecker#visitField(int, String, String, String, Object) 
		assertMutableClass(ClassWithAllPrimitives.Array.class); */ 
	}

	@SuppressWarnings("unused")
	private void assertMutableClass(Class<?> toCheck) {
		new CheckerRunner(null).run(checker, toCheck);
		assertDefinitelyNotImmutable(checker.result());
		assertTrue(checker.reasons().size() > 0);
	}

	private void assertImmutableClass(Class<?> toCheck) {
		new CheckerRunner(null).run(checker, toCheck);
		assertImmutable(checker.result());
	}
}
