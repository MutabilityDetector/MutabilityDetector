package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class PublishedNonFinalField {

    public int x;

    public PublishedNonFinalField(int x) {
        this.x = x;
    }
    
}
