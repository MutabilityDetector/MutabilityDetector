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

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.objectweb.asm.Type.getType;

@Ignore
public class LineNumbersUtilTest {
    public static class SomeClass {
        public int someFieldOfInnerClass = 42;
    }

    private class SomePrivateNonstaticClass {
        private int somePrivateFieldOfInnerPrivateNonstaticClass = 42;
    }

    public int someField = 42;

    @Test
    public void unknownLocationToString() {
        String unknownString = SourceLocation.newUnknownSourceLocation().toString();
        assertThat(unknownString, is(""));
    }

    @Test
    public void classLocationToString() {
        SourceLocation classLocation = LineNumbersUtil.newClassLocation(getType(LineNumbersUtilTest.class));
        assertThat(classLocation.toString(), is("(LineNumbersUtilTest.java:29)"));
    }

    @Test
    public void initializedFieldOfLocationToString() {
        SourceLocation fieldLocation = LineNumbersUtil.newFieldLocation(getType(LineNumbersUtilTest.class), "someField");
        assertThat(fieldLocation.toString(), is("(LineNumbersUtilTest.java:38)"));
    }

    @Test
    public void internalClassLocationToString() {
        SourceLocation classLocation = LineNumbersUtil.newClassLocation(getType(SomeClass.class));
        assertThat(classLocation.toString(), is("(LineNumbersUtilTest.java:30)"));
    }

    @Test
    public void initializedFieldOfInnerClassLocationToString() {
        SourceLocation fieldLocation = LineNumbersUtil.newFieldLocation(getType(SomeClass.class), "someFieldOfInnerClass");
        assertThat(fieldLocation.toString(), is("(LineNumbersUtilTest.java:31)"));
    }

    @Test
    public void privateInnerClassLocationToString() {
        SourceLocation classLocation = LineNumbersUtil.newClassLocation(getType(SomePrivateNonstaticClass.class));
        assertThat(classLocation.toString(), is("(LineNumbersUtilTest.java:34)"));
    }

    @Test
    public void privateInitializedFieldOfInnerClassLocationToString() {
        SourceLocation fieldLocation = LineNumbersUtil.newFieldLocation(getType(SomePrivateNonstaticClass.class), "somePrivateFieldOfInnerPrivateNonstaticClass");
        assertThat(fieldLocation.toString(), is("(LineNumbersUtilTest.java:35)"));
    }

    @Test
    public void notInnerClassLocationToString() {
        SourceLocation classLocation = LineNumbersUtil.newClassLocation(getType(NotInnerPackagePrivateClass.class));
        assertThat(classLocation.toString(), is("(LineNumbersUtilTest.java:95)"));
    }

    @Test
    public void InitializedFieldOfNotInnerClassLocationToString() {
        SourceLocation fieldLocation = LineNumbersUtil.newFieldLocation(getType(NotInnerPackagePrivateClass.class), "someFieldOfNotInnerPackagePrivateClass");
        assertThat(fieldLocation.toString(), is("(LineNumbersUtilTest.java:96)"));
    }
}

class NotInnerPackagePrivateClass {
    public int someFieldOfNotInnerPackagePrivateClass = 42;
}