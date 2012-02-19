package org.mutabilitydetector.checkers;

import static org.junit.Assert.*;

import org.junit.Test;

public class MethodIsTest {

    @Test
    public void correctlyDeterminesIfMethodIsAConstructor() throws Exception {
        assertTrue(MethodIs.aConstructor("<init>"));
        assertFalse(MethodIs.aConstructor("anotherMethod"));
    }
    
}
