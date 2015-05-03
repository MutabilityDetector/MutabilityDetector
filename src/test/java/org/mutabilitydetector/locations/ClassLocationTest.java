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



import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import static org.mutabilitydetector.locations.CodeLocation.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.Slashed.slashed;

import org.junit.Test;

public class ClassLocationTest {

    @Test
    public void testConstructedFromInternalTypeName() throws Exception {
        CodeLocation<ClassLocation> location = ClassLocation.fromInternalName("some/package/Class");
        assertEquals("some.package.Class", location.typeName());
    }

    @Test
    public void canConstructFromSlashed() {
        CodeLocation<ClassLocation> location = ClassLocation.from(slashed("some/package/Class"));
        assertEquals("some.package.Class", location.typeName());
    }

    @Test
    public void compareTo() throws Exception {
        CodeLocation<ClassLocation> location = fromInternalName("some/package/Class");
        ClassLocation same = fromInternalName("some/package/Class");
        assertEquals(0, location.compareTo(same));

        ClassLocation different = fromInternalName("some/different/Class");
        assertFalse(location.compareTo(different) == 0);
    }

    @Test
    public void prettyPrintShowsClassNameInDottedFormat() throws Exception {
        ClassLocation location = fromInternalName("some/package/MyClass");
        assertThat(location.prettyPrint(), is("[Class: some.package.MyClass]"));
    }

}
