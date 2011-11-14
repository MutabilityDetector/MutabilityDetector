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

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;
import static org.mutabilitydetector.TestUtil.formatReasons;

import org.mutabilitydetector.IAnalysisSession.AnalysisError;

public class ImmutableAssert {

    public static void assertImmutable(AnalysisResult result) {
        doAssertEquals(result.dottedClassName, IMMUTABLE, result);
    }

    public static void assertImmutable(Class<?> toAnalyse) {
        doAssertEquals(toAnalyse.getName(), IMMUTABLE, getResultAndPrintErrors(toAnalyse));
    }

    public static void assertDefinitelyNotImmutable(IsImmutable result) {
        assertEquals("Expected Not Immutable result.", NOT_IMMUTABLE, result);
    }

    public static void assertDefinitelyNotImmutable(AnalysisResult result) {
        doAssertEquals(result.dottedClassName, NOT_IMMUTABLE, result);
    }

    public static void assertNotImmutable(Class<?> toAnalyse) {
        doAssertEquals(toAnalyse.getName(), NOT_IMMUTABLE, getResultAndPrintErrors(toAnalyse));
    }

    public static void assertEffectivelyImmutable(Class<?> toAnalyse) {
        doAssertEquals(toAnalyse.getName(), EFFECTIVELY_IMMUTABLE, getResultAndPrintErrors(toAnalyse));
    }

    public static void assertEffectivelyImmutable(AnalysisResult result) {
        doAssertEquals(result.dottedClassName, EFFECTIVELY_IMMUTABLE, result);
    }

    public static void assertNotImmutable(AnalysisResult result) {
        doAssertEquals(result.dottedClassName, NOT_IMMUTABLE, result);
    }

    private static AnalysisResult getResultAndPrintErrors(Class<?> toAnalyse) {
        IAnalysisSession session = AnalysisSession.createWithCurrentClassPath();
        String className = toAnalyse.getName();
        AnalysisResult result = session.resultFor(className);

        for (AnalysisError error : session.getErrors()) {
            System.err.printf("Analysis error running checker=[%s] on class=[%s.class]:%n%s%n",
                    error.checkerName,
                    error.onClass,
                    error.description);
        }

        return result;
    }

    private static void doAssertEquals(String className, IsImmutable expected, AnalysisResult actual) {
        String failure = "Class " + className + " is expected to be " + expected + " immutable.";
        failure += "\n" + formatReasons(actual.reasons);
        assertEquals(failure, expected, actual.isImmutable);
    }

}
