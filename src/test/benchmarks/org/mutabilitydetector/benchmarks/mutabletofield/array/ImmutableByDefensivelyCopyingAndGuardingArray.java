package org.mutabilitydetector.benchmarks.mutabletofield.array;

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

import java.util.Arrays;

public final class ImmutableByDefensivelyCopyingAndGuardingArray {

    private final Integer[] defensivelyCopied;

    public ImmutableByDefensivelyCopyingAndGuardingArray(Integer[] unsafe) {
        defensivelyCopied = new Integer[unsafe.length];
        System.arraycopy(unsafe, 0, defensivelyCopied, 0, unsafe.length);
    }
    
    public ImmutableByDefensivelyCopyingAndGuardingArray(Integer[] unsafe, int length) {
        defensivelyCopied = Arrays.copyOf(unsafe, length);
    }

    public Integer first() {
        return defensivelyCopied[0];
    }
    
}
