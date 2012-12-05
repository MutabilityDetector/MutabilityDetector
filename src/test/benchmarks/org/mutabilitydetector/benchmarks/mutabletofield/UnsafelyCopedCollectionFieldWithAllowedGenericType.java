package org.mutabilitydetector.benchmarks.mutabletofield;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class UnsafelyCopedCollectionFieldWithAllowedGenericType {

    public final List<Date> dates;

    public UnsafelyCopedCollectionFieldWithAllowedGenericType(List<Date> dates) {
        this.dates = Collections.unmodifiableList(dates);
    }
    
}
