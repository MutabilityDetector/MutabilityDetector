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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.mutabilitydetector.checkers.info.AnalysisDatabase.newAnalysisDatabase;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mutabilitydetector.checkers.AsmSessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.Dotted;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.common.base.Optional;

public final class AnalysisSession implements IAnalysisSession {

    private final Map<Dotted, AnalysisResult> analysedClasses = newHashMap();
    private final List<Dotted> requestedAnalysis = newArrayList();
    private final List<AnalysisError> analysisErrors = newArrayList();

    private final IMutabilityCheckerFactory checkerFactory;
    private final ICheckerRunnerFactory checkerRunnerFactory;
    private final AnalysisDatabase database;
    private final AnalysisClassLoader analysisClassLoader;
	private final Configuration configuration;

    private AnalysisSession(ClassPath classpath, 
                             ICheckerRunnerFactory checkerRunnerFactory,
                             IMutabilityCheckerFactory checkerFactory, 
                             AnalysisClassLoader analysisClassLoader, 
                             Configuration configuration) {
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.checkerFactory = checkerFactory;
        AsmSessionCheckerRunner sessionCheckerRunner = new SessionCheckerRunner(this, checkerRunnerFactory.createRunner());
        this.database = newAnalysisDatabase(sessionCheckerRunner);
        this.analysisClassLoader = analysisClassLoader;
        this.configuration = configuration;
    }

    public static IAnalysisSession createWithGivenClassPath(ClassPath classpath, 
                                                              ICheckerRunnerFactory checkerRunnerFactory,
                                                              IMutabilityCheckerFactory checkerFactory, 
                                                              AnalysisClassLoader analysisClassLoader,
                                                              Configuration configuration) {
        return createWithGivenClassPath(classpath, configuration);
    }

    public static IAnalysisSession createWithCurrentClassPath() {
        return createWithCurrentClassPath(Configuration.NO_CONFIGURATION);
    }
    
	public static IAnalysisSession createWithCurrentClassPath(Configuration configuration) {
		ClassPath classpath = new ClassPathFactory().createFromJVM();
        return createWithGivenClassPath(classpath, configuration);
	}

	private static IAnalysisSession createWithGivenClassPath(ClassPath classpath, Configuration configuration) {
		return new AnalysisSession(classpath, 
                                    new CheckerRunnerFactory(classpath), 
                                    new MutabilityCheckerFactory(), 
                                    new PassthroughAnalysisClassLoader(),
                                    configuration);
	}

    @Override
    public RequestedAnalysis resultFor(Dotted className) {
        AnalysisResult resultForClass = requestAnalysis(className);
        return resultForClass == null 
                ? RequestedAnalysis.incomplete()
                : RequestedAnalysis.complete(addAnalysisResult(resultForClass));
    }

    private AnalysisResult requestAnalysis(Dotted className) {
    	
    	Optional<AnalysisResult> hardcodedResult = configuration.hardcodedResultFor(className);
    	if (hardcodedResult.isPresent()) {
    		return hardcodedResult.get();
    	}
    	
        if (isRepeatedRequestFor(className)) {
            return null;
        }
        
        if (resultHasAlreadyBeenGenerated(className)) {
            return analysedClasses.get(className);
        }
        
        requestedAnalysis.add(className);
        AllChecksRunner allChecksRunner = new AllChecksRunner(checkerFactory,
                                                              checkerRunnerFactory,
                                                              className, 
                                                              analysisClassLoader);
        return allChecksRunner.runCheckers(this);
    }

	private boolean isRepeatedRequestFor(Dotted className) {
        return requestedAnalysis.contains(className);
    }

    private boolean resultHasAlreadyBeenGenerated(Dotted className) {
        return analysedClasses.containsKey(className);
    }

    @Override
    public void runAnalysis(Collection<String> classNames) {
        for (String resource : classNames) {
            resource = resource.replace("/", ".");
            if (resource.endsWith(".class")) {
                resource = resource.substring(0, resource.lastIndexOf(".class"));
            }
            requestAnalysis(dotted(resource));
        }
    }

    private AnalysisResult addAnalysisResult(AnalysisResult result) {
        requestedAnalysis.remove(dotted(result.dottedClassName));
        analysedClasses.put(dotted(result.dottedClassName), result);
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
