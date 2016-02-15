package org.mutabilitydetector.locations.line;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LineNumbersUtilTest {
    private static class SomeClass {
        public int someField = 42;
    }

    @Test
    public void unknownLocationStringIdEmpty() {
        String unknownString = SourceLocation.newUnknownSourceLocation().toString();
        assertThat(unknownString, is(""));
    }

    @Test
    public void classLocationIsDeterminedCorrect() {
        SourceLocation classLocation = LineNumbersUtil.newClassLocation(SomeClass.class);
        assertThat(classLocation.toString(), is("(LineNumbersUtilTest.java:29)"));
    }

    @Test
    public void initializedFieldIsDeterminedCorrect() {
        SourceLocation fieldLocation = LineNumbersUtil.newFieldLocation(SomeClass.class, "someField");
        assertThat(fieldLocation.toString(), is("(LineNumberUtilTest.java:30)"));
    }
}