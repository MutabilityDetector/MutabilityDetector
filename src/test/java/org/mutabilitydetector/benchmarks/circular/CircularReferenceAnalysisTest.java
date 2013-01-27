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
package org.mutabilitydetector.benchmarks.circular;

import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.MutabilityAsserter.configured;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;
import org.mutabilitydetector.Configurations;
import org.mutabilitydetector.benchmarks.circular.MultipleCircularAssignments.B;
import org.mutabilitydetector.benchmarks.circular.OuterClassWithInnerClassAsField.Inner;
import org.mutabilitydetector.benchmarks.inheritance.ImmutableSupertype;
import org.mutabilitydetector.unittesting.MutabilityAsserter;

public class CircularReferenceAnalysisTest {
    
    
    private static final MutabilityAsserter asserter = configured(Configurations.NO_CONFIGURATION);
    
    private boolean classExistsInHostJdk(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    public void stackOverflowExceptionIsNotThrown_FVDCodeBaseImpl() throws Exception {
        if (classExistsInHostJdk("com.sun.corba.se.impl.io.FVDCodeBaseImpl")) {
            asserter.assertInstancesOf(Class.forName("com.sun.corba.se.impl.io.FVDCodeBaseImpl"), areNotImmutable());
        }
    }

    @Test
    public void stackOverflowExceptionIsNotThrown_Component$NativeInLightFixer() throws Exception {
        if (classExistsInHostJdk("java.awt.Component$NativeInLightFixer")) {
            asserter.assertInstancesOf(Class.forName("java.awt.Component$NativeInLightFixer"), areNotImmutable());
        }
    }
    
    @Test
    public void stackOverflowExceptionIsNotThrown_awtExample() throws Exception {
        asserter.assertInstancesOf(MimicAwtCircularDependencies.class, areNotImmutable());
    }
    

    @Test
    public void handlesCircularReferencesInFieldAssignments() throws Exception {
        asserter.assertInstancesOf(B.class, areNotImmutable());
    }

    @Test
    public void handlesCircularReferencesAcrossSeveralHops() throws Exception {
        asserter.assertInstancesOf(SeveralHopsCircularDependency.class, areNotImmutable());
    }

    @Test
    public void handlesCircularReferencesCreatedByAssigningInnerClassToField() throws Exception {
        asserter.assertInstancesOf(Inner.class, areNotImmutable());
    }

    @Test
    public void immutableExampleIsNotIncorrectlyAnalysedAsHavingACircularDependency() throws Exception {
        asserter.assertInstancesOf(ImmutableSupertype.class, areImmutable(), allowingForSubclassing());
    }
    
    @Test
    public void classWhichAssignsSelfTypeToFieldHasACircularReference() throws Exception {
        asserter.assertInstancesOf(HasCircularReference.class, areNotImmutable());
    }
}
