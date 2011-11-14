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

package org.mutabilitydetector.benchmarks.settermethod;

@SuppressWarnings("unused")
public class ImmutableButSetsPrivateFieldOfInstanceOfSelf {

    private Object myField = null;
    private int primitiveField = 0;
    private ImmutableButSetsPrivateFieldOfInstanceOfSelf fieldOfSelfType = null;

    public ImmutableButSetsPrivateFieldOfInstanceOfSelf setPrivateFieldOnInstanceOfSelf() {
        ImmutableButSetsPrivateFieldOfInstanceOfSelf i = new ImmutableButSetsPrivateFieldOfInstanceOfSelf();
        this.hashCode();
        i.myField = new Object();
        this.hashCode();
        i.primitiveField++;
        return i;
    }

}

class MutableBySettingFieldOnThisInstanceAndOtherInstance {
    @SuppressWarnings("unused")
    private int myField = 0;

    public void setMyField(int newMyField, MutableBySettingFieldOnThisInstanceAndOtherInstance otherInstance) {
        this.myField = newMyField;
        otherInstance.myField = 42;

    }
}
