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
package org.mutabilitydetector;

import java.util.Collection;

import org.mutabilitydetector.checkers.info.AnalysisDatabase;

public interface IAnalysisSession {

    RequestedAnalysis resultFor(String className);

    void addAnalysisError(AnalysisError error);

    void runAnalysis(Collection<String> classNames);

    Collection<AnalysisResult> getResults();

    Collection<AnalysisError> getErrors();

    AnalysisDatabase analysisDatabase();

    public static final class AnalysisError {
        public final String checkerName;
        public final String description;
        public final String onClass;

        public AnalysisError(String onClass, String checkerName, String errorDescription) {
            this.onClass = onClass;
            this.checkerName = checkerName;
            this.description = errorDescription;
        }
    }
    
    public static final class RequestedAnalysis {
        public final AnalysisResult result;
        public final boolean analysisComplete;
        
        private RequestedAnalysis(AnalysisResult result) {
            this.result = result;
            this.analysisComplete = result != null;
        }
        
        public static RequestedAnalysis incomplete() {
            return new RequestedAnalysis(null);
        }
        
        public static RequestedAnalysis complete(AnalysisResult result) {
            return new RequestedAnalysis(result);
        }
    }

}
