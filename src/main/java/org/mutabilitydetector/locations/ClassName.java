package org.mutabilitydetector.locations;

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



import javax.annotation.Nonnull;

public abstract class ClassName {
    private final String asString;

    public ClassName(@Nonnull String className) {
        this.asString = className;
    }

    public String asString() {
        return asString;
    }

    /**
     * Similar to {@code ClassName#asString} but uses unqualified type names
     */
    public String asSimpleString() {
        int dotIndex = asString.lastIndexOf('.');
        if (dotIndex > 0) {
            return asString().substring(dotIndex + 1);
        }
        return asString();
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int hashCode() {
        return asString.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ClassName other = (ClassName) obj;
        return asString.equals(other.asString);
    }

    public abstract String asInternal();
}
