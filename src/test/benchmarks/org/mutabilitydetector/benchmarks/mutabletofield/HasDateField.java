package org.mutabilitydetector.benchmarks.mutabletofield;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class HasDateField {
    private final Date myDate;
    
    public HasDateField(Date date) {
        this.myDate = new Date(date.getTime());
    }
    
    public Date getDate() {
        return new Date(myDate.getTime());
    }
}