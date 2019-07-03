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



import java.util.function.Function;
import javax.annotation.concurrent.Immutable;

/**
 * @author Graham Allan / Grundlefleck at gmail dot com
 */
@Immutable
public final class ClassNameConverter {

    public static final ClassNameConverter CONVERTER = new ClassNameConverter();

    public String dotted(final String givenClassName) {
        return SINGLE_DIMENSIONAL_IF_ARRAY
                .andThen(REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE)
                .andThen(REMOVE_REFERENCE_DESCRIPTOR_IF_REFERENCE_TYPE)
                .andThen(REMOVE_CLASS_EXTENSION)
                .andThen(REMOVE_TRAILING_SEMICOLON)
                .andThen(REPLACE_SLASHES_WITH_DOTS)
                .apply(givenClassName);
    }

    public String toInternal(Dotted dotted) {
        return "L" + dotted.asSimpleString().replace(".", "/") + ";";
    }

    public String toInternal(Slashed slashed) {
        return "L" + slashed.asSimpleString() + ";";
    }

    private static final Function<String, String> SINGLE_DIMENSIONAL_IF_ARRAY = s -> s.replaceAll("\\[+", "[");

    private static final Function<String, String> REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE =
            s -> s.startsWith("[L") ? s.replace("[L", "") : s;

    private static final Function<String, String> REMOVE_REFERENCE_DESCRIPTOR_IF_REFERENCE_TYPE =
            s -> s.startsWith("L") ? s.substring(1) : s;
    
    private static final Function<String, String> REMOVE_CLASS_EXTENSION =
            s -> s.endsWith(".class") ? s.replace(".class", "") : s;

    private static final Function<String, String> REMOVE_TRAILING_SEMICOLON = s -> s.replace(";", "");

    private static final Function<String, String> REPLACE_SLASHES_WITH_DOTS = s -> s.replace("/", ".");

}
