package org.mutabilitydetector;

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


import org.junit.Ignore;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.CheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.InformationRetrievalRunner;
import org.mutabilitydetector.checkers.info.TypeInformationRetriever;
import org.mutabilitydetector.classloading.AnalysisClassLoader;
import org.mutabilitydetector.classloading.CachingAnalysisClassLoader;
import org.mutabilitydetector.classloading.ClassForNameWrapper;
import org.mutabilitydetector.locations.ClassIdentifier;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.internal.ReasonsFormatter;

import java.util.Collection;

import static java.util.Collections.singleton;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.MutabilityReason.NULL_REASON;
import static org.mutabilitydetector.MutableReasonDetail.newMutableReasonDetail;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.checkers.CheckerRunner.createWithCurrentClasspath;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;
import static org.mutabilitydetector.locations.Dotted.fromClass;

@Ignore
public class TestUtil {

    public static AnalysisResult getAnalysisResult(Class<?> toAnalyse) {
        return testAnalysisSession().resultFor(Dotted.fromClass(toAnalyse));
    }

    public static AnalysisSession testAnalysisSession() {
        return ThreadUnsafeAnalysisSession.createWithCurrentClassPath(OUT_OF_THE_BOX_CONFIGURATION);
    }

    public static String formatReasons(Collection<MutableReasonDetail> reasons) {
        return ReasonsFormatter.formatReasons(reasons);
    }

    public static Collection<MutableReasonDetail> unusedMutableReasonDetails() {
        return singleton(unusedMutableReasonDetail());
    }

    public static MutableReasonDetail unusedMutableReasonDetail() {
        return newMutableReasonDetail("this reason is not meant to be involved",
                ClassLocation.fromInternalName("some made up class name"),
                NULL_REASON);
    }

    public static AnalysisResult runChecker(AsmMutabilityChecker checker, Class<?> toAnalyse) {
        AnalysisSession analysisSession = testAnalysisSession();
        CheckerRunner.createWithCurrentClasspath(FAIL_FAST).run(checker, fromClass(toAnalyse), analysisSession.getResults());
        return AnalysisResult.analysisResult(toAnalyse.getCanonicalName(), checker.result(), checker.reasons());
    }

    public static InformationRetrievalRunner sessionCheckerRunner() {
        return new InformationRetrievalRunner(testAnalysisSession(), createWithCurrentClasspath(FAIL_FAST));
    }

    public static void retrieveInformation(TypeInformationRetriever checker, Class<?> toAnalyse) {
        TestUtil.sessionCheckerRunner().run(checker, ClassIdentifier.forClass(fromClass(toAnalyse)));
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
        return new CachingAnalysisClassLoader(new ClassForNameWrapper());
    }

    public static AsmVerifierFactory testingVerifierFactory() {
        return new ClassLoadingVerifierFactory(testingAnalysisClassLoader());
    }
}
