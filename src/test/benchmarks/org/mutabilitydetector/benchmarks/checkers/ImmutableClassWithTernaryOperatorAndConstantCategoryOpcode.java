package org.mutabilitydetector.benchmarks.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImmutableClassWithTernaryOperatorAndConstantCategoryOpcode {
    private final List<String> roles;
    
    public ImmutableClassWithTernaryOperatorAndConstantCategoryOpcode(final List<String> roles){
        this.roles = roles==null ? null : Collections.unmodifiableList(new ArrayList<String>(roles));
    }
}
