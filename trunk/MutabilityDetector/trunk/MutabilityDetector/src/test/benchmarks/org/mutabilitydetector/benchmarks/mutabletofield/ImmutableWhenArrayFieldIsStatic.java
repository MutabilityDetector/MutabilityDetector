package org.mutabilitydetector.benchmarks.mutabletofield;

public class ImmutableWhenArrayFieldIsStatic {
    static final String names[] = new String[] { "Rick", "Levon", "Richard", "Garth", "Robbie" };

    static {
        names[4] = "Bob";
    }

    static void mutateStaticField() {
        names[4] = "Bobby";
    }
}