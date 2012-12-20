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
package org.mutabilitydetector.cli;

import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;
import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;
import static org.mutabilitydetector.ThreadUnsafeAnalysisSession.createWithGivenClassPath;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.CARRY_ON;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.Configuration;
import org.mutabilitydetector.ConfigurationBuilder;
import org.mutabilitydetector.asmoverride.AsmVerifierFactory;
import org.mutabilitydetector.asmoverride.CachingTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.ClassLoadingVerifierFactory;
import org.mutabilitydetector.asmoverride.FileBasedTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.GuavaCachingTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.GuavaIsAssignableFromCachingTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.IsAssignableFromCachingTypeHierarchyReader;
import org.mutabilitydetector.asmoverride.NonClassLoadingVerifierFactory;
import org.mutabilitydetector.checkers.ClassPathBasedCheckerRunnerFactory;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory;
import org.mutabilitydetector.classloading.CachingAnalysisClassLoader;
import org.mutabilitydetector.classloading.ClassForNameWrapper;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.TypeHierarchyReader.TypeHierarchy;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.common.collect.MapMaker;
import com.google.common.io.InputSupplier;

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
        
        Configuration configuration = new ConfigurationBuilder() {
            @Override
            public void configure() {
                mergeHardcodedResultsFrom(OUT_OF_THE_BOX_CONFIGURATION);
                setExceptionPolicy(options.failFast() ? FAIL_FAST : CARRY_ON);
            }
        }.build(); 

        String[] classPathFiles = new ClassPathFactory().parseClasspath(options.classpath());
        AsmVerifierFactory verifierFactory = createClassLoadingVerifierFactory(classPathFiles);

        AnalysisSession newSession = createWithGivenClassPath(classpath, 
                                                            new ClassPathBasedCheckerRunnerFactory(classpath, configuration.exceptionPolicy()), 
                                                            new MutabilityCheckerFactory(), 
                                                            verifierFactory,
                                                            configuration);
        
        List<Dotted> filtered = namesFromClassResources.asDotted(findResources);
        
        AnalysisSession completedSession = new BatchAnalysisSession(newSession).runAnalysis(filtered);
        
        ClassListReaderFactory readerFactory = new ClassListReaderFactory(options.classListFile());
        
        return new SessionResultsFormatter(options, readerFactory)
                       .format(completedSession.getResults(), completedSession.getErrors());
    }

    @SuppressWarnings("unused")
    private NonClassLoadingVerifierFactory createGuavaVerifierFactory(String[] findResources) {
        return new NonClassLoadingVerifierFactory(
                new GuavaIsAssignableFromCachingTypeHierarchyReader(
                        new GuavaCachingTypeHierarchyReader(new FileBasedTypeHierarchyReader(getClassPathFileSuppliers(findResources)),
                                                            findResources.length)));
    }
    @SuppressWarnings("unused")
    private NonClassLoadingVerifierFactory createVerifierFactory(String[] findResources) {
        return new NonClassLoadingVerifierFactory(
                new IsAssignableFromCachingTypeHierarchyReader(
                        new CachingTypeHierarchyReader(
                                new FileBasedTypeHierarchyReader(getClassPathFileSuppliers(findResources)),
                                new MapMaker().initialCapacity(findResources.length).<Type, TypeHierarchy>makeMap())));
    }
    
    
    private ClassLoadingVerifierFactory createClassLoadingVerifierFactory(String[] classPathFiles) {
        return new ClassLoadingVerifierFactory(
                new CachingAnalysisClassLoader(
                        new URLFallbackClassLoader(getCustomClassLoader(classPathFiles), new ClassForNameWrapper())));
    }
    
    private URLClassLoader getCustomClassLoader(String[] classPathFiles) {
        List<URL> urlList = new ArrayList<URL>(classPathFiles.length);
        
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

    private Map<Dotted, InputSupplier<InputStream>> getClassPathFileSuppliers(String[] findResources) {
        Map<Dotted, InputSupplier<InputStream>> classFileInputMap = new ConcurrentHashMap<Dotted, InputSupplier<InputStream>>(findResources.length);

        for (final String resourcePath : findResources) {
            classFileInputMap.put(dotted(resourcePath), new InputSupplier<InputStream>() {
                @Override
                public InputStream getInput() throws IOException {
                    return classpath.getResourceAsStream(resourcePath);
                }
            });
        }
        return classFileInputMap;
    }

    public static void main(String[] args) {
        BatchAnalysisOptions options = createOptionsFromArgs(args);
        ClassPath classpath = new ClassPathFactory().createFromPath(options.classpath());

        new RunMutabilityDetector(classpath, options, new NamesFromClassResources(options.match())).run();
    }

    private static BatchAnalysisOptions createOptionsFromArgs(String[] args) {
        try {
            BatchAnalysisOptions options = new CommandLineOptions(System.err, args);
            return options;
        } catch (Throwable e) {
            System.out.println("Exiting...");
            System.exit(1);
            return null; // impossible statement
        }
    }
}
