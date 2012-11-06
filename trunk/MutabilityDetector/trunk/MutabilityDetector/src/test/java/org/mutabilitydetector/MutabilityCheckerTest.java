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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.TestUtil.formatReasons;
import static org.mutabilitydetector.TestUtil.getAnalysisResult;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingMutableFieldAssigned;
import org.mutabilitydetector.benchmarks.MutableByHavingPublicNonFinalField;
import org.mutabilitydetector.benchmarks.MutableByNoCopyOfIndirectlyConstructedField;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall;
import org.mutabilitydetector.benchmarks.mutabletofield.CopyListIntoNewArrayListAndUnmodifiableListIdiom;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.mutabletofield.UnsafelyCopedCollectionFieldWithAllowedGenericType;
import org.mutabilitydetector.benchmarks.sealed.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.benchmarks.visibility.EffectivelyImmutable;
import org.mutabilitydetector.benchmarks.visibility.SafelyPublishesUsingVolatile;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

public class MutabilityCheckerTest {

    @Rule public IncorrectAnalysisRule rule = new IncorrectAnalysisRule();
    
    @Test
    public void immutableExample() throws Exception {
        assertImmutable(ImmutableExample.class);
    }
    
    @Test
    public void effectivelyImmutableByHavingNonFinalField() throws Exception {
        assertInstancesOf(EffectivelyImmutable.class, areEffectivelyImmutable());
    }
    
    @Test
    @FalsePositive("Ensures non-final fields are immediately visible")
    public void volatileAssignmentOfFieldEnsuresOtherNonFinalFieldsAreImmediatelyVisible() throws Exception {
        assertImmutable(SafelyPublishesUsingVolatile.class);
    }
    
    @Test
    public void mutableByAllowingAccessToNonFinalField() throws Exception {
        assertInstancesOf(MutableByHavingPublicNonFinalField.class, areNotImmutable());
    }

    @Test
    public void mutableByHavingMutableFieldAssigned() throws Exception {
        assertInstancesOf(MutableByHavingMutableFieldAssigned.class, areNotImmutable());
    }

    @Test
    public void mutableByHavingSetterMethod() throws Exception {
        assertInstancesOf(MutableByHavingSetterMethod.class, areNotImmutable());
    }

    @Test
    public void mutableByNoCopyOfIndirectlyConstructedField() throws Exception {
        assertInstancesOf(MutableByNoCopyOfIndirectlyConstructedField.class, areNotImmutable());
    }

    @Test
    public void immutableByCopyingMutableListIntoNewArrayListAndUnmodifiableList() throws Exception {
        assertImmutable(CopyListIntoNewArrayListAndUnmodifiableListIdiom.class);
    }

    @Test
    public void immutableByCopyingMutableListIntoNewArrayListAndUnmodifiableListInStaticMethod() throws Exception {
        assertImmutable(CopyListIntoNewArrayListAndUnmodifiableListIdiom.StaticMethodDoesTheCopying.class);
    }

    @Test
    public void mutableByNotBeingFinalClass() throws Exception {
        assertInstancesOf(MutableByNotBeingFinalClass.class, areNotImmutable());
    }
    
    @Test
    public void mutableByLettingTheThisReferenceEscapeDuringConstruction() throws Exception {
        assertInstancesOf(PassesThisReferenceToMethodCall.AsOneOfSeveralParameters.class, areNotImmutable());
    }

    @Test
    public void enumTypesAreImmutable() throws Exception {
        assertImmutable(EnumType.class);
    }
    
    @Test
    public void unsafelyCopiedCollectionsAreStillMutableEvenIfElementTypeIsAllowed() throws Exception {
        assertInstancesOf(UnsafelyCopedCollectionFieldWithAllowedGenericType.class, areNotImmutable());
    }

    @Test
    public void onlyOneReasonIsRaisedForAssigningAbstractTypeToField() throws Exception {
        AnalysisResult analysisResult = getAnalysisResult(MutableByAssigningAbstractTypeToField.class);
        Collection<MutableReasonDetail> reasons = analysisResult.reasons;
        assertThat(formatReasons(reasons), reasons.size(), is(1));

        Reason reason = reasons.iterator().next().reason();
        assertThat(reason, CoreMatchers.<Reason> is(ABSTRACT_TYPE_TO_FIELD));
    }

}
