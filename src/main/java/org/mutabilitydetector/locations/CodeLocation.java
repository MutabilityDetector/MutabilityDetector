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



import javax.annotation.concurrent.Immutable;

public interface CodeLocation<T extends CodeLocation<T>> extends Comparable<T> {

    String typeName();

    public abstract String prettyPrint();
    
    @Immutable
    public final static class UnknownCodeLocation implements CodeLocation<UnknownCodeLocation> {

        public static final UnknownCodeLocation UNKNOWN = new UnknownCodeLocation();
        
        private UnknownCodeLocation() { }
        
        @Override
        public int compareTo(UnknownCodeLocation o) {
            return 0; // There can be only one.
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }
        
        @Override
        public int hashCode() {
            return 0;
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
