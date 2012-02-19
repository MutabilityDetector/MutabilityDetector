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
