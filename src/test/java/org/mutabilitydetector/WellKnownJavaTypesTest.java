package org.mutabilitydetector;

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
import org.junit.Rule;
import org.junit.Test;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

import javax.management.ImmutableDescriptor;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.fail;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.AllowedReason.assumingFields;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

public class WellKnownJavaTypesTest {

    @Rule public IncorrectAnalysisRule incorrectAnalysisRule = new IncorrectAnalysisRule();

    @Test
    public void Object() throws Exception {
        assertInstancesOf(Object.class, areImmutable(), allowingForSubclassing());
    }

    @Test
    @FalsePositive("Not final " + "Reassigned field " + "Mutable type to field (BigInteger, String)")
    public void BigDecimal() {
        assertInstancesOf(BigDecimal.class, areImmutable(),
                          provided(BigInteger.class).isAlsoImmutable(),
                          provided(String.class).isAlsoImmutable(),
                          allowingForSubclassing());
    }

    @Test
    @FalsePositive("Not final" + "Published fields can be reassigned"
            + "Reassigning field"
            + "Mutable type to field (primitive array)"
            + "Field which is a mutable type")
    public void BigInteger() {
        assertInstancesOf(BigInteger.class, areImmutable());
    }

    @FalsePositive("Field is not final; Field [hash] can be reassigned")
    @Test
    public void String() {
        assertInstancesOf(String.class, areImmutable(), assumingFields("value").areNotModifiedAndDoNotEscape());
    }

    @Test
    public void Integer() {
        assertInstancesOf(Integer.class, areImmutable());
    }

    @Test
    public void Array() {
        assertInstancesOf(Array.class, areImmutable());
    }

    @Test
    public void Date() {
        assertInstancesOf(Date.class, areNotImmutable());
    }

    @Test
    public void AbstractMap$SimpleImmutableEntry() {
        assertInstancesOf(AbstractMap.SimpleImmutableEntry.class, areImmutable(),
                          allowingForSubclassing(),
                          provided("K").isAlsoImmutable(),
                          provided("V").isAlsoImmutable());
    }

    @FalsePositive("Not final" + "Field hashCode reassigned" + "Field of mutable type (primitive array)")
    @Test
    public void ImmutableDescriptor() {
        assertInstancesOf(ImmutableDescriptor.class, areImmutable());
    }

    @Test
    public void Class() {
        assertInstancesOf(Class.class, areNotImmutable());
    }

    @Test
    public void URI() {
        assertInstancesOf(URI.class, areNotImmutable());
    }

    @Test
    public void Constructor() {
        assertInstancesOf(java.lang.reflect.Constructor.class, areNotImmutable());
    }

    @Test
    public void ArrayList() {
        assertInstancesOf(ArrayList.class, areNotImmutable());
    }
}
