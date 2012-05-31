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
import org.mutabilitydetector.locations.ClassNameConverter;

public class ClassNameConverterTest {

    private static final ClassNameConverter CONVERTER = new ClassNameConverter();

    @Test
    public void dottedClassNamesRemainTheSame() throws Exception {
        String dotted = "some.dotted.ClassName";
        assertEquals(dotted, CONVERTER.dotted(dotted));
    }

    @Test
    public void slashedClassNameIsReturnedDotted() throws Exception {
        String slashed = "some/slashed/ClassName";
        assertEquals("some.slashed.ClassName", CONVERTER.dotted(slashed));
    }

    @Test
    public void dotClassSuffixIsRemoved() throws Exception {
        String dotClass = "some/slashed/ClassName.class";
        assertEquals("some.slashed.ClassName", CONVERTER.dotted(dotClass));
    }
    
    @Test
	public void doesNotReplaceTheWordClassIfItAppearsInMiddleOfWord() throws Exception {
    	String containsWordClass = "some/slashed/packagewithwordclassinit/ClassName.class";
    	assertEquals("some.slashed.packagewithwordclassinit.ClassName", CONVERTER.dotted(containsWordClass));
	}

    @Test
    public void doesNotReplaceTheWordClassIfItAppearsAtStartOfPackageName() throws Exception {
    	String containsWordClass = "com/sun/org/apache/bcel/internal/classfile/AccessFlags.class";
    	assertEquals("com.sun.org.apache.bcel.internal.classfile.AccessFlags", CONVERTER.dotted(containsWordClass));
    }

}
