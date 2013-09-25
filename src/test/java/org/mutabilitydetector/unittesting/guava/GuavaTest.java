package org.mutabilitydetector.unittesting.guava;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.addCopyMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.junit.Test;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker;
import org.mutabilitydetector.unittesting.MutabilityAsserter;
import org.mutabilitydetector.unittesting.MutabilityAssertionError;
import org.mutabilitydetector.unittesting.guava.Playground.MyAsserter;
import org.objectweb.asm.Type;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

@Immutable
final class ClassContainingImmutableGuavaCollection {
	final ImmutableCollection<String> list;
	public ClassContainingImmutableGuavaCollection() {
		list = ImmutableList.of("a", "b", "c");
	}
}

final class ImmutableClass {
	final List<String> myStrings;
	public ImmutableClass(List<String> source) {
		this.myStrings = Collections.unmodifiableList(com.google.common.collect.Lists.newArrayList(source));
		//this.myStrings = Collections.unmodifiableList(new ArrayList(source));
	}
}

public class GuavaTest {

	@Test
	public void shouldRecogniseFinalMemberAssignedGuavaCollectionAsImmutable() {
		assertImmutable(ClassContainingImmutableGuavaCollection.class);
	}

	@Test
	public void shouldRecogniseFinalMemberAssignedGuavaCollectionAsImmutable2() throws Exception {

		addCopyMethod2("java.util.List", "com.google.common.collect.Lists.newArrayList", Iterable.class);
		//addCopyMethod("java.util.List",
		//		"com.google.common.collect.Lists", "newArrayList", "(Ljava/lang/Iterable;)Ljava/util/ArrayList;");

		assertImmutable(ImmutableClass.class);
	}

	
	void addCopyMethod2(String targetClass, String method, Class argType) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		String className = method.substring(0, method.lastIndexOf("."));
		String methodName = method.substring(method.lastIndexOf(".")+1);
		Method m = Class.forName(className).getMethod(methodName, argType);
		String desc = Type.getMethodDescriptor(Type.getType(m.getReturnType()), Type.getType(argType));
		System.out.println("desc="+desc);
		addCopyMethod(targetClass, className, methodName, desc);
	}
	
	@Test
	public void shouldRecogniseFinalMemberAssignedGuavaCollectionAsImmutable3() {
		MutabilityAsserter.configured(
			new ConfigurationBuilder() { 
				@Override public void configure() {
					addCopyMethod("com.google.common.collect.Lists.newArrayList");
				}
			}
		).assertImmutable(ImmutableClass.class);
	}

	public static class MyAsserter { 
		public static final MutabilityAsserter MUTABILITY = MutabilityAsserter.configured(
				new ConfigurationBuilder() { 
					@Override public void configure() {
						addCopyMethod("com.google.common.collect.Lists.newArrayList");
					}
				});
	}

}




