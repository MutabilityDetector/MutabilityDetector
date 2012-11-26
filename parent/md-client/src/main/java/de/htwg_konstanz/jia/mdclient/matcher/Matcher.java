/**
 * 
 */
package de.htwg_konstanz.jia.mdclient.matcher;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * Utility class for retrieving matchers.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class Matcher {

    /*
     * Inhibit instantiation.
     */
    private Matcher() {
        throw new AssertionError();
    }

    /**
     * @return a matcher which checks if the reason for mutability is that the
     *         analysed class allows inheritance.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> classAllowsInheritance() {
        return new ClassAllowsInheritanceMatcher();
    }

    /**
     * @return a matcher which checks if the reason for mutability is that the
     *         analysed class leaks its {@code this}-reference during
     *         construction.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> classLeaksItsThisReference() {
        return new ThisEscapeMatcher();
    }

    /**
     * @param fieldName
     *            name of the field which can be reassigned. Must not be empty.
     * @param methodName
     *            name of the method which enables reassigning of
     *            {@code fieldName}. Must not be empty.
     * @return a matcher which checks if the reason for mutability is that the
     *         field with name {@code fieldName} can be reassigned by the method
     *         with name {@code methodName}.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> fieldCanBeReassigned(final String fieldName,
            final String methodName) {
        return new FieldCanBeReassignedMatcher(fieldName, methodName);
    }

    /**
     * @param fieldName
     *            name of the field which is mutable.
     * @param mutableFieldType
     *            class which surrounds {@code fieldName}.
     * @return a matcher which checks if the reason for mutability is that the
     *         field with name {@code fieldName} is of the mutable type
     *         {@code mutableFieldType}.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> fieldHasMutableType(final String fieldName,
            final Class<?> mutableFieldType) {
        return new FieldHasMutableTypeMatcher(fieldName, mutableFieldType);
    }

    /**
     * @param nameOfCausingMethod
     *            name of the field which is affected by side effect.
     * @param nameOfAffectedField
     *            name of the method which causes the side effect.
     * @return a matcher which checks if the reason for mutability is that the
     *         method with name {@code nameOfCausingMethod} causes a side effect
     *         on the field with name {@code nameOfAffectedField}.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> methodCausesSideEffect(
            final String nameOfCausingMethod, final String nameOfAffectedField) {
        return new MethodCausesSideEffectMatcher(nameOfAffectedField, nameOfCausingMethod);
    }

    /**
     * @return a matcher which checks if the reason for mutability is that the
     *         analysed type is abstract (e. g. abstract class or interface) and
     *         thus is inherently mutable.
     */
    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> abstractTypeIsInherentlyMutable() {
        return new ClassIsAbstractTypeMatcher();
    }

//    /**
//     * @return a matcher which checks if the reason for mutability is that the analysed class uses a co
//     */
//    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> classUsesCollectionWithMutableElementType() {
//        return new CollectionWithMutableElementTypeMatcher();
//    }

}
