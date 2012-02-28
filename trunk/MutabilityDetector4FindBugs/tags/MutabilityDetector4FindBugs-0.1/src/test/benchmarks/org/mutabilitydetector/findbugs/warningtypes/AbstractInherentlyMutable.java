package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class AbstractInherentlyMutable {

    public static interface InterfaceInherentlyMutable {
        
    }
}
