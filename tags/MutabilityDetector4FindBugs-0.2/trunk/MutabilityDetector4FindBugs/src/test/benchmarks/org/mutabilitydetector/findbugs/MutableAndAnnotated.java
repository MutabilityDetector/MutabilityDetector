package org.mutabilitydetector.findbugs;

import net.jcip.annotations.Immutable;

@Immutable
public class MutableAndAnnotated {
    public int[] mutable = new int[2];
}
