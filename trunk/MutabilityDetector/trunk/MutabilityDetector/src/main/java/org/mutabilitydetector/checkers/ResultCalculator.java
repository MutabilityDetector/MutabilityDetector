/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.checkers;

import static java.lang.Integer.valueOf;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.EFFECTIVELY_IMMUTABLE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.IMMUTABLE;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.NOT_IMMUTABLE;

import java.util.Map;

import org.mutabilitydetector.IAnalysisSession.IsImmutable;

public final class ResultCalculator {
    public IsImmutable calculateImmutableStatus(Map<IsImmutable, Integer> results) {
        IsImmutable isImmutable;
        int numDefinitely = getNumOfResult(results, IMMUTABLE);
        int numMaybe = getNumOfResult(results, EFFECTIVELY_IMMUTABLE);
        int numDefinitelyNot = getNumOfResult(results, NOT_IMMUTABLE);

        if (numDefinitelyNot > 0) {
            isImmutable = NOT_IMMUTABLE;
        } else if (numMaybe > 0) {
            isImmutable = EFFECTIVELY_IMMUTABLE;
        } else if (numDefinitely > 0) {
            isImmutable = IMMUTABLE;
        } else {
            isImmutable = EFFECTIVELY_IMMUTABLE;
        }

        return isImmutable;
    }

    private int getNumOfResult(Map<IsImmutable, Integer> results, IsImmutable resultType) {
        if (!results.containsKey(resultType)) { return valueOf(0); }

        Integer numOfResultType = valueOf(results.get(resultType));
        if (numOfResultType != null) {
            return numOfResultType.intValue();
        } else {
            return valueOf(0);
        }
    }
}
