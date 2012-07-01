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

import static java.util.Collections.singleton;

import java.util.Collections;
import java.util.functions.Mapper;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Function;

/**
 * @author Graham Allan / Grundlefleck at gmail dot com
 */
@Immutable
public final class ClassNameConverter {

	public static String toDottedString(final String givenClassName) {
	    return singleton(givenClassName)
	            .map(MAKE_SINGLE_DIMENSIONAL_IF_ARRAY)
	            .map(REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE)
	            .map(REMOVE_CLASS_EXTENSION)
	            .map(REMOVE_TRAILING_SEMI_COLON)
	            .map(REPLACE_SLASHES_WITH_DOTS)
	            .getFirst();
    }
	
	private static final Mapper<String, String> 
		MAKE_SINGLE_DIMENSIONAL_IF_ARRAY = input -> input.replaceAll("\\[+", "[");
			
	private static final Mapper<String, String> 
		REMOVE_ARRAY_DESCRIPTOR_IF_REFERENCE_TYPE = input -> input.startsWith("[L") ? input.replace("[L", "") : input;
	
	private static final Mapper<String, String> 
		REMOVE_CLASS_EXTENSION = input -> input.endsWith(".class") ? input.replace(".class", "") : input;
	
	private static final Mapper<String, String> 
		REMOVE_TRAILING_SEMI_COLON = input -> input.replace(";", "");
	
	private static final Mapper<String, String> 
		REPLACE_SLASHES_WITH_DOTS = input -> input.replace("/", ".");

}
