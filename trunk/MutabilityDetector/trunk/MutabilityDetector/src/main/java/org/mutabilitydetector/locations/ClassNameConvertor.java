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

/**
 * @author Graham Allan (grundlefleck@gmail.com)
 * @date 8 Apr 2010
 * 
 */
public class ClassNameConvertor {

    public String dotted(String slashed) {
        return slashed.replace(".class", "").replace("/", ".");
    }

}
