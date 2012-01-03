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
package org.mutabilitydetector.benchmarks.finalfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.NonFinalFieldChecker;

public class NonFinalFieldsCheckerTest {

    
    private final AsmMutabilityChecker checker = new NonFinalFieldChecker();


    @Test
    public void remainsImmutableWhenFieldIsFinal() throws Exception {
        TestUtil.runChecker(checker, ImmutableExample.class);
        assertEquals(IsImmutable.IMMUTABLE, checker.result());
    }
    
    
    @Test
    public void isEffectivelyImmutableWhenTheFieldIsNotDeclaredFinal() throws Exception {
        TestUtil.runChecker(checker, HasNonFinalField.class);
        assertEquals(IsImmutable.EFFECTIVELY_IMMUTABLE, checker.result());
    }
}
