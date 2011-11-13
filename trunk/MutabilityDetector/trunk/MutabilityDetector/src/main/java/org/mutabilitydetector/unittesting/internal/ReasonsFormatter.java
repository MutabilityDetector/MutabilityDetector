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

import org.mutabilitydetector.MutableReasonDetail;

public class ReasonsFormatter {
    private ReasonsFormatter() { }
    
    public static String formatReasons(Collection<MutableReasonDetail> reasons, StringBuilder builder) {
        builder.append(format("    Reasons:%n"));
        for (MutableReasonDetail reason : reasons) {
            builder.append(format("        %s %s%n", reason.message(), reason.codeLocation().prettyPrint()));
        }
        return builder.toString();
    }

    public static String formatReasons(Collection<MutableReasonDetail> reasons) {
        return formatReasons(reasons, new StringBuilder());
    }
}
