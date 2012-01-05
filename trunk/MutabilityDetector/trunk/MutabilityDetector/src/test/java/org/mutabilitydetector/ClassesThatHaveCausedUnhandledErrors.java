package org.mutabilitydetector;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areNotImmutable;

import org.junit.Test;

public class ClassesThatHaveCausedUnhandledErrors {

    
    @Test
    public void xpDefaultRenderer() throws Exception {
        Class<?>[] classes = new com.sun.java.swing.plaf.windows.WindowsTableHeaderUI().getClass().getDeclaredClasses();
        assertInstancesOf(classes[2], areNotImmutable());
    }
}
