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


import com.google.classpath.ClassPathFactory;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class AnalysingRtJar {

    private final PrintStream errorStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // suppress output in tests
        }
    });
    private final NamesFromClassResources namesFromClassResources = new NamesFromClassResources(".*");

    @Ignore
    @Test
    public void checkExceptionIsNotThrown() {
        String rtJarPath = System.getProperty("java.home") + "/lib/rt.jar";
        BatchAnalysisOptions options = new CommandLineOptions(System.err, "-cp", rtJarPath, "-e");
        new RunMutabilityDetector(new ClassPathFactory().createFromPath(rtJarPath), options, namesFromClassResources).run();
    }
    
    public static void main(String[] args) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Date started = new Date();
        new AnalysingRtJar().checkExceptionIsNotThrown();
        Date ended = new Date();
        stopwatch.stop();

        System.out.println("======================================");
        System.out.println("Started: " + started);
        System.out.println("Ended: " + ended);
        System.out.println("Total: " + stopwatch.elapsed(TimeUnit.SECONDS));
    }
    
    @Ignore
    @Test
    public void checkExceptionIsNotThrownRunOnSelfJar() {
        String selfJarPath = System.getProperty("user.home") + "/.m2/repository/joda-time/joda-time/2.0/joda-time-2.0.jar";
        BatchAnalysisOptions options = new CommandLineOptions(errorStream, "-v", "-cp", selfJarPath);
        new RunMutabilityDetector(new ClassPathFactory().createFromPath(selfJarPath), options, namesFromClassResources).run();
    }

    @Ignore
    @Test
    public void checkNullPointerExceptionIsNotThrownOnAbritaryCodebase() {
        String rtJarPath = "...";
        BatchAnalysisOptions options = new CommandLineOptions(errorStream, "-cp", rtJarPath);
        new RunMutabilityDetector(new ClassPathFactory().createFromPath(rtJarPath), options, namesFromClassResources).run();
    }

    @Ignore
    @Test
    public void compareNonClassloadingVsClassloadingResults() throws Exception {
        String rtJarPath = System.getProperty("java.home") + "/lib/rt.jar";
//        String rtJarPath = "./target/test-classes/";
        List<String> options = Arrays.asList("--classpath", rtJarPath, "--reportErrors");
        List<String> nonClassloadingOptions = ImmutableList.<String>builder().addAll(options).add("--nonClassloading").build();
        BatchAnalysisOptions classloadingBatchOptions = new CommandLineOptions(System.err, options);
        BatchAnalysisOptions nonClassloadingBatchOptions = new CommandLineOptions(System.err, nonClassloadingOptions);


        String classLoadingResult = new RunMutabilityDetector(
            new ClassPathFactory().createFromPath(rtJarPath), classloadingBatchOptions, namesFromClassResources).call();

        String nonClassloadingResult = new RunMutabilityDetector(
            new ClassPathFactory().createFromPath(rtJarPath), nonClassloadingBatchOptions, namesFromClassResources).call();


        assertEquals(classLoadingResult, nonClassloadingResult);
    }

}
