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
package org.mutabilitydetector.checkers;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy;

import com.google.classpath.ClassPath;

@Immutable
public final class ClassPathBasedCheckerRunnerFactory implements CheckerRunnerFactory {

    private final ClassPath classpath;
    private final ExceptionPolicy exceptionPolicy;

    public ClassPathBasedCheckerRunnerFactory(ClassPath classpath, ExceptionPolicy exceptionPolicy) {
        this.classpath = classpath;
        this.exceptionPolicy = exceptionPolicy;
    }

    @Override
    public CheckerRunner createRunner() {
        return CheckerRunner.createWithClasspath(classpath, exceptionPolicy);
    }

}
