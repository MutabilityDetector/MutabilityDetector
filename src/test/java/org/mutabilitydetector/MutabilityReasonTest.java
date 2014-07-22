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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.MutabilityReason.*;

import org.junit.Test;

public class MutabilityReasonTest {

    @Test public void isOneOfReturnsTrue() {
         assertTrue(NON_FINAL_FIELD.isOneOf(NON_FINAL_FIELD));
    }
    
    @Test public void isOneOfReturnsTrueWithSeveralArguments() {
        assertTrue(NON_FINAL_FIELD.isOneOf(ABSTRACT_TYPE_INHERENTLY_MUTABLE, NON_FINAL_FIELD, MUTABLE_TYPE_TO_FIELD));
   }
    
    @Test public void isOneOfReturnsFalseWhenNotEqualToAnyOfGivenReasons() {
        assertFalse(ESCAPED_THIS_REFERENCE.isOneOf(ABSTRACT_TYPE_INHERENTLY_MUTABLE, NON_FINAL_FIELD, MUTABLE_TYPE_TO_FIELD));
    }

    @Test public void isSuperseededByOneOfReturnsTrue() {
         assertTrue(NON_FINAL_FIELD.supersededByOneOf(PUBLISHED_NON_FINAL_FIELD, ABSTRACT_TYPE_INHERENTLY_MUTABLE));
    }

    @Test public void isSuperseededByOneOfReturnsFalse() {
        assertFalse(ESCAPED_THIS_REFERENCE.supersededByOneOf(NON_FINAL_FIELD, ABSTRACT_TYPE_INHERENTLY_MUTABLE));
    }
}
