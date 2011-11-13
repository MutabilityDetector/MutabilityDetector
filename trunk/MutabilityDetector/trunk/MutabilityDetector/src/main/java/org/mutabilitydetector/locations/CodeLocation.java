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

public interface CodeLocation<T extends CodeLocation<T>> extends Comparable<T> {

    String typeName();

    public abstract String prettyPrint();
    
    public final static class UnknownCodeLocation implements CodeLocation<UnknownCodeLocation> {

        public static final UnknownCodeLocation UNKNOWN = new UnknownCodeLocation();
        
        private UnknownCodeLocation() { }
        
        @Override
        public int compareTo(UnknownCodeLocation o) {
            return 0; // There can be only one.
        }

        @Override
        public String typeName() {
            return "unknown code location";
        }

        @Override
        public String prettyPrint() {
            return "[Unknown code location]";
        }
        
    }

}
