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

import static java.util.Arrays.asList;
import static org.mutabilitydetector.AnalysisClassLoader.CLASS_FOR_NAME_LOADER;
import static org.mutabilitydetector.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.MutabilityReason.NULL_REASON;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithCurrentClassPath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import java.util.Collection;

import org.junit.Ignore;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.TypeHierarchyReader;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.internal.ReasonsFormatter;

@Ignore
public class TestUtil {
    public static IsImmutable getIsImmutableResult(Class<?> toAnalyse) {
        IsImmutable result = ThreadUnsafeAnalysisSession.createWithCurrentClassPath().resultFor(Dotted.fromClass(toAnalyse)).result.isImmutable;
        return result;
    }

    public static AnalysisResult getAnalysisResult(Class<?> toAnalyse) {
        return ThreadUnsafeAnalysisSession.createWithCurrentClassPath().resultFor(Dotted.fromClass(toAnalyse)).result;
    }

    public static String formatReasons(Collection<MutableReasonDetail> reasons) {
        return ReasonsFormatter.formatReasons(reasons);
    }

    public static Collection<MutableReasonDetail> unusedMutableReasonDetails() {
        return asList(unusedMutableReasonDetail());
    }

    public static MutableReasonDetail unusedMutableReasonDetail() {
        return newMutableReasonDetail("this reason is not meant to be involved", 
                                       ClassLocation.fromInternalName("some made up class name"), 
                                       NULL_REASON);
    }

    public static AnalysisResult runChecker(AsmMutabilityChecker checker, Class<?> toAnalyse) {
        CheckerRunner.createWithCurrentClasspath().run(createWithCurrentClassPath(), checker, fromClass(toAnalyse));
        return AnalysisResult.analysisResult(toAnalyse.getCanonicalName(), checker.result(), checker.reasons());
    }

    public static SessionCheckerRunner sessionCheckerRunner() {
        return new SessionCheckerRunner(createWithCurrentClassPath(), createWithCurrentClasspath());
    }

    public static AnalysisDatabase analysisDatabase() {
        return newAnalysisDatabase(sessionCheckerRunner());
    }

    public static AnalysisResult unusedAnalysisResult(String dottedClassName, IsImmutable isImmutable) {
        return AnalysisResult.analysisResult(dottedClassName, isImmutable, unusedMutableReasonDetails());
    }

    public static CodeLocation<?> unusedCodeLocation() {
        return ClassLocation.fromInternalName("this is an unused code location");
    }

    public static Reason unusedReason() {
        return MutabilityReason.NULL_REASON;
    }

    public static AnalysisClassLoader testingAnalysisClassLoader() {
        return new CachingAnalysisClassLoader(CLASS_FOR_NAME_LOADER);
    }

    public static TypeHierarchyReader testingTypeHierarchyReader() {
        return new TypeHierarchyReader();
    }

    public static AsmVerifierFactory testingVerifierFactory() {
        return new ClassLoadingVerifierFactory(testingAnalysisClassLoader());
    }
}
