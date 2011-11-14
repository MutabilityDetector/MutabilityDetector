/*
 *    Copyright (c) 2008-2011 Graham Allan
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

package org.mutabilitydetector;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mutabilitydetector.locations.ClassNameConvertor;

public class ClassNameConverterTest {

    private static final ClassNameConvertor CONVERTOR = new ClassNameConvertor();

    @Test
    public void dottedClassNamesRemainTheSame() throws Exception {
        String dotted = "some.dotted.ClassName";
        assertEquals(dotted, CONVERTOR.dotted(dotted));
    }

    @Test
    public void slashedClassNameIsReturnedDotted() throws Exception {
        String slashed = "some/slashed/ClassName";
        assertEquals("some.slashed.ClassName", CONVERTOR.dotted(slashed));
    }

    @Test
    public void dotClassSuffixIsRemoved() throws Exception {
        String dotClass = "some/slashed/ClassName.class";
        assertEquals("some.slashed.ClassName", CONVERTOR.dotted(dotClass));
    }

}
