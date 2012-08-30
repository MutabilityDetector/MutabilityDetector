package org.mutabilitydetector.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.mutabilitydetector.locations.DottedClassNameMatcher.aDottedClassNameOf;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mutabilitydetector.locations.Dotted;

public class NamesFromClassResourcesTest {

    @Test
    public void convertsSlashedClassResourceNamesToDotted() throws Exception {
        NamesFromClassResources toAnalyse = new NamesFromClassResources(".*");
        String[] resources = { "com/some/classfile/Whatever.class", };
        
        assertThat(toAnalyse.asDotted(resources), contains(aDottedClassNameOf("com.some.classfile.Whatever")));
    }
    
    @Test
    public void appliesGivenRegularExpressionToFilterOutClassNames() throws Exception {
        String[] resources = { "com/some/classfile/FindMe.class", "com/some/classfile/IgnoreThis.class" };
        NamesFromClassResources toAnalyse = new NamesFromClassResources(".*FindMe.*");
        
        assertThat(toAnalyse.asDotted(resources), 
                allOf(contains(aDottedClassNameOf("com.some.classfile.FindMe")), 
                      Matchers.<Dotted>iterableWithSize(1)));
    }
    
}
