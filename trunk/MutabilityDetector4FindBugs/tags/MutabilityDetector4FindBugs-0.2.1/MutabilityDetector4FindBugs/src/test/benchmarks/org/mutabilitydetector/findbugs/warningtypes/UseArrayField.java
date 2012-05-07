package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;


@Immutable
public final class UseArrayField {

    private String[] arrayField;

    public UseArrayField() {
        this.arrayField = new String[] { "first", "second" };
    }
    
    public String first() {
        return this.arrayField[0];
    }
    
}
