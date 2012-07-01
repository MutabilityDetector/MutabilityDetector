package org.mutabilitydetector;

import java.util.functions.Predicate;


public class GuavaToJavaPredicate<T> implements Predicate<T> {
	private com.google.common.base.Predicate<T> guavaPredicate;

	public GuavaToJavaPredicate(com.google.common.base.Predicate<T> guavaPredicate) {
		this.guavaPredicate = guavaPredicate;
	}
	
	public static <T> Predicate<T> of(com.google.common.base.Predicate<T> guavaPredicate) {
		return new GuavaToJavaPredicate<T>(guavaPredicate);
	}

	@Override
	public boolean test(T t) {
		return guavaPredicate.apply(t);
	}
}

