package org.mutabilitydetector.checkers.info;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
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

import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.line.LineNumbers;
import org.mutabilitydetector.locations.line.SourceLocation;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mutabilitydetector.locations.line.SourceLocation.newSourceLocation;
import static org.mutabilitydetector.locations.line.SourceLocation.newUnknownSourceLocation;

public final class LineNumberInformation implements AnalysisInformation {
    private InformationRetrievalRunner sessionCheckerRunner;
    private Set<Dotted> lineNumberInfoMissing = new HashSet<Dotted>();
    private Map<Dotted, LineNumbers> lineNumbersInfoByClass = new HashMap<Dotted, LineNumbers>();

    public LineNumberInformation(InformationRetrievalRunner sessionCheckerRunner) {
        this.sessionCheckerRunner = sessionCheckerRunner;
    }

    public SourceLocation getFieldLocation(Dotted type, String fieldName) {
        // If we tried to analyze this type in past and failed
        if (lineNumberInfoMissing.contains(type)) {
            return newUnknownSourceLocation();
        }

        LineNumbers analyzer = getOrCreateAnalyzer(type);
        // If analyzer can't be created
        if (analyzer == null) {
            return newUnknownSourceLocation();
        }

        String sourceName = analyzer.getSource();
        Integer fieldLine = analyzer.getLineNumberOfField(fieldName);

        // If type does not contain required field
        if (fieldLine == null) {
            return newSourceLocation(sourceName, analyzer.getFirstLine());
        }

        // Everything is ok, return location
        return newSourceLocation(sourceName, fieldLine);
    }

    /**
     * Returns analyzer for given type or {@code null} if can't be created.
     */
    private LineNumbers getOrCreateAnalyzer(Dotted dotted) {
        if (!lineNumbersInfoByClass.containsKey(dotted)) {
            try {
                LineNumbers analyzer = new LineNumbers(typeFromDotted(dotted));
                lineNumbersInfoByClass.put(dotted, analyzer);
            } catch (IOException e) {
                lineNumberInfoMissing.add(dotted);
            }
        }

        return lineNumbersInfoByClass.get(dotted);
    }

    private Type typeFromDotted(Dotted dotted) {
        return Type.getType(dotted.asString().replace('.', '/'));
    }

}
