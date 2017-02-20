package org.mutabilitydetector.cli;

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

import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.DefaultCachingAnalysisSession.createWithGivenClassPath;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.CARRY_ON;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory.ClassloadingOption;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.NonClassLoadingVerifierFactory;
import org.mutabilitydetector.checkers.ClassPathBasedCheckerRunnerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice;
import org.mutabilitydetector.classloading.CachingAnalysisClassLoader;
import org.mutabilitydetector.classloading.ClassForNameWrapper;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.misc.TimingUtil;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;

/**
 * Runs an analysis configured by the given classpath and options.
 * 
 */
public final class RunMutabilityDetector implements Runnable, Callable<String> {

    private final ClassPath classpath;
    private final BatchAnalysisOptions options;
    private final NamesFromClassResources namesFromClassResources;

    public RunMutabilityDetector(ClassPath classpath, BatchAnalysisOptions options, NamesFromClassResources namesFromClassResources) {
        this.classpath = classpath;
        this.options = options;
        this.namesFromClassResources = namesFromClassResources;
    }

    /**
     * Runs mutability detection, printing the results to System.out.
     */
    @Override
    public void run() {
        StringBuilder output = getResultString();
        System.out.println(output);
    }

    /**
     * Runs mutability detection, returning the results as a String.
     */
    @Override
    public String call() throws Exception {
        return getResultString().toString();
    }

    private StringBuilder getResultString() {
        RegExpResourceFilter regExpResourceFilter = new RegExpResourceFilter(ANY, ENDS_WITH_CLASS);
        String[] findResources = classpath.findResources("", regExpResourceFilter);
        List<Dotted> filtered = namesFromClassResources.asDotted(findResources);

        Configuration configuration = new ConfigurationBuilder() {
            @Override
            public void configure() {
                mergeHardcodedResultsFrom(OUT_OF_THE_BOX_CONFIGURATION);
                setExceptionPolicy(options.failFast() ? FAIL_FAST : CARRY_ON);
                setClassloadingPolicy(options.classloading());
            }
        }.build();

        String[] classPathFiles = new ClassPathFactory().parseClasspath(options.classpath());
        AsmVerifierFactory verifierFactory = options.classloading() == ClassloadingOption.ENABLED
            ? createClassLoadingVerifierFactory(classPathFiles)
            : new NonClassLoadingVerifierFactory(classpath);

        AnalysisSession newSession = createWithGivenClassPath(classpath,
                                                            new ClassPathBasedCheckerRunnerFactory(classpath, configuration.exceptionPolicy()),
                                                            new MutabilityCheckerFactory(ReassignedFieldAnalysisChoice.NAIVE_PUT_FIELD_ANALYSIS, configuration.immutableContainerClasses()),
                                                            verifierFactory,
                                                            configuration);


        AnalysisSession completedSession = new BatchAnalysisSession(newSession).runAnalysis(filtered);
        
        ClassListReaderFactory readerFactory = new ClassListReaderFactory(options.classListFile());
        
        TimingUtil timingUtil = new TimingUtil();
        
        return new SessionResultsFormatter(options, readerFactory, timingUtil)
                       .format(completedSession.getResults(), completedSession.getErrors());
    }

    private ClassLoadingVerifierFactory createClassLoadingVerifierFactory(String[] classPathFiles) {
        return new ClassLoadingVerifierFactory(
                new CachingAnalysisClassLoader(
                        new URLFallbackClassLoader(getCustomClassLoader(classPathFiles), new ClassForNameWrapper())));
    }

    private URLClassLoader getCustomClassLoader(String[] classPathFiles) {
        List<URL> urlList = new ArrayList<>(classPathFiles.length);
        
        for (String classPathUrl : classPathFiles) {
            try {
                URL toAdd = new File(classPathUrl).toURI().toURL();
                urlList.add(toAdd);
            } catch (MalformedURLException e) {
                System.err.printf("Classpath option %s is invalid.", classPathUrl);
            }
        }
        return new URLClassLoader(urlList.toArray(new URL[urlList.size()]));
    }

    public static void main(String[] args) {
        BatchAnalysisOptions options = createOptionsFromArgs(args);
        ClassPath classpath = new ClassPathFactory().createFromPath(options.classpath());

        new RunMutabilityDetector(classpath, options, new NamesFromClassResources(options.match())).run();
    }

    private static BatchAnalysisOptions createOptionsFromArgs(String[] args) {
        try {
            return new CommandLineOptions(System.err, args);
        } catch (Throwable e) {
            System.out.println("Exiting...");
            System.exit(1);
            throw new IllegalStateException();
        }
    }
}
