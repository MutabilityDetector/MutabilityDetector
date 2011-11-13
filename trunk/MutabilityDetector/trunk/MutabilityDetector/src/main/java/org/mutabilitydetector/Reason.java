/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector;


/**
 * 
 * Provides an abstraction for the reason a class can be mutable. It is expected that checkers will provide a single
 * public instance of this class to act as a 'key' for the mutability reason. Other checkers can then use the reasons
 * already associated with a class to help their decision.
 * 
 * @author Graham Allan (grundlefleck@gmail.com)
 * 
 */
public interface Reason {

    String description();

    String code();

    IsImmutable createsResult();
    
    public boolean isOneOf(Reason... reasons);

}
