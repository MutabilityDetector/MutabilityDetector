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
package org.mutabilitydetector.benchmarks;

public final class MutableByHavingMutableFieldAssigned {
    private MutableExample mutableField; // Access level doesn't matter

    public MutableByHavingMutableFieldAssigned(MutableExample mutableField) {
        this.mutableField = mutableField;
    }

    public MutableExample getMutableField() {
        new PublishTarget().publishMutableField(mutableField);
        return mutableField;
    }
}

class MutableExample {
    public String name;
}

class PublishTarget {
    public void publishMutableField(MutableExample mutableField) {
    }
}
