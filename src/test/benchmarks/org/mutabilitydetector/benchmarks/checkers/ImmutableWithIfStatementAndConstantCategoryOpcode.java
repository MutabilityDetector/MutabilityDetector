package org.mutabilitydetector.benchmarks.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImmutableWithIfStatementAndConstantCategoryOpcode {
    private final List<String> roles;
    
    public ImmutableWithIfStatementAndConstantCategoryOpcode(final List<String> roles){
        if(roles==null) {
           this.roles = null; 
        }else {
            this.roles = Collections.unmodifiableList(new ArrayList<String>(roles));
        }
    }
}
