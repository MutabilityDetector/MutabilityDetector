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
package org.mutabilitydetector.benchmarks;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Benchmark discussed on the project lombok mailing list, <a
 * href="http://groups.google.com/group/project-lombok/browse_thread/thread/553c771b5006e18f/0393e4c1ae41099c" >
 * here</a>
 * 
 */
public final class Fakeout {
    private static final Map<Fakeout, Integer> map = new IdentityHashMap<Fakeout, Integer>();

    public Fakeout() {
        map.put(this, 0);
    }

    public void setAge(int age) {
        map.put(this, age);
    }

    public int getAge() {
        return map.get(this);
    }
}
