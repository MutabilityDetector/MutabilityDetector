package org.mutabilitydetector.checkers.info;

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



import static org.mutabilitydetector.locations.Dotted.fromSlashed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.Slashed;

@Immutable
public final class MethodIdentifier {

    private final @Nonnull Dotted dottedClassName;
    private final @Nonnull String methodDescriptor;

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
        result = prime * result + dottedClassName.hashCode();
        result = prime * result + methodDescriptor.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        MethodIdentifier other = (MethodIdentifier) obj;
        
        return dottedClassName.equals(other.dottedClassName) 
                && methodDescriptor.equals(other.methodDescriptor);
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
