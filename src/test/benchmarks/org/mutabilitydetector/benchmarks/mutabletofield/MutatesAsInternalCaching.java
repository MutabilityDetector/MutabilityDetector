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
package org.mutabilitydetector.benchmarks.mutabletofield;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class MutatesAsInternalCaching {
    private final String myString;
    private final String otherString;
    private int lengthWhenConcatenated;
    public MutatesAsInternalCaching(String myString, String otherString) {
        this.myString = myString;
        this.otherString = otherString;
    }
    
    public int getConcatenatedLength() {
        if (lengthWhenConcatenated == 0) {
//            lengthWhenConcatenated = myString.concat(otherString).length();
            lengthWhenConcatenated = getLengthWhenConcatenated();
        }
        return lengthWhenConcatenated;
    }

    private int getLengthWhenConcatenated() {
        return myString.concat(otherString).length();
    }
    
}
