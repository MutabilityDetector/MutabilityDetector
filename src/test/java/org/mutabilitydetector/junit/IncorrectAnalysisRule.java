package org.mutabilitydetector.junit;

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



import static java.lang.String.format;

import java.lang.annotation.Annotation;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class IncorrectAnalysisRule implements MethodRule {


    @Override
    public Statement apply(Statement base, final FrameworkMethod method, Object target) {
        if (isAnnotatedWith(FalsePositive.class, method)) {
            return expectFalsePositive(base);
        } else if (isAnnotatedWith(FalseNegative.class, method)) {
            return expectFalseNegative(base);
        }

        return base;
    }

    private Statement expectFalseNegative(Statement base) throws AssertionError {
        try {
            base.evaluate();
            return noOpStatement();
        } catch (Throwable e) {
            throw new AssertionError(format(
                    "The test is expected to pass with a False Negative result, " +
                    "i.e. the class is mutable but the test passes.%n" +
                    "This test is correctly failing, therefore the analysis is no longer incorrect, " +
                    "and the annotation can be removed."));
        }
        
    }

    private Statement expectFalsePositive(Statement base) throws AssertionError {
        try {
            base.evaluate();
        } catch (Throwable e) {
            return noOpStatement();
        }
        throw new AssertionError(format(
                "The test is expected to fail with a False Positive result, " +
                "i.e. the class is immutable but the test fails.%n" +
                "This test is correctly passing, therefore the analysis is no longer incorrect, " +
                "and the annotation can be removed."));
    }

    private boolean isAnnotatedWith(Class<? extends Annotation> annotation, FrameworkMethod method) {
        return isMethodAnnotatedWith(annotation, method) 
                || isClassAnnotated(annotation, (Class<?>) method.getMethod().getDeclaringClass());
    }


    private boolean isMethodAnnotatedWith(Class<? extends Annotation> annotation, FrameworkMethod method) {
        return method.getAnnotation(annotation) != null;
    }

    private boolean isClassAnnotated(Class<? extends Annotation> annotation, Class<?> klass) {
        return klass.getAnnotation(annotation) != null;
    }

    private Statement noOpStatement() {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
            }
        };
    }

}
