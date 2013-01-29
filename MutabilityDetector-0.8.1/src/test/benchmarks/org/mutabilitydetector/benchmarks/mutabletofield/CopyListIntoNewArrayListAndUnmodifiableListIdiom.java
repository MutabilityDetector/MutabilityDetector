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
package org.mutabilitydetector.benchmarks.mutabletofield;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CopyListIntoNewArrayListAndUnmodifiableListIdiom {

    private final List<String> unmodifiable;
    
    public CopyListIntoNewArrayListAndUnmodifiableListIdiom(List<String> potentiallyMutatable) {
        this.unmodifiable = Collections.unmodifiableList(new ArrayList<String>(potentiallyMutatable));
    }
    
    public List<String> getUnmodifiable() {
        return unmodifiable;
    }
    
    public final static class StaticMethodDoesTheCopying {
        private final List<String> unmodifiable;

        private StaticMethodDoesTheCopying(List<String> unmodifiable) {
            this.unmodifiable = unmodifiable;
        }
        
        public static StaticMethodDoesTheCopying create(List<String> potentiallyMutatable) {
            return new StaticMethodDoesTheCopying(Collections.unmodifiableList(new ArrayList<String>(potentiallyMutatable)));
        }
         
        public List<String> getUnmodifiable() {
            return unmodifiable;
        }
        
    }
    
}
