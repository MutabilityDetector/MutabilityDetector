/*
 * Mutability Detector
 * 
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

import org.mutabilitydetector.checkers.ISessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public class AnalysisSession implements IAnalysisSession {

    private final Map<String, AnalysisResult> analysedClasses = new HashMap<String, AnalysisResult>();
    private final List<AnalysisError> analysisErrors = new ArrayList<AnalysisError>();
    private final IMutabilityCheckerFactory checkerFactory = new MutabilityCheckerFactory();
    private final CheckerRunnerFactory checkerRunnerFactory;
    private final List<String> requestedAnalysis = new ArrayList<String>();
    private final AnalysisDatabase database;

    public AnalysisSession(ClassPath classpath) {
        checkerRunnerFactory = new CheckerRunnerFactory(classpath);
        ISessionCheckerRunner sessionCheckerRunner = new SessionCheckerRunner(this, checkerRunnerFactory.createRunner());
        database = newAnalysisDatabase(sessionCheckerRunner);
    }

    public AnalysisSession() {
        this(new ClassPathFactory().createFromJVM());
    }

    public static IAnalysisSession createWithGivenClassPath(ClassPath classpath) {
        return new AnalysisSession(classpath);
    }

    public static IAnalysisSession createWithCurrentClassPath() {
        return new AnalysisSession(new ClassPathFactory().createFromJVM());
    }

    @Override
    public AnalysisResult resultFor(String className) {
        AnalysisResult resultForClass = analysedClasses.get(className);
        if (resultForClass != null) {
            return resultForClass;
        }
        
        requestAnalysis(className);
        return resultFor(className);
    }

    private void requestAnalysis(String className) {
        if (requestedAnalysis.contains(className)) {
            // isImmutable has already been called for this class, and the
            // result not yet generated
            
            return;
        }
        
        requestedAnalysis.add(className);
        AllChecksRunner allChecksRunner = new AllChecksRunner(checkerFactory,
                                                              checkerRunnerFactory,
                                                              dotted(className));
        AnalysisResult result = allChecksRunner.runCheckers(this);
        addAnalysisResult(result);

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

    @Override
    public void addAnalysisResult(AnalysisResult result) {
        requestedAnalysis.remove(result.dottedClassName);
        analysedClasses.put(result.dottedClassName, result);
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
