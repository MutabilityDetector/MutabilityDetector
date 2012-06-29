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

import static com.google.common.collect.FluentIterable.from;
import static java.util.Collections.singleton;

import javax.annotation.concurrent.Immutable;


import com.google.common.base.Function;

/**
 * @author Graham Allan / Grundlefleck at gmail dot com
 */
@Immutable
public final class ClassNameConverter {

	private static final ClassNameConverter CONVERTER = new ClassNameConverter();
    public static final Function<String, String> TO_DOTTED_STRING = new Function<String, String>() {
		@Override public String apply(String input) { return CONVERTER.dotted(input); }
    };

	public String dotted(final String givenClassName) {
	    return from(singleton(givenClassName))
	            .transform(SINGLE_DIMENSIONAL_IF_ARRAY)
	            .transform(REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE)
	            .transform(REMOVE_CLASS_EXTENSION)
	            .transform(REMOVE_TRAILING_SEMICOLON)
	            .transform(REPLACE_SLASHES_WITH_DOTS)
	            .first().get();
    }

	private static final Function<String, String> SINGLE_DIMENSIONAL_IF_ARRAY = new Function<String, String>() {
	    @Override public String apply(String input) { return input.replaceAll("\\[+", "["); }
	};

	private static final Function<String, String> REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE = new Function<String, String>() {
	    @Override public String apply(String input) { return input.startsWith("[L") ? input.replace("[L", "") : input; }
	};
	
	private static final Function<String, String> REMOVE_CLASS_EXTENSION = new Function<String, String>() {
	    @Override public String apply(String input) { return input.endsWith(".class") ? input.replace(".class", "") : input; }
	};

	private static final Function<String, String> REMOVE_TRAILING_SEMICOLON = new Function<String, String>() {
	    @Override public String apply(String input) { return input.replace(";", ""); }
	};

	private static final Function<String, String> REPLACE_SLASHES_WITH_DOTS = new Function<String, String>() {
	    @Override public String apply(String input) { return input.replace("/", "."); }
	};
    
}
