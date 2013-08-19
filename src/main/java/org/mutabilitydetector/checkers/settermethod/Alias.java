package org.mutabilitydetector.checkers.settermethod;

import javax.annotation.concurrent.Immutable;

@Immutable
final class Alias {
    public final boolean doesExist;
    public final int localVariable;

    private Alias(final boolean doesExist, final int localVariable) {
        this.doesExist = doesExist;
        this.localVariable = localVariable;
    }

    public static Alias newInstance(final boolean doesExist, final int localVariable) {
        return new Alias(doesExist, localVariable);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("Alias [").append("doesExist=").append(doesExist);
        b.append(", localVariable=").append(localVariable).append("]");
        return b.toString();
    }
}
