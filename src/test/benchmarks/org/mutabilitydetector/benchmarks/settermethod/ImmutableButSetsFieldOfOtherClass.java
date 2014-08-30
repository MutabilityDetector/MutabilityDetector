package org.mutabilitydetector.benchmarks.settermethod;

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

/**
 * This is for checking against a silly mistake in analysis, where only a field being set is being checked for,
 * regardless of what object holds the field.
 */
public class ImmutableButSetsFieldOfOtherClass {
    @SuppressWarnings("unused")
    private int myField = 43;

    public void setFieldOnParameter(AssignMyField otherObject) {
        this.toString(); // Ensure the 'this' reference is involved somewhere
        otherObject.reassignable = 42;
    }

}

class AssignMyField {
    public int reassignable;
}
