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

import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mutabilitydetector.checkers.AsmSessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public final class AnalysisSession implements IAnalysisSession {

    private final Map<String, AnalysisResult> analysedClasses = new HashMap<String, AnalysisResult>();
    private final List<AnalysisError> analysisErrors = new ArrayList<AnalysisError>();
    private final IMutabilityCheckerFactory checkerFactory;
    private final ICheckerRunnerFactory checkerRunnerFactory;
    private final List<String> requestedAnalysis = new ArrayList<String>();
    private final AnalysisDatabase database;
    private final AnalysisClassLoader analysisClassLoader;

    private AnalysisSession(ClassPath classpath, 
                             ICheckerRunnerFactory checkerRunnerFactory,
                             IMutabilityCheckerFactory checkerFactory, 
                             AnalysisClassLoader analysisClassLoader) {
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.checkerFactory = checkerFactory;
        AsmSessionCheckerRunner sessionCheckerRunner = new SessionCheckerRunner(this, checkerRunnerFactory.createRunner());
        this.database = newAnalysisDatabase(sessionCheckerRunner);
        this.analysisClassLoader = analysisClassLoader;
    }

    public static IAnalysisSession createWithGivenClassPath(ClassPath classpath, 
                                                              ICheckerRunnerFactory checkerRunnerFactory,
                                                              IMutabilityCheckerFactory checkerFactory, 
                                                              AnalysisClassLoader analysisClassLoader) {
        return new AnalysisSession(classpath, checkerRunnerFactory, checkerFactory, analysisClassLoader);
    }

    public static IAnalysisSession createWithCurrentClassPath() {
        ClassPath classpath = new ClassPathFactory().createFromJVM();
        return new AnalysisSession(classpath, 
                                    new CheckerRunnerFactory(classpath), 
                                    new MutabilityCheckerFactory(), 
                                    new PassthroughAnalysisClassLoader());
    }

    @Override
    public RequestedAnalysis resultFor(String className) {
        AnalysisResult resultForClass = requestAnalysis(className);
        return resultForClass == null 
                ? RequestedAnalysis.incomplete()
                : RequestedAnalysis.complete(addAnalysisResult(resultForClass));
    }

    private AnalysisResult requestAnalysis(String className) {
        if (isRepeatedRequestFor(className)) {
            return null;
        }
        
        if (resultHasAlreadyBeenGenerated(className)) {
            return analysedClasses.get(className);
        }
        
        requestedAnalysis.add(className);
        AllChecksRunner allChecksRunner = new AllChecksRunner(checkerFactory,
                                                              checkerRunnerFactory,
                                                              dotted(className), 
                                                              analysisClassLoader);
        return allChecksRunner.runCheckers(this);
    }


    private boolean isRepeatedRequestFor(String className) {
        return requestedAnalysis.contains(className);
    }

    private boolean resultHasAlreadyBeenGenerated(String className) {
        return analysedClasses.containsKey(className);
    }

    @Override
    public void runAnalysis(Collection<String> classNames) {
        for (String resource : classNames) {
            resource = resource.replace("/", ".");
            if (resource.endsWith(".class")) {
                resource = resource.substring(0, resource.lastIndexOf(".class"));
            }
            requestAnalysis(resource);
        }
    }

    private AnalysisResult addAnalysisResult(AnalysisResult result) {
        requestedAnalysis.remove(result.dottedClassName);
        analysedClasses.put(result.dottedClassName, result);
        return result;
    }

    @Override
    public void addAnalysisError(AnalysisError error) {
        requestedAnalysis.remove(error.onClass);
        analysisErrors.add(error);
    }

    @Override
    public Collection<AnalysisResult> getResults() {
        return Collections.unmodifiableCollection(analysedClasses.values());
    }

    @Override
    public Collection<AnalysisError> getErrors() {
        return Collections.unmodifiableCollection(analysisErrors);
    }

    @Override
    public AnalysisDatabase analysisDatabase() {
        return database;
    }

}
