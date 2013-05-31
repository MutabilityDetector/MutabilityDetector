package org.mutabilitydetector.checkers;

import static java.lang.String.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.locations.Dotted;

public final class EscapedThisReferenceCheckerTest {

    private static final class StartThreadInConstructor {
        @SuppressWarnings("unused")
        public StartThreadInConstructor() {
            super();
            final Thread t = new Thread(HELLO_WORLD_RUNNABLE);
            t.start();
        }
    }

    private static final class InvokeAlienMethodWithThis {
        @SuppressWarnings("unused")
        public InvokeAlienMethodWithThis() {
            super();
            System.out.println(this);
        }
    }

    private static final class ReturnThisFromMethod {
        public static ReturnThisFromMethod rtfm;

        public ReturnThisFromMethod() {
            rtfm = doIt();
        }

        public ReturnThisFromMethod doIt() {
            return this;
        }
    }

    private static final class AssignThisToVisibleVariable {
        @SuppressWarnings("unused")
        public final Object obj = this;
    }

    private static class InvokeOverridableInstanceMethod {

        @SuppressWarnings("unused")
        public InvokeOverridableInstanceMethod() {
            super();
            final Object temp = overridable();
        }

        public Object overridable() {
            return "Hello";
        }
    }

    private static final Runnable HELLO_WORLD_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello World.");
        }
    };

    private final AnalysisSession analysisSession = ThreadUnsafeAnalysisSession.createWithCurrentClassPath();

    @Test
    public void startingAThreadInConstructorMakesClassMutable() {
        assertIsMutable(StartThreadInConstructor.class);
    }

    private void assertIsMutable(final Class<?> classToAnalyse) {
        final AnalysisResult analysisResult = analyse(classToAnalyse);
        assertEquals(IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);
        if (analysisResult.isImmutable != IsImmutable.IMMUTABLE) {
            final String className = classToAnalyse.getName();
            
            System.out.println(format("%s: ", className));
            for (final MutableReasonDetail reason : analysisResult.reasons) {
                System.out.println(format("  %s", reason.message()));
            }
        }
    }

    private AnalysisResult analyse(final Class<?> classToAnalyse) {
        final Dotted dottedClassName = Dotted.dotted(classToAnalyse.getName());
        return analysisSession.resultFor(dottedClassName);
    }

    @Test
    public void alienMethodCallMakesClassMutable() {
        assertIsMutable(InvokeAlienMethodWithThis.class);
    }

    @Test
    public void returningThisFromMethodLeadsToMutableClass() {
        assertIsMutable(ReturnThisFromMethod.class);
    }

    @Test
    public void assigningThisToVisibleVariableLeadsToMutableClass() {
        assertIsMutable(AssignThisToVisibleVariable.class);
    }

    @Test
    public void callingOverridableInstanzMethodLeadsToMutableClass() {
        assertIsMutable(InvokeOverridableInstanceMethod.class);
    }

}
