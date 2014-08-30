package org.mutabilitydetector.checkers;

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



import static org.mutabilitydetector.IsImmutable.COULD_NOT_ANALYSE;
import static org.mutabilitydetector.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IsImmutable.NOT_IMMUTABLE;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.IsImmutable;

@Immutable
public final class ResultCalculator {
    
    public IsImmutable calculateImmutableStatus(Map<IsImmutable, Integer> results) {
        
        int numCouldNotAnalyse = getNumOfResult(results, COULD_NOT_ANALYSE);
        int numDefinitely = getNumOfResult(results, IMMUTABLE);
        int numEffectively = getNumOfResult(results, EFFECTIVELY_IMMUTABLE);
        int numDefinitelyNot = getNumOfResult(results, NOT_IMMUTABLE);

        if (numDefinitelyNot > 0) {
            return NOT_IMMUTABLE;
        } else if (numCouldNotAnalyse > 0) {
            return COULD_NOT_ANALYSE;
        } else if (numEffectively > 0) {
            return EFFECTIVELY_IMMUTABLE;
        } else if (numDefinitely > 0) {
            return IMMUTABLE;
        } else {
            return NOT_IMMUTABLE;
        }
    }

    private int getNumOfResult(Map<IsImmutable, Integer> results, IsImmutable resultType) {
        return results.containsKey(resultType) ? results.get(resultType) : 0;
    }
}
