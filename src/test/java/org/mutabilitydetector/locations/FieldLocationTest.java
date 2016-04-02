package org.mutabilitydetector.locations;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.from;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.CodeLocation.FieldLocation;
import static org.mutabilitydetector.locations.CodeLocation.FieldLocation.fieldLocation;
import static org.mutabilitydetector.locations.Dotted.dotted;

public class FieldLocationTest {

    @Test
    public void hasFieldNameAndNameOfTypeContainingField() throws Exception {
        FieldLocation fieldLocation = FieldLocation.fieldLocation("myFieldName",
                ClassLocation.fromInternalName("a/b/MyClass"));
        assertThat(fieldLocation.fieldName(), is("myFieldName"));
        assertThat(fieldLocation.typeName(), is("a.b.MyClass"));
    }

    @Test
    public void comparesToOtherFieldLocationsSortingAlphabeticallyByOwningTypeNameThenFieldName() throws Exception {
        FieldLocation comparing = fieldLocation("myFieldName", fromInternalName("a/b/MyClass"));
        assertThat(comparing.compareTo(fieldLocation("myFieldNamd", fromInternalName("a/b/MyClass"))),
                is(greaterThan(0)));
        assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClass"))), is(equalTo(0)));
        assertThat(comparing.compareTo(fieldLocation("myFieldNamf", fromInternalName("a/b/MyClass"))), is(lessThan(0)));

        assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClasr"))),
                is(greaterThan(0)));
        assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClass"))), is(equalTo(0)));
        assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClast"))), is(lessThan(0)));
    }

    @Test
    public void prettyPrintIncludesFieldAndClassName() throws Exception {
        FieldLocation fieldLocation = FieldLocation.fieldLocation("myFieldName",
                ClassLocation.fromInternalName("a/b/MyClass"));
        assertThat(fieldLocation.prettyPrint(), is("[Field: a.b.MyClass.myFieldName]"));
    }

    @Test
    @Ignore("Should be switched on after refactoring")
    public void lineInfoIsIncludedInPrettyPrint() {
        ClassLocation classLocation = from(dotted(SomeClass.class.getName()));
        FieldLocation fieldLocation = FieldLocation.fieldLocation("someField", classLocation);
        assertThat(fieldLocation.prettyPrint(),
                is("[Field: org.mutabilitydetector.locations.FieldLocationTest$SomeClass.someField(FieldLocationTest.java:75)]"));
    }

    private static class SomeClass {
        public int someField = 42;
    }
}
