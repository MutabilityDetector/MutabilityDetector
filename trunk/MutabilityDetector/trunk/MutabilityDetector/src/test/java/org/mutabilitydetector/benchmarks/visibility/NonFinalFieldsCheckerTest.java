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
package org.mutabilitydetector.benchmarks.visibility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.TestUtil.runChecker;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areEffectivelyImmutable;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableUsingPrivateFieldSettingMethod;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.checkers.NonFinalFieldChecker;

public class NonFinalFieldsCheckerTest {

    
    private final AsmMutabilityChecker checker = new NonFinalFieldChecker();


    @Test
    public void remainsImmutableWhenFieldIsFinal() throws Exception {
        assertThat(runChecker(checker, ImmutableExample.class), areImmutable());
    }
    
    
    @Test
    public void isEffectivelyImmutableWhenTheFieldIsNotDeclaredFinal() throws Exception {
        assertThat(runChecker(checker, HasNonFinalField.class), areEffectivelyImmutable());
    }
    
    @Test
    public void isMutableForNonFinalFieldsSetOnlyInConstructor() throws Exception {
        assertThat(runChecker(checker, ImmutableUsingPrivateFieldSettingMethod.class), areEffectivelyImmutable());
    }
}
