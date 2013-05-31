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

package org.mutabilitydetector.locations;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ClassLocation implements CodeLocation<ClassLocation> {

    private final @Nonnull String dottedClassName;

    public ClassLocation(String dottedClassName) {
        this.dottedClassName = dottedClassName;
    }

    @Override
    public String typeName() {
        return dottedClassName;
    }

    @Override
    public int compareTo(ClassLocation other) {
        return typeName().compareTo(other.typeName());
    }

    @Override
    public int hashCode() {
        return dottedClassName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ClassLocation other = (ClassLocation) obj;

        return dottedClassName.equals(other.dottedClassName);
    }

    public static ClassLocation fromInternalName(String internalClassName) {
        String dottedClassName = new ClassNameConverter().dotted(internalClassName);
        return new ClassLocation(dottedClassName);
    }

    public static ClassLocation from(Slashed slashed) {
        String dottedClassName = ClassIdentifier.forClass(slashed).asDotted().asString();
        return new ClassLocation(dottedClassName);
    }

    public static ClassLocation from(Dotted dotted) {
        return new ClassLocation(dotted.asString());
    }

    @Override
    public String prettyPrint() {
        return String.format("[Class: %s]", typeName());
    }

}
