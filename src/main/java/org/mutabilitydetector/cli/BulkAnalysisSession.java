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
package org.mutabilitydetector.cli;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.locations.Dotted;

public final class BulkAnalysisSession {
    private final AnalysisSession session;

    public BulkAnalysisSession(AnalysisSession session) {
        this.session = session;
    }

    public AnalysisSession runAnalysis(Iterable<Dotted> classNames) {
        for (Dotted className : classNames) {
            session.resultFor(className);
        }
        return session;
    }
}
