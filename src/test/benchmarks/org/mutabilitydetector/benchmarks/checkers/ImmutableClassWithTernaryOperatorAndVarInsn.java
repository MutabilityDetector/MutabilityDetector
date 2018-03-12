package org.mutabilitydetector.benchmarks.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImmutableClassWithTernaryOperatorAndVarInsn {
private final List<String> roles;
    
    public ImmutableClassWithTernaryOperatorAndVarInsn(final List<String> roles){
        this.roles = roles==null ? new ArrayList<String>() : Collections.unmodifiableList(new ArrayList<String>(roles));
    }
}
