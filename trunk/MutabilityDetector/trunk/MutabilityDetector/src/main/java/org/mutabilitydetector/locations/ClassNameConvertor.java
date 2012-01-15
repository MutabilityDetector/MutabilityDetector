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

/**
 * @author Graham Allan / Grundlefleck at gmail dot com
 */
public final class ClassNameConvertor {

    public String dotted(final String slashed) {
        String withNoArrayDescriptor = stripArrayDescriptorFromTypeArrayDescriptor(slashed);
        
        return withNoArrayDescriptor
                   .replace(".class", "")
                   .replace("/", ".")
                   .replace(";", "");
    }


    private String stripArrayDescriptorFromTypeArrayDescriptor(String slashed) {
        return slashed.contains("[L") 
            ? slashed.replace("[L", "")
                     .replace("[", "") // multi-dimensional arrays
            : slashed;
    }

    
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> forName = Class.forName("B");
        System.out.println(forName);
    }
}
