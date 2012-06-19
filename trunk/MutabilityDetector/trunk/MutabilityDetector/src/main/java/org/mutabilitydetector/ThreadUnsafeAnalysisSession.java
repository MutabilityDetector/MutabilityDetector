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

import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.CachingTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.NonClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.TypeHierarchyReader;
import org.mutabilitydetector.checkers.AsmSessionCheckerRunner;
import org.mutabilitydetector.checkers.info.AnalysisDatabase;
import org.mutabilitydetector.checkers.info.SessionCheckerRunner;
import org.mutabilitydetector.locations.Dotted;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.common.base.Optional;

public final class ThreadUnsafeAnalysisSession implements AnalysisSession {

    private final Map<Dotted, AnalysisResult> analysedClasses = newHashMap();
    private final List<Dotted> requestedAnalysis = newArrayList();
    private final List<AnalysisError> analysisErrors = newArrayList();

    private final MutabilityCheckerFactory checkerFactory;
    private final CheckerRunnerFactory checkerRunnerFactory;
    private final AnalysisDatabase database;
	private final Configuration configuration;
    private final AsmVerifierFactory verifierFactory;
	
    private ThreadUnsafeAnalysisSession(ClassPath classpath, 
                             CheckerRunnerFactory checkerRunnerFactory,
                             MutabilityCheckerFactory checkerFactory, 
                             AsmVerifierFactory verifierFactory,
                             Configuration configuration) {
        this.checkerRunnerFactory = checkerRunnerFactory;
        this.checkerFactory = checkerFactory;
        this.verifierFactory = verifierFactory;
        AsmSessionCheckerRunner sessionCheckerRunner = new SessionCheckerRunner(this, checkerRunnerFactory.createRunner());
        this.database = newAnalysisDatabase(sessionCheckerRunner);
        this.configuration = configuration;
    }

    public static AnalysisSession createWithGivenClassPath(ClassPath classpath, 
                                                              CheckerRunnerFactory checkerRunnerFactory,
                                                              MutabilityCheckerFactory checkerFactory, 
                                                              AsmVerifierFactory verifierFactory,
                                                              Configuration configuration) {
        return createWithGivenClassPath(classpath, configuration, verifierFactory);
    }

    public static AnalysisSession createWithCurrentClassPath() {
        return createWithCurrentClassPath(Configuration.NO_CONFIGURATION);
    }
    
	public static AnalysisSession createWithCurrentClassPath(Configuration configuration) {
		ClassPath classpath = new ClassPathFactory().createFromJVM();
        ClassLoadingVerifierFactory verifierFactory = new ClassLoadingVerifierFactory(new CachingAnalysisClassLoader(new ClassForNameWrapper()));
        return createWithGivenClassPath(classpath, configuration, verifierFactory);
	}
	
	public static AnalysisSession tempCreateWithVerifier() {
	    ClassPath classpath = new ClassPathFactory().createFromJVM();
	    AsmVerifierFactory verifierFactory = new NonClassLoadingVerifierFactory(new CachingTypeHierarchyReader(new TypeHierarchyReader()));
	    return createWithGivenClassPath(classpath, Configuration.NO_CONFIGURATION, verifierFactory);
	    
	}

	private static AnalysisSession createWithGivenClassPath(ClassPath classpath, Configuration configuration, AsmVerifierFactory verifierFactory) {
		return new ThreadUnsafeAnalysisSession(classpath, 
                                    new ClassPathBasedCheckerRunnerFactory(classpath), 
                                    new MutabilityCheckerFactory(), 
                                    verifierFactory,
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
                                                              verifierFactory, 
                                                              className);
        return allChecksRunner.runCheckers(this, database);
    }

	private boolean isRepeatedRequestFor(Dotted className) {
        return requestedAnalysis.contains(className);
    }

    private boolean resultHasAlreadyBeenGenerated(Dotted className) {
        return analysedClasses.containsKey(className);
    }

    @Override
    public void runAnalysis(Iterable<Dotted> classNames) {
        for (Dotted className : classNames) {
            requestAnalysis(className);
        }
    }

    private AnalysisResult addAnalysisResult(AnalysisResult result) {
        requestedAnalysis.remove(dotted(result.dottedClassName));
        analysedClasses.put(dotted(result.dottedClassName), result);
        return result;
    }

    @Override
    public void addAnalysisError(AnalysisError error) {
        requestedAnalysis.remove(dotted(error.onClass));
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

}
