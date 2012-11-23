/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import org.hamcrest.Matcher;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 23.11.2012
 */
public interface AnalysisResultAsserter {

    /**
     * 
     * 
     * @param reasonMatcher 
     * @return an instance of this interface to enable method chaining.
     */
    AnalysisResultAsserter andOneReasonIsThat(Matcher<ParentAwareMutableReasonDetail> reasonMatcher);

    AnalysisResultAsserter andNoReasonIsThat(Matcher<ParentAwareMutableReasonDetail> reasonMatcher);

    AnalysisResultAsserter andTheReasonIsThat(Matcher<ParentAwareMutableReasonDetail> reasonMatcher);

}
