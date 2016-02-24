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


import com.google.common.base.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Dotted extends ClassName {

    private Dotted(String className) {
        super(className);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Dotted other = (Dotted) obj;
        return asString().equals(other.asString());
    }

    @Override
    public int hashCode() {
        return asString().hashCode();
    }

    public static final Function<String, Dotted> STRING_NAME_TO_DOTTED = new Function<String, Dotted>() {
        @Override public Dotted apply(@Nonnull String className) { return dotted(className); }
    };

    public static final Function<Class<?>, Dotted> CLASS_TO_DOTTED = new Function<Class<?>, Dotted>() {
        @Override public Dotted apply(@Nonnull Class<?> clazz) { return fromClass(clazz); }
    };

    public static final Function<Dotted, String> AS_SIMPLE_STRING = new Function<Dotted, String>() {
        @Nullable
        @Override
        public String apply(Dotted input) {
            return input.asSimpleString();
        }
    };

    public static Dotted dotted(String className) {
        return new Dotted(new ClassNameConverter().dotted(className));
    }

    public static Dotted fromSlashed(Slashed className) {
        String converted = new ClassNameConverter().dotted(className.asString());
        return dotted(converted);
    }

    public static Dotted fromSlashedString(String className) {
        String dottedClassNameString = new ClassNameConverter().dotted(className);
        return dotted(dottedClassNameString);
    }

    public static Dotted fromClass(Class<?> clazz) {
        return dotted(clazz.getName());
    }

}
