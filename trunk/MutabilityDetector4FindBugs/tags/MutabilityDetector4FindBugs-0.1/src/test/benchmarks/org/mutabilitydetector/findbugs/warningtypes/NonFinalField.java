package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class NonFinalField {

    private int x;

    public NonFinalField(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }
    
}
