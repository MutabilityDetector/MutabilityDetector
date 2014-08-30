package org.mutabilitydetector.benchmarks.visibility;

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

/**
 * Non final fields are not guaranteed to be visible after publishing
 * an instance of their enclosing class. However, if the last assignment
 * is to a volatile variable, it will be guaranteed the preceding assignments
 * are visible[citation needed].
 * 
 *
 */
public final class SafelyPublishesUsingVolatile {

    private int x;
    private int y;
    private volatile int z;
    
    public SafelyPublishesUsingVolatile(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
}
