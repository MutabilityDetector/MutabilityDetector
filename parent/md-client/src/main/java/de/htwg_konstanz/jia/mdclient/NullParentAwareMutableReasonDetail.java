package de.htwg_konstanz.jia.mdclient;

import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.CodeLocation.UnknownCodeLocation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public enum NullParentAwareMutableReasonDetail implements ParentAwareMutableReasonDetail {

    /**
     * The sole instance of this implementation.
     */
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