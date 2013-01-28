package org.mutabilitydetector.checkers;

public final class MethodIs {
    private MethodIs() { }

    public static boolean aConstructor(String name) {
        return name.equals("<init>");
    }

}
