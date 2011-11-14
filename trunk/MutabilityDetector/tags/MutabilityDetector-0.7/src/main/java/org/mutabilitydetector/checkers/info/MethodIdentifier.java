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

package org.mutabilitydetector.checkers.info;

import static org.mutabilitydetector.locations.Dotted.fromSlashed;

import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.Slashed;

public class MethodIdentifier {

    private final Dotted dottedClassName;
    private final String methodDescriptor;

    public MethodIdentifier(Dotted className, String methodDescriptor) {
        this.dottedClassName = className;
        this.methodDescriptor = methodDescriptor;

    }

    @Override
    public String toString() {
        return dottedClassName + "." + methodDescriptor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dottedClassName == null) ? 0 : dottedClassName.hashCode());
        result = prime * result + ((methodDescriptor == null) ? 0 : methodDescriptor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        MethodIdentifier other = (MethodIdentifier) obj;
        if (dottedClassName == null) {
            if (other.dottedClassName != null) { return false; }
        } else if (!dottedClassName.equals(other.dottedClassName)) { return false; }
        if (methodDescriptor == null) {
            if (other.methodDescriptor != null) { return false; }
        } else if (!methodDescriptor.equals(other.methodDescriptor)) { return false; }
        return true;
    }

    public static MethodIdentifier forMethod(Dotted className, String methodDescriptor) {
        return new MethodIdentifier(className, methodDescriptor);
    }

    public static MethodIdentifier forMethod(Slashed className, String methodDescriptor) {
        Dotted dotted = fromSlashed(className);
        return new MethodIdentifier(dotted, methodDescriptor);
    }

    public Dotted dottedClassName() {
        return dottedClassName;
    }

    public String methodDescriptor() {
        return methodDescriptor;
    }

}
