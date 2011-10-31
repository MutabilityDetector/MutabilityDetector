/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks.inheritance;

import org.mutabilitydetector.benchmarks.ImmutableExample;

public class ImmutableSupertype {
    @SuppressWarnings("unused")
    private final ImmutableExample immutableField;

    public ImmutableSupertype(ImmutableExample immutableField) {
        this.immutableField = immutableField;
    }
}

class MutableSubtypeOfImmutableSupertype extends ImmutableSupertype {
    public Object reassignableField = new Object();

    public MutableSubtypeOfImmutableSupertype(ImmutableExample immutableField) {
        super(immutableField);
    }
}

final class ImmutableSubtypeOfImmutableSupertype extends ImmutableSupertype {
    @SuppressWarnings("unused")
    private final int immutableField = 2;

    public ImmutableSubtypeOfImmutableSupertype(ImmutableExample immutableField) {
        super(immutableField);
    }

}
