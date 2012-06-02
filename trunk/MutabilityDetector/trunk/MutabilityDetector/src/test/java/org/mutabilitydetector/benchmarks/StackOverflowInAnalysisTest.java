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
package org.mutabilitydetector.benchmarks;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.junit.Test;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.circular.AssignsItselfToField;
import org.mutabilitydetector.benchmarks.mutabletofield.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.mutabletofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.types.EnumType;
import org.mutabilitydetector.locations.Dotted;

public class StackOverflowInAnalysisTest {

    private final AnalysisSession session = ThreadUnsafeAnalysisSession.createWithCurrentClassPath();

    @Test
    public void innerClassDoesNotCauseStackOverflowError() throws Exception {
        session.resultFor(Dotted.fromClass(ImmutableExample.class));

        session.resultFor(Dotted.fromClass(AbstractStringContainer.class));
        session.resultFor(Dotted.fromClass(MutableByAssigningAbstractTypeToField.class));
        session.resultFor(Dotted.fromClass(AbstractStringContainer.class));
    }

    @Test
    public void visitingEnumTypeDoesNotCauseStackOverflowError() throws Exception {
        session.resultFor(Dotted.fromClass(EnumType.class));
    }

    @Test
    public void analysingThisTestDoesNotCauseStackOverflowError() throws Exception {
        session.resultFor(Dotted.fromClass(this.getClass()));
    }

    @Test
    public void analysingConstructorClassDoesNotCauseStackOverflow() throws Exception {
        // Constructor has a field of type 'Class'
        // Class has a field of type 'Constructor'
        session.resultFor(Dotted.fromClass(Constructor.class));
    }

    @Test
    public void aClassAssigningInstanceOfItsOwnTypeDoesNotCauseError() throws Exception {
        TestUtil.getAnalysisResult(AssignsItselfToField.class);
    }
    
    @Test public void
    HashMap$EntryDoesntCauseStackOverflow() {
        session.resultFor(Dotted.fromClass(HashMap.class)); 
    }
}
