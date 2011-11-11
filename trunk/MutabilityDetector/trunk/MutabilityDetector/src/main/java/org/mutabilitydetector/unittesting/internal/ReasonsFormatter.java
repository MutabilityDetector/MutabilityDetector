/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.unittesting.internal;

import static java.lang.String.format;

import java.util.Collection;

import org.mutabilitydetector.CheckerReasonDetail;

public class ReasonsFormatter {
    private ReasonsFormatter() { }
    
    public static String formatReasons(Collection<CheckerReasonDetail> reasons, StringBuilder builder) {
        builder.append(format("    Reasons:%n"));
        for (CheckerReasonDetail reason : reasons) {
            builder.append(format("        %s%n", reason.message()));
        }
        return builder.toString();
    }

    public static String formatReasons(Collection<CheckerReasonDetail> reasons) {
        return formatReasons(reasons, new StringBuilder());
    }
}
