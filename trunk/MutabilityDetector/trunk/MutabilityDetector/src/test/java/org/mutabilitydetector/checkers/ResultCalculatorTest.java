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
package org.mutabilitydetector.checkers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mutabilitydetector.IsImmutable;

public class ResultCalculatorTest {

    @Test public void aSingleCouldNotAnalyseResultRendersResultCouldNotAnalyse() {
         ResultCalculator calculator = new ResultCalculator();
         Map<IsImmutable, Integer> results = new HashMap<IsImmutable, Integer>();
         results.put(IsImmutable.COULD_NOT_ANALYSE, 1);
         
         assertEquals(IsImmutable.COULD_NOT_ANALYSE, calculator.calculateImmutableStatus(results));
    }

    @Test public void couldNotAnalyseTakesPrecedenceOverImmutableAndEffectivelyImmutable() {
        ResultCalculator calculator = new ResultCalculator();
        
        Map<IsImmutable, Integer> results = new HashMap<IsImmutable, Integer>();
        results.put(IsImmutable.COULD_NOT_ANALYSE, 1);
        results.put(IsImmutable.EFFECTIVELY_IMMUTABLE, 2);
        results.put(IsImmutable.IMMUTABLE, 2);
        
        assertEquals(IsImmutable.COULD_NOT_ANALYSE, calculator.calculateImmutableStatus(results));
    }
    
    @Test public void notImmutableTakesPrecedenceOverCouldNotAnalyse_weKnowItsAlreadyNotImmutableJustReportItThatWay() {
        ResultCalculator calculator = new ResultCalculator();
        
        Map<IsImmutable, Integer> results = new HashMap<IsImmutable, Integer>();
        results.put(IsImmutable.COULD_NOT_ANALYSE, 2);
        results.put(IsImmutable.NOT_IMMUTABLE, 1);
        
        assertEquals(IsImmutable.NOT_IMMUTABLE, calculator.calculateImmutableStatus(results));
         
    }
    
}
