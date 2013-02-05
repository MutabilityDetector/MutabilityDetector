package de.htwg_konstanz.jia.lazyinitialisation;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 04.02.2013
 */
public final class LazyInitializationCheckerTest {

    @Test
    public void startVerification() throws IOException {
        final ClassName dotted = Dotted.fromClass(IntegerSingleCheckLazyInitialisation.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        checker.result();
    }

    @Test
    @Ignore
    public void printJumpsOpcodes() {
        for (final Opcode opcode : Opcode.jumps()) {
            System.out.println(String.format("Opcode '%s', number: '%d'.", opcode.name(), opcode.asInt()));
        }
    }

}
