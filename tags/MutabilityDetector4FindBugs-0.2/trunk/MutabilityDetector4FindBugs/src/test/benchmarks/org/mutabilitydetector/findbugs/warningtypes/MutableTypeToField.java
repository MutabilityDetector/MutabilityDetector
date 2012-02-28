package org.mutabilitydetector.findbugs.warningtypes;

import java.util.ArrayList;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class MutableTypeToField {

    private final ArrayList<String> mutable;

    public MutableTypeToField(ArrayList<String> mutable) {
        this.mutable = mutable;
    }
    
    public String first() {
        return mutable.get(0);
    }
    
}
