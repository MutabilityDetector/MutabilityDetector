/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.locations;

public class Dotted extends ClassName {

    private Dotted(String className) {
        super(className);
    }

    public static Dotted dotted(String dottedClassName) {
        return new Dotted(dottedClassName);
    }

    public static Dotted fromSlashed(Slashed slashedClassName) {
        String converted = new ClassNameConvertor().dotted(slashedClassName.asString());
        return dotted(converted);
    }

    public static Dotted fromSlashedString(String slashedClassNameString) {
        String dottedClassNameString = new ClassNameConvertor().dotted(slashedClassNameString);
        return dotted(dottedClassNameString);
    }

    public static Dotted fromClass(Class<?> clazz) {
        return dotted(clazz.getName());
    }

}
