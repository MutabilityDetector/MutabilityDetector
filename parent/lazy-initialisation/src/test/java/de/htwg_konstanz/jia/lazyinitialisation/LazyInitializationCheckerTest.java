package de.htwg_konstanz.jia.lazyinitialisation;

import java.io.IOException;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.FloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithSemanticDefault;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 04.02.2013
 */
public final class LazyInitializationCheckerTest {

    @Test
    public void verifyIntegerSingleCheckLI() throws IOException {
        final ClassName dotted = Dotted.fromClass(IntegerWithDefault.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        checker.result();
    }

    @Test
    public void verifyFloatSingleCheckLI() throws IOException {
        final ClassName dotted = Dotted.fromClass(FloatWithDefault.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        checker.result();
    }

    @Test
    public void verifyIntegerScliWithSemanticDefaultValue() throws IOException {
        final ClassName dotted = Dotted.fromClass(IntegerWithSemanticDefault.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        checker.result();
    }

    @Test
    public void verifyJavaLangString() throws IOException {
        final ClassName dotted = Dotted.fromClass(String.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        final IsImmutable result = checker.result();
        final Collection<MutableReasonDetail> reasons = checker.reasons();
        System.out.println(result);
    }

    @Test
    @Ignore
    public void printJumpsOpcodes() {
        for (final Opcode opcode : Opcode.jumps()) {
            System.out.println(String.format("Opcode '%s', number: '%d'.", opcode.name(), opcode.asInt()));
        }
    }

}
