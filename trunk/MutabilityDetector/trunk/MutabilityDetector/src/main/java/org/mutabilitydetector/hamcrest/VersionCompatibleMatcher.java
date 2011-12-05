package org.mutabilitydetector.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * This class is... unfortunate.
 * 
 * There's a bit of a problem going on in terms of JUnit and Hamcrest compatibility. JUnit is currently sticking with
 * version 1.1 because upgrading the version they use will cause compile errors for their users. Version 1.2+ of
 * hamcrest has many goodies I do not want to give up. Particularly the ability to describe a mismatch (this is a big
 * thing for explaining the reasons why a class is not immutable). Unfortunately, if a user of JUnit uses the bundled
 * hamcrest 1.1, it will fail with a {@link NoSuchMethodError} on the aforementioned missing
 * {@link Matcher#describeMismatch(Object, org.hamcrest.Description)} method. This class is used to reflectively
 * determine if, at runtime, the given matcher has such a method or not, delegating if it does, doing something else
 * appropriate if it doesn't.
 * 
 * This class probably does {@link Object#equals(Object)} rather badly.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 */
public class VersionCompatibleMatcher<T> extends BaseMatcher<T> {

    private final Matcher<T> delegate;

    private VersionCompatibleMatcher(Matcher<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean matches(Object item) {
       return delegate.matches(item);
    }
    
    @Override
    public void describeMismatch(Object item, Description description) {
        delegate.describeMismatch(item, description);
    }

    @Override
    public void describeTo(Description description) {
        delegate.describeTo(description);
    }

    public static <DELEGATE_TYPE> VersionCompatibleMatcher<DELEGATE_TYPE> compatibleMatcherFor(Matcher<DELEGATE_TYPE> delegate) {
        return new VersionCompatibleMatcher<DELEGATE_TYPE>(delegate);
    }
    
    @Override
    public boolean equals(Object obj) {
        return this == obj || delegatesAreEqual(obj);
    }

    private boolean delegatesAreEqual(Object obj) {
        if (getClass() == obj.getClass()) {
            return delegate.equals(((VersionCompatibleMatcher<?>)obj).delegate);
        } else {
            return delegate.equals(obj);
        }
    }
    
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

}
