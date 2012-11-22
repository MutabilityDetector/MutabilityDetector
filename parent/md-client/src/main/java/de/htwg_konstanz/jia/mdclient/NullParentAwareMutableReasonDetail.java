package de.htwg_konstanz.jia.mdclient;

import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.CodeLocation.UnknownCodeLocation;

public enum NullParentAwareMutableReasonDetail implements ParentAwareMutableReasonDetail {
    INSTANCE;

    @Override
    public IsImmutable isImmutable() {
        return null;
    }

    @Override
    public String dottedClassName() {
        return "";
    }

    @Override
    public Reason reason() {
        return MutabilityReason.NULL_REASON;
    }

    @Override
    public CodeLocation<?> codeLocation() {
        return UnknownCodeLocation.UNKNOWN;
    }

    @Override
    public String message() {
        return "";
    }

    @Override
    public String toString() {
        return "NullParentAwareMutableReasonDetail []";
    }
    
}