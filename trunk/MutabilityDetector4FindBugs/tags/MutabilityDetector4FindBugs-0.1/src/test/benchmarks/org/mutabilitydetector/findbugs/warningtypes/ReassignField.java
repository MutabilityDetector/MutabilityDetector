package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class ReassignField {

    private int x;

    public ReassignField(int x) {
        this.x = x;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}

