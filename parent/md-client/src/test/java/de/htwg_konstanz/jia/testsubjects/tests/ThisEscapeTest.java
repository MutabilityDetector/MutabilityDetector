/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classLeaksItsThisReference;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.*;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.*;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.CallToOtherConstructor;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.NewsUpObjectToAssignToField;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.PassesInitialisedFieldToOtherMethod;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.SaveThisReferenceToPrivateInstanceField;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.*;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.PassesThisReferenceToMethodCall.*;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.AliasesThisReferenceBeforeLettingItEscape;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.PassAnonymousInnerClassWithImplicitReferenceToThis;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.PassInnerClassWithImplicitReferenceToThis;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.PassThisReferenceToParameter;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.PassThisReferenceToStaticObject;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.SaveThisReferenceAsInstanceFieldOfThisClass;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.SaveThisReferenceAsStaticFieldOfThisClass;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.SetThisReferenceAsInstanceFieldOfOtherObject;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.SetThisReferenceAsStaticFieldOfOtherClass;

/**
 * Test cases which examine the behaviour of <em>Mutability Detector</em>
 * regarding the escaping of the {@code this}-reference.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 23.11.2012
 */
@RunWith(Theories.class)
public final class ThisEscapeTest {

    @Retention(RetentionPolicy.RUNTIME)
    @ParametersSuppliedBy(PassingThisReferenceAsParameterAtDifferentPositionsSupplier.class)
    public @interface PassingThisReferenceAsParameterAtDifferentPositions {
    }

    public static final class PassingThisReferenceAsParameterAtDifferentPositionsSupplier extends ParameterSupplier {

        @Override
        public List<PotentialAssignment> getValueSources(final ParameterSignature sig) {
            final ImmutableList.Builder<PotentialAssignment> builder = new ImmutableList.Builder<PotentialAssignment>();
            builder.add(wrap(AsFirstOfSeveralParameters.class)).add(wrap(AsLastOfSeveralParameters.class));
            builder.add(wrap(AsMoreThanOneOfSeveralParameters.class)).add(wrap(AsOneOfSeveralParameters.class));
            builder.add(wrap(AsOneOfSeveralParametersWithOtherWeirdCode.class));
            builder.add(wrap(AsParameterToPrivateMethod.class)).add(wrap(AsParameterToStaticMethod.class));
            builder.add(wrap(AsSingleParameter.class));
            return builder.build();
        }

        private PotentialAssignment wrap(final Class<?> classToWrap) {
            return PotentialAssignment.forValue(classToWrap.getName(), classToWrap);
        }
        
    }

    // Tests for unsafe classes

    @Test
    public void directlyAssigningThisToVisibleClassVariableOfSameClass() {
        assertIsMutable(SaveThisReferenceAsStaticFieldOfThisClass.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleClassVariableOfOtherClass() {
        assertIsMutable(SetThisReferenceAsStaticFieldOfOtherClass.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableOfSameClass() {
        assertIsMutable(SaveThisReferenceAsInstanceFieldOfThisClass.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableOfOtherObject() {
        assertIsMutable(SetThisReferenceAsInstanceFieldOfOtherObject.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void indirectlyAssigningThisToVisibleInstanceVariableOfOtherObject() {
        assertIsMutable(AssigningThisToAlienInstanceVariable.class).andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void invokingAlienConstructorWithAliasOfThisReference() {
        assertIsMutable(AliasesThisReferenceBeforeLettingItEscape.class).andTheReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void addingThisReferenceToStaticCollectionOfOtherClass() {
        assertIsMutable(PassThisReferenceToStaticObject.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void invokingAlienClassMethodWithThis() {
        assertIsMutable(InvokeAlienClassMethodWithThis.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void invokingAlienInstanceMethodWithThis() {
        assertIsMutable(PassThisReferenceToParameter.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void startingThreadWhichHoldsImplicitThisReference() {
        assertIsMutable(StartingThreadWithImplicitThisReference.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void addingThisToVisibleCollectionOfOwnClass() {
        assertIsMutable(AddingThisToVisibleCollection.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void addingThisToVisibleCollectionOfOtherClass() {
        assertIsMutable(PassThisReferenceToStaticObject.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void callingAnAlienConstructorWithThis() {
        assertIsMutable(CallingConstructorWithThis.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void returningThisFromStaticMethod() {
        assertIsMutable(ReturningThisFromMethod.class).andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void passAnonymousInnerClassWithImplicitReferenceToThis() {
        assertIsMutable(PassAnonymousInnerClassWithImplicitReferenceToThis.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void passInnerClassWithImplicitReferenceToThis() {
        assertIsMutable(PassInnerClassWithImplicitReferenceToThis.class).andOneReasonIsThat(
                classLeaksItsThisReference());
    }

    @Test
    public void invokingAnOverridableInstanceMethodFromWithinConstructor() {
        assertIsMutable(InvokeOverridableInstanceMethod.class).andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void aliasesThisReferenceInInstanceMethod() {
        assertIsMutable(AliasesThisReferenceInInstanceMethod.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void rendersMutableForPassingThisReferenceInOneConstructorButNotTheOther() {
        assertIsMutable(InOneConstructorButNotTheOther.class).andTheReasonIsThat(classLeaksItsThisReference());
    }

    @Theory
    public void rendersMutableForPassingThisReferenceAsParameterAtDifferentPositions(
            @PassingThisReferenceAsParameterAtDifferentPositions final Class<?> classToAnalyse) {
        assertIsMutable(classToAnalyse).andOneReasonIsThat(classLeaksItsThisReference());
    }

    // Tests for safe classes

    @Test
    public void startingBenignThreadPreservesImmutability() {
        assertInstancesOf(StartingBenignThread.class, areImmutable());
    }

    @Test
    public void invokingNonOverridableInstanceMethodInConstructor() {
        assertInstancesOf(InvokeFinalInstanceMethod.class, areImmutable(), allowingForSubclassing());
    }

    @Test
    public void storingThisReferenceInPrivateInstanceVariable() {
        assertInstancesOf(SaveThisReferenceToPrivateInstanceField.class, areImmutable());
    }

    @Test
    public void createAndStoreObjectReferenceInPrivateInstanceVariable() {
        assertIsMutable(NewsUpObjectToAssignToField.class).andNoReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void callingOtherConstructorOfSameClass() {
        assertInstancesOf(CallToOtherConstructor.class, areImmutable());
    }

    @Test
    public void doesNotRenderMutableForPassingInitialisedFieldToOtherMethod() {
        assertIsMutable(PassesInitialisedFieldToOtherMethod.class).andNoReasonIsThat(classLeaksItsThisReference());
    }

}
