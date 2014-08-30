package org.mutabilitydetector.checkers.info;

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



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.TestUtil.sessionCheckerRunner;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.types.ConcreteType;
import org.mutabilitydetector.benchmarks.types.InterfaceType;

public class TypeStructureInformationTest {

    @Test
    public void isAbstractReturnsTrueForAbstractType() throws Exception {
        TypeStructureInformation info = new TypeStructureInformation(sessionCheckerRunner());
        assertTrue(info.isTypeAbstract(dotted(AbstractType.class.getName())));
    }

    @Test
    public void isAbstractReturnsFalseForConcreteType() throws Exception {
        TypeStructureInformation info = new TypeStructureInformation(sessionCheckerRunner());
        assertFalse(info.isTypeAbstract(dotted(ConcreteType.class.getName())));
    }

    @Test
    public void isInterfaceReturnsTrueForInterfaceType() throws Exception {
        TypeStructureInformation info = new TypeStructureInformation(sessionCheckerRunner());
        assertTrue(info.isTypeInterface(dotted(InterfaceType.class.getName())));
    }

    @Test
    public void isInterfaceReturnsFalseForConcreteType() throws Exception {
        TypeStructureInformation info = new TypeStructureInformation(sessionCheckerRunner());
        assertFalse(info.isTypeInterface(dotted(ConcreteType.class.getName())));
    }
}
