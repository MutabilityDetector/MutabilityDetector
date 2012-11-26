/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.tests;

import static de.htwg_konstanz.jia.mdclient.MutabilityAsserter.assertIsMutable;
import static de.htwg_konstanz.jia.mdclient.matcher.Matcher.classLeaksItsThisReference;
import static org.mutabilitydetector.unittesting.AllowedReason.allowingForSubclassing;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;

import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.*;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.CallToOtherConstructor;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.NewsUpObjectToAssignToField;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.PassesInitialisedFieldToOtherMethod;
import de.htwg_konstanz.jia.testsubjects.escapedthis.safe.Safe.SaveThisReferenceToPrivateInstanceField;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.*;
import de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe.Unsafe.*;

/**
 * Test cases which examine the behaviour of <em>Mutability Detector</em>
 * regarding the escaping of the {@code this}-reference.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 23.11.2012
 */
public final class ThisEscapeTest {

    // Tests for unsafe classes

    @Test
    public void directlyAssigningThisToVisibleClassVariableOfSameClass() {
        assertIsMutable(SaveThisReferenceAsStaticFieldOfThisClass.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleClassVariableOfOtherClass() {
        assertIsMutable(SetThisReferenceAsStaticFieldOfOtherClass.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableOfSameClass() {
        assertIsMutable(SaveThisReferenceAsInstanceFieldOfThisClass.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void directlyAssigningThisToVisibleInstanceVariableOfOtherObject() {
        assertIsMutable(SetThisReferenceAsInstanceFieldOfOtherObject.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void indirectlyAssigningThisToVisibleInstanceVariableOfOtherObject() {
        assertIsMutable(AssigningThisToAlienInstanceVariable.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void invokingAlienConstructorWithAliasOfThisReference() {
        assertIsMutable(AliasesThisReferenceBeforeLettingItEscape.class)
            .andTheReasonIsThat(classLeaksItsThisReference());
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
        assertIsMutable(PassAnonymousInnerClassWithImplicitReferenceToThis.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void passInnerClassWithImplicitReferenceToThis() {
        assertIsMutable(PassInnerClassWithImplicitReferenceToThis.class)
            .andOneReasonIsThat(classLeaksItsThisReference());
    }
    
    @Test
    public void invokingAnOverridableInstanceMethodFromWithinConstructor() {
        assertIsMutable(InvokeOverridableInstanceMethod.class).andOneReasonIsThat(classLeaksItsThisReference());
    }

    @Test
    public void aliasesThisReferenceInInstanceMethod() {
        assertIsMutable(AliasesThisReferenceInInstanceMethod.class).andTheReasonIsThat(classLeaksItsThisReference());
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
    public void bar() {
        assertIsMutable(PassesInitialisedFieldToOtherMethod.class).andNoReasonIsThat(classLeaksItsThisReference());
    }

}
