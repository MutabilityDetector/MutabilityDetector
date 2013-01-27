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
package org.mutabilitydetector;

import javax.annotation.concurrent.Immutable;


public interface AnalysisErrorReporter {

    void addAnalysisError(AnalysisError error);

    @Immutable
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
}