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

import static org.mutabilitydetector.locations.ClassLocation.from;
import static org.mutabilitydetector.locations.Dotted.fromClass;

import org.mutabilitydetector.MutabilityReason;

public final class NullMutabilityChecker extends AbstractMutabilityChecker {

    public NullMutabilityChecker() {
        addResult("This checker is a placeholder for a proper checker.",
                from(fromClass(NullMutabilityChecker.class)),
                MutabilityReason.CANNOT_ANALYSE);
    }

}
