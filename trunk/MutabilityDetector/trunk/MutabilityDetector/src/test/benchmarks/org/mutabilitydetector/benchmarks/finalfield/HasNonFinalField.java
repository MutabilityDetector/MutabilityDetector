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
package org.mutabilitydetector.benchmarks.finalfield;

public final class HasNonFinalField {
    public final int isFinal;
    private int isNotFinal;
    
    public HasNonFinalField(int whatever) {
        isNotFinal = whatever;
        isFinal = whatever;
    }
    
    public int getIsNotFinal() {
        return isNotFinal;
    }
    
}
