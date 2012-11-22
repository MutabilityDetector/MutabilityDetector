/**
 * 
 */
package de.htwg_konstanz.jia.mdclient.matcher;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class Matcher {

    private Matcher() {
        throw new AssertionError();
    }

    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> classAllowsInheritance() {
        return new ClassAllowsInheritanceMatcher();
    }

    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> classLeaksItsThisReference() {
        return new ThisEscapeMatcher();
    }

    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> fieldCanBeReassigned(final String fieldName,
            final String methodName) {
        return new FieldCanBeReassignedMatcher(fieldName, methodName);
    }

    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> fieldHasMutableType(final String fieldName,
            final Class<?> mutableFieldType) {
        return new FieldHasMutableTypeMatcher(fieldName, mutableFieldType);
    }

    public static org.hamcrest.Matcher<ParentAwareMutableReasonDetail> methodCausesSideEffect(
            final String nameOfCausingMethod,
            final String nameOfAffectedField) {
        return new MethodCausesSideEffectMatcher(nameOfAffectedField, nameOfCausingMethod);
    }

}
