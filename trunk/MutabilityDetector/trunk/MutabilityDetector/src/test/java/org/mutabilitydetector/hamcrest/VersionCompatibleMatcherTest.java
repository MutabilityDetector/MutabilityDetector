package org.mutabilitydetector.hamcrest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;


public class VersionCompatibleMatcherTest {

    @Test
    public void delegatesCallToMatches() throws Exception {
        @SuppressWarnings("unchecked")
        Matcher<String> delegate = mock(Matcher.class);
        
        when(delegate.matches(any(String.class))).thenReturn(true);
        
        VersionCompatibleMatcher<String> matcher = VersionCompatibleMatcher.compatibleMatcherFor(delegate);
        matcher.matches("anything");
        
        verify(delegate).matches("anything");
        
    }

    @Test
    public void delegatesCallToDescribeMismatch() throws Exception {
        @SuppressWarnings("unchecked")
        Matcher<String> delegate = mock(Matcher.class);
        
        VersionCompatibleMatcher<String> matcher = VersionCompatibleMatcher.compatibleMatcherFor(delegate);
        StringDescription expectedDescription = new StringDescription();
        matcher.describeMismatch("anything", expectedDescription);
        
        verify(delegate).describeMismatch("anything", expectedDescription);
        
    }
    
    @Test
    public void delegatesCallToDescribeTo() throws Exception {
        @SuppressWarnings("unchecked")
        Matcher<String> delegate = mock(Matcher.class);
        
        VersionCompatibleMatcher<String> matcher = VersionCompatibleMatcher.compatibleMatcherFor(delegate);
        StringDescription expectedDescription = new StringDescription();
        matcher.describeTo(expectedDescription);
        
        verify(delegate).describeTo(expectedDescription);
    }
    
    @Test
    public void matchersWithSameDelegateAreEqual() throws Exception {
        Matcher<String> delegate = containsString("Booyah");
        Matcher<String> otherMatcher = containsString("Beeyo");
        
        VersionCompatibleMatcher<String> first = VersionCompatibleMatcher.compatibleMatcherFor(delegate);
        VersionCompatibleMatcher<String> second = VersionCompatibleMatcher.compatibleMatcherFor(delegate);
        VersionCompatibleMatcher<String> different = VersionCompatibleMatcher.compatibleMatcherFor(otherMatcher);
        
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first, delegate);
        assertEquals(different, different);
        assertFalse(first.equals(different));
        assertFalse(different.equals(first));

        assertEquals(first.hashCode(), delegate.hashCode());
        assertEquals(second.hashCode(), first.hashCode());
        assertFalse(second.hashCode() == different.hashCode());
    }
    
    @Test
    public void describesMismatchItselfIfNoSuchMethodExistsForDelegateMatcher() throws Exception {
        // how to do this?
    }
    
}
