/*
 *    Copyright (c) 2008-2013 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
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
