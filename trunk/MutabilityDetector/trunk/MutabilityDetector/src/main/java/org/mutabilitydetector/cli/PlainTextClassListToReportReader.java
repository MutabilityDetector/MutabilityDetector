/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class PlainTextClassListToReportReader implements ClassListToReportCollector {

    private final BufferedReader reader;

    public PlainTextClassListToReportReader(BufferedReader reader) {
        this.reader = reader;

    }

    public Collection<String> classListToReport() {
        String line = null;
        Collection<String> classes = new HashSet<String>();
        try {
            while ((line = reader.readLine()) != null) {
                classes.add(line);
            }
        } catch (IOException e) {
            throw new ClassListException("I/O exception while reading class list.", e);
        }

        return classes;
    }

}
