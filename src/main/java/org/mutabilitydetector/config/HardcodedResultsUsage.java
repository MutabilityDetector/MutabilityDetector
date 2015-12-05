package org.mutabilitydetector.config;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2015 Graham Allan
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

import org.mutabilitydetector.unittesting.MutabilityAssert;

public enum HardcodedResultsUsage {
    /**
     * Hardcoded results are only used when looking up the mutability of a
     * class when it relates to the analysis of another class. For example,
     * lookup whether <code>Foo.class</code> is immutable because
     * <code>Bar.class</code> is being tested, and has a field of type
     * <code>Foo</code>. However, if <code>Foo</code> is tested directly
     * with e.g. <code>MutabilityAssert.assertImmutable(Foo.class);</code>
     * then the "real" analysis is performed, and the actual result is used
     * in the assertion.
     */
    LOOKUP_WHEN_REFERENCED,

    /**
     * Hardcoded results are used when invoking any of the assertions
     * available in {@link MutabilityAssert}. For example, if
     * <code>Foo.class</code> is hardcoded as immutable,
     * <code>MutabilityAssert.assertImmutable(Foo.class);</code> will
     * always pass.
     */
    DIRECTLY_IN_ASSERTION
}
