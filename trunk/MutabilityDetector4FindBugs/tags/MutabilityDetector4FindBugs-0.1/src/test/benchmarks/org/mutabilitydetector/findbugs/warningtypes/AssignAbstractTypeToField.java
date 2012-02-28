package org.mutabilitydetector.findbugs.warningtypes;

import java.util.List;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class AssignAbstractTypeToField {

    private final List<String> couldBeAnyImplementation;

    public AssignAbstractTypeToField(List<String> couldBeAnyImplementation) {
        this.couldBeAnyImplementation = couldBeAnyImplementation;
    }

    public String firstString() {
        return couldBeAnyImplementation.get(0);
    }
    
}
