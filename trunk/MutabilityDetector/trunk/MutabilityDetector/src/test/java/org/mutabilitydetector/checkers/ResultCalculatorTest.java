package org.mutabilitydetector.checkers;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;

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
