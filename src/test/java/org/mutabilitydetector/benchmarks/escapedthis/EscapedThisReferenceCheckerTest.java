package org.mutabilitydetector.benchmarks.escapedthis;

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
import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsFirstOfSeveralParameters;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsLastOfSeveralParameters;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsMoreThanOneOfSeveralParameters;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsOneOfSeveralParameters;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsOneOfSeveralParametersWithOtherWeirdCode;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsParameterToPrivateMethod;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsParameterToStaticMethod;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.AsSingleParameter;
import org.mutabilitydetector.benchmarks.escapedthis.PassesThisReferenceToMethodCall.InOneConstructorButNotTheOther;
import org.mutabilitydetector.benchmarks.escapedthis.Safe.IsMutableForReassigningFieldNotForThisEscaping;
import org.mutabilitydetector.benchmarks.escapedthis.Safe.NoThisPassedToOtherObjectAsOneOfManyParametersAndDoesWeirdStuffInNewCall;
import org.mutabilitydetector.benchmarks.escapedthis.Safe.PassesThisReferenceAfterConstruction;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.AliasesThisReferenceBeforeLettingItEscape;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.PassAnonymousInnerClassWithImplicitReferenceToThis;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.PassInnerClassWithImplicitReferenceToThis;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.PassThisReferenceToParameter;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.PassThisReferenceToStaticObject;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.SaveThisReferenceAsInstanceFieldOfThisClass;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.SaveThisReferenceAsStaticFieldOfThisClass;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.SetThisReferenceAsInstanceFieldOfOtherObject;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.SetThisReferenceAsStaticFieldOfOtherClass;
import org.mutabilitydetector.benchmarks.escapedthis.Unsafe.ThisPassedToPrivateMethodWhichDoesPublishReference;
import org.mutabilitydetector.checkers.EscapedThisReferenceChecker;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;

@RunWith(Theories.class)
public class EscapedThisReferenceCheckerTest {

    @Rule public MethodRule rule = new IncorrectAnalysisRule();
    
    @Test
    public void immutableExampleIsNotRenderedMutable() throws Exception {
        assertThisDoesNotEscape(ImmutableExample.class);
    }

    @Test
    public void thisReferenceEscapingAfterConstructionDoesNotRenderClassMutable() throws Exception {
        assertThisDoesNotEscape(PassesThisReferenceAfterConstruction.class);
    }

    @Ignore("This fails on Jenkins, but passes locally, no idea why.")
    @Test
    public void noThisReferencePassedToConstructorOfOtherObjectWithInExtraWeirdCodeInNewCall() throws Exception {
        assertThisDoesNotEscape(NoThisPassedToOtherObjectAsOneOfManyParametersAndDoesWeirdStuffInNewCall.class);
    }

    @Test
    public void doesNotRenderMutableForHavingSetterMethod() throws Exception {
        assertThisDoesNotEscape(IsMutableForReassigningFieldNotForThisEscaping.class);
    }
    
    @Test
    public void doesNotRenderMutableForNewingUpObjectToAssignToField() throws Exception {
        assertThisDoesNotEscape(Safe.NewsUpObjectToAssignToField.class);
    }
    
    @Test
    public void doesNotRenderMutableForImplicitlyInvokingSuper() throws Exception {
        assertThisDoesNotEscape(Safe.ImplicitCallToSuper.class);
    }

    @Test
    public void doesNotRenderMutableForAssigningNonTopLevelClassToField() throws Exception {
        assertThisDoesNotEscape(AssignsNonInnerNonTopLevelClassToField.class);
    }
    
    @Test
    public void doesNotRenderMutableForExplicitlyInvokingSuper() throws Exception {
        assertThisDoesNotEscape(Safe.ExplicitCallToSuper.class);
    }

    @Test
    public void doesNotRenderMutableForCallingOtherConstructorOfThisClass() throws Exception {
        assertThisDoesNotEscape(Safe.CallToOtherConstructor.class);
    }

    @Test
    public void doesNotRenderMutableForPassingInitialisedFieldReference() throws Exception {
        assertThisDoesNotEscape(Safe.PassesInitialisedFieldToOtherMethod.class);
    }

    @Test
    @FalsePositive("Is only assigning this reference to field of same instance")
    public void doesNotRenderMutableForAssigningThisToInstanceField() throws Exception {
        assertThisDoesNotEscape(SaveThisReferenceAsInstanceFieldOfThisClass.class);
    }
    
    @Test
    @FalsePositive("Can't detect this situation.")
    public void thisReferenceAliasedToSomethingElseWhichEscapesIsReported() throws Exception {
       thisReferenceEscapingRendersClassMutable(AliasesThisReferenceBeforeLettingItEscape.class);
    }
    
    
    private void assertThisDoesNotEscape(Class<?> toAnalyse) {
        AnalysisResult result = runChecker(new EscapedThisReferenceChecker(), toAnalyse);
        assertEquals(TestUtil.formatReasons(result.reasons), IMMUTABLE, result.isImmutable);
    }

    @DataPoints
    public static Class<?>[] classes = new Class[] {
            ThisEscape.class,
            AsSingleParameter.class,
            AsOneOfSeveralParameters.class,
            AsOneOfSeveralParametersWithOtherWeirdCode.class,
            InOneConstructorButNotTheOther.class,
            AsLastOfSeveralParameters.class,
            AsFirstOfSeveralParameters.class,
            AsMoreThanOneOfSeveralParameters.class,
            AsParameterToPrivateMethod.class,
            AsParameterToStaticMethod.class,
            PassThisReferenceToStaticObject.class,
            ThisPassedToPrivateMethodWhichDoesPublishReference.class,
            SaveThisReferenceAsStaticFieldOfThisClass.class,
            SetThisReferenceAsStaticFieldOfOtherClass.class,
            SetThisReferenceAsInstanceFieldOfOtherObject.class,
            PassThisReferenceToParameter.class,
            PassInnerClassWithImplicitReferenceToThis.class,
            PassAnonymousInnerClassWithImplicitReferenceToThis.class 
            };

    @Theory
    public void thisReferenceEscapingRendersClassMutable(Class<?> passesThisReference) throws Exception {
        AnalysisResult result = runChecker(new EscapedThisReferenceChecker(), passesThisReference);
        assertThat(result, areNotImmutable());
        assertEquals(reasonDetailFor(passesThisReference), result.reasons.iterator().next());
    }

    private MutableReasonDetail reasonDetailFor(Class<?> clazz) {
        return newMutableReasonDetail("The 'this' reference is passed outwith the constructor.",
                ClassLocation.from(Dotted.fromClass(clazz)),
                MutabilityReason.ESCAPED_THIS_REFERENCE);
    }

}
