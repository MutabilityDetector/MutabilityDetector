package org.mutabilitydetector.benchmarks.checkers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImmutableWithIfStatementAndVarInsn {
    private final List<String> roles;
    
    public ImmutableWithIfStatementAndVarInsn   (final List<String> roles){
        if(roles==null) {
           this.roles = new ArrayList<String>(); 
        }else {
            this.roles = Collections.unmodifiableList(new ArrayList<String>());
        }
    }
}
