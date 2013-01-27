/*
 *    Copyright (c) 2008-2013 Graham Allan
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
package org.mutabilitydetector.benchmarks.sealed;

/**
 * This class is not final, but it also cannot be subclassed, since all of its constructors are private.
 * 
 */
public class ImmutableByHavingOnlyPrivateConstructors {

    private final int something;
    
    private ImmutableByHavingOnlyPrivateConstructors(int something) {
        this.something = something;
       
    }
    
    private ImmutableByHavingOnlyPrivateConstructors() {
        this(43);
        
    }

    public int getSomething() {
        return something;
    }
    
    public static ImmutableByHavingOnlyPrivateConstructors newUp(int something) {
        return new ImmutableByHavingOnlyPrivateConstructors(something);
    }
    
}
