/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;

import org.mutabilitydetector.cli.BatchAnalysisOptions;
import org.mutabilitydetector.cli.CommandLineOptions;
import org.mutabilitydetector.cli.RunMutabilityDetector;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public class CheckSomeClass {

    public static void main(String[] args) {
        checkClass(IAmImmutable.class);
        checkClass(ComposedOfImmutables.class);
    }

    private static void checkClass(Class<?> toAnalyse) {
        ClassPath cp = new ClassPathFactory().createFromJVM();
        String match = toAnalyse.getName().replace("$", "\\$");
        BatchAnalysisOptions options = new CommandLineOptions(System.err, "-verbose", "-match", match);
        new RunMutabilityDetector(cp, options).run();
    }

    public class IAmImmutable {
        private String label;

        public IAmImmutable(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    public static class SecondImmutable {
        private String label;

        public SecondImmutable(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }

    public class ComposedOfImmutables {
        public final IAmImmutable firstField;
        public final SecondImmutable secondField;

        public ComposedOfImmutables(IAmImmutable first, SecondImmutable second) {
            firstField = first;
            secondField = second;
        }
    }

}
