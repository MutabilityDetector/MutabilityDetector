/*
 * Mutability Detector
 * 
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.ImmutableAssert.assertNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertEffectivelyImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.TestUtil.formatReasons;
import static org.mutabilitydetector.TestUtil.getAnalysisResult;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingMutableFieldAssigned;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.MutableByNoCopyOfIndirectlyConstructedField;
import org.mutabilitydetector.benchmarks.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall;
import org.mutabilitydetector.benchmarks.finalfield.HasNonFinalField;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.EnumType;

/**
 * This test acts as an overall progress checker as well as a general acceptance of the tool so far. There are several
 * classes used as micro benchmarks to tell if the tool is correct. Once the checker can correctly assess these classes
 * the tool is correct for our definition.
 * 
 * @author Graham Allan (grundlefleck@gmail.com)
 * 
 */
public class MutabilityCheckerTest {

    @Test
    public void immutableExample() throws Exception {
        assertImmutable(ImmutableExample.class);
    }
    
    @Test
    public void effectivelyImmutableByHavingNonFinalField() throws Exception {
        assertEffectivelyImmutable(HasNonFinalField.class);
    }
    
    @Test
    public void mutableByAllowingAccessToNonFinalField() throws Exception {
        assertNotImmutable(MutableByHavingPublicNonFinalField.class);
    }

    @Test
    public void mutableByAssigningAbstractTypeToField() throws Exception {
        assertNotImmutable(MutableByAssigningInterfaceToField.class);
    }

    @Test
    public void mutableByHavingMutableFieldAssigned() throws Exception {
        assertNotImmutable(MutableByHavingMutableFieldAssigned.class);
    }

    @Test
    public void mutableByHavingSetterMethod() throws Exception {
        assertNotImmutable(MutableByHavingSetterMethod.class);
    }

    @Test
    public void mutableByNoCopyOfIndirectlyConstructedField() throws Exception {
        assertNotImmutable(MutableByNoCopyOfIndirectlyConstructedField.class);
    }

    @Ignore
    @Test
    public void immutableByCopyingMutableListIntoNewArrayListAndUnmodifiableList() throws Exception {
        assertImmutable(CopyListIntoNewArrayListAndUnmodifiableListIdiom.class);
    }

    @Ignore
    @Test
    public void immutableByCopyingMutableListIntoNewArrayListAndUnmodifiableListInStaticMethod() throws Exception {
        assertImmutable(CopyListIntoNewArrayListAndUnmodifiableListIdiom.StaticMethodDoesTheCopying.class);
    }

    @Test
    public void mutableByNotBeingFinalClass() throws Exception {
        assertNotImmutable(MutableByNotBeingFinalClass.class);
    }
    
    @Test
    public void mutableByLettingTheThisReferenceEscapeDuringConstruction() throws Exception {
        assertNotImmutable(PassesThisReferenceToMethodCall.AsOneOfSeveralParameters.class);
    }

    @Test
    public void enumTypesAreImmutable() throws Exception {
        assertImmutable(EnumType.class);
    }


    @Test
    public void onlyOneReasonIsRaisedForAssigningAbstractTypeToField() throws Exception {
        AnalysisResult analysisResult = getAnalysisResult(MutableByAssigningAbstractTypeToField.class);
        Collection<CheckerReasonDetail> reasons = analysisResult.reasons;
        assertThat(formatReasons(reasons), reasons.size(), is(1));

        Reason reason = reasons.iterator().next().reason();
        assertThat(reason, CoreMatchers.<Reason> is(MutabilityReason.ABSTRACT_TYPE_TO_FIELD));
    }

}
