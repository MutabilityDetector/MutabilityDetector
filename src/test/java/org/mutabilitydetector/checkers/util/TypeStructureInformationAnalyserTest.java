package org.mutabilitydetector.checkers.util;

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


import org.junit.Test;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ConcreteType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;
import org.mutabilitydetector.locations.Dotted;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.checkers.util.TypeStructureInformationAnalyser.newAnalyser;
import static org.mutabilitydetector.locations.Dotted.fromClass;

public class TypeStructureInformationAnalyserTest {

    @Test
    public void isAbstractIsTrueForAbstractType() throws Exception {
        Dotted className = fromClass(AbstractType.class);
        TypeStructureInformationAnalyser checker = newAnalyser(className);
        TestUtil.retrieveInformation(checker, AbstractType.class);

        assertTrue("Class is abstract.", checker.isAbstract());

    }

    @Test
    public void isAbstractIsFalseForConcreteType() throws Exception {
        Dotted className = fromClass(ConcreteType.class);
        TypeStructureInformationAnalyser analyser = newAnalyser(className);
        TestUtil.retrieveInformation(analyser, ConcreteType.class);

        assertFalse("Class is concrete.", analyser.isAbstract());
    }

    @Test
    public void isInterfaceIsTrueForInterfaceType() throws Exception {
        Dotted className = fromClass(InterfaceType.class);
        TypeStructureInformationAnalyser analyser = newAnalyser(className);
        TestUtil.retrieveInformation(analyser, InterfaceType.class);

        assertTrue("Type is an interface.", analyser.isInterface());
    }

    @Test
    public void isInterfaceIsFalseForConcreteType() throws Exception {
        Dotted className = fromClass(ConcreteType.class);
        TypeStructureInformationAnalyser analyser = newAnalyser(className);
        TestUtil.retrieveInformation(analyser, ConcreteType.class);

        assertFalse("Type is a concrete class.", analyser.isInterface());
    }


}
