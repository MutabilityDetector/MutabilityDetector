package org.mutabilitydetector.findbugs;

import net.jcip.annotations.Immutable;

@Immutable
public final class ImmutableExample {

    public final int x;
    
    public ImmutableExample(int x) {
        this.x = x;
    }
    
}
