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
package org.mutabilitydetector.benchmarks.settermethod;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.AnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.checkers.SetterMethodChecker.newSetterMethodChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.CheckerRunner;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsBoolean;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsByte;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsChar;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsDouble;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsFloat;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsInt;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsLong;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsObjectArrayArray;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsReference;
import org.mutabilitydetector.benchmarks.settermethod.SetsFieldsOfDifferentTypes.SetsShort;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.checkers.SetterMethodChecker;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.junit.FalsePositive;
import org.mutabilitydetector.junit.IncorrectAnalysisRule;

@RunWith(Theories.class)
public class SetterMethodCheckerTest {
    
    @Rule public MethodRule rule = new IncorrectAnalysisRule();

    private SetterMethodChecker checker;
    private CheckerRunner checkerRunner;
    private IAnalysisSession analysisSession;
    private PrivateMethodInvocationInformation info;

    @Before
    public void setUp() {
        checkerRunner = CheckerRunner.createWithCurrentClasspath();
        analysisSession = createWithCurrentClassPath();
        info = new PrivateMethodInvocationInformation(new SessionCheckerRunner(analysisSession, checkerRunner));
        checker = newSetterMethodChecker(info);
    }

    private AnalysisResult doCheck(Class<?> toCheck) {
        return TestUtil.runChecker(checker, toCheck);
    }

    @Test
    public void immutableExamplePassesCheck() throws Exception {
        assertThat(doCheck(ImmutableExample.class), areImmutable());
    }

    @Test
    public void mutableByHavingSetterMethodFailsCheck() throws Exception {
        assertThat(doCheck(MutableByHavingSetterMethod.class), areNotImmutable());
    }

    @Test
    public void integerClassPassesCheck() throws Exception {
        assertThat(doCheck(Integer.class), areImmutable());
    }

    @Test
    public void enumTypePassesCheck() throws Exception {
        assertThat(doCheck(EnumType.class), areImmutable());
    }

    @FalsePositive("Field [myField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]" + "Field [primitiveField] can be reassigned within method [setPrivateFieldOnInstanceOfSelf]")
    @Test
    public void settingFieldOfOtherInstanceDoesNotRenderClassMutable() throws Exception {
        assertThat(doCheck(ImmutableButSetsPrivateFieldOfInstanceOfSelf.class), areImmutable());
    }

    @Test
    public void settingFieldOfOtherInstanceAndThisInstanceRendersClassMutable() throws Exception {
        assertThat(doCheck(MutableBySettingFieldOnThisInstanceAndOtherInstance.class), areNotImmutable());
    }

    @Test
    public void fieldsSetInPrivateMethodCalledOnlyFromConstructorIsImmutable() {
        assertThat(doCheck(ImmutableUsingPrivateFieldSettingMethod.class), areImmutable());
    }

    @FalsePositive("Field [reassignable] can be reassigned within method [setFieldOnParameter]")
    @Test
    public void settingFieldOfObjectPassedAsParameterDoesNotRenderClassMutable() throws Exception {
        assertThat(doCheck(ImmutableButSetsFieldOfOtherClass.class), areImmutable());
    }

    @Test
    public void settingFieldOfMutableFieldRendersClassMutable() throws Exception {
        assertThat(doCheck(MutableBySettingFieldOfField.class), areNotImmutable());
    }

    @FalsePositive("Does not create any reasons.")
    @Test
    public void subclassOfSettingFieldOfMutableFieldRendersClassMutable() throws Exception {
        AnalysisResult result = doCheck(StillMutableSubclass.class);

        assertThat(checker.reasons().size(), is(not(0)));
        assertThat(result, areNotImmutable());
    }

    @FalsePositive("Field [precision] can be reassigned within method [precision]" + "Field [stringCache] can be reassigned within method [toString]"
            + "Field [intVal] can be reassigned within method [inflate]")
    @Test
    public void bigDecimalDoesNotFailCheck() throws Exception {
        assertThat(doCheck(BigDecimal.class), areImmutable());
    }

    @FalsePositive("Field [hash] can be reassigned within method [hashCode]")
    @Test
    public void stringDoesNotFailCheck() throws Exception {
        assertThat(doCheck(String.class), areImmutable());
    }

    @Test
    public void fieldReassignmentInPublicStaticMethodMakesClassMutable() throws Exception {
        AnalysisResult result = doCheck(MutableByAssigningFieldOnInstanceWithinStaticMethod.class);
        assertThat(result, areNotImmutable());
    }

    @FalsePositive("Field can be reassigned.")
    @Test
    public void reassignmentOfStackConfinedObjectDoesNotFailCheck() throws Exception {
        assertThat(doCheck(ImmutableWithMutatingStaticFactoryMethod.class), areImmutable());
    }

    @Test
    public void reassigningFieldWithNewedUpObjectShouldBeMutable() {
        assertThat(doCheck(MutableByAssigningFieldToNewedUpObject.class), areNotImmutable());
    }

    @DataPoints
    public static Class<?>[] classes = new Class[] { SetsBoolean.class, SetsByte.class, SetsChar.class,
            SetsDouble.class, SetsFloat.class, SetsInt.class, SetsLong.class, SetsObjectArray.class,
            SetsObjectArrayArray.class, SetsReference.class, SetsShort.class };

    @Theory
    public void settingFieldsOfAnyTypeShouldBeMutable(Class<?> mutableSettingField) throws Exception {
        AnalysisResult result = runChecker(checker, mutableSettingField);
        assertThat(result, areNotImmutable());
    }

}
