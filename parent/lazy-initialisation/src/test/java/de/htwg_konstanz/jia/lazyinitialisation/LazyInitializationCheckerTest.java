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
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedFloatWithDefault;
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
    public void verifyIntegerSingleCheckLIWithClassNode() throws IOException {
        final ClassName dotted = Dotted.fromClass(IntegerWithDefault.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final MethodNode hashCodeMethodNode = cn.methods.get(1);
        final AbstractInsnNode[] instructions = hashCodeMethodNode.instructions.toArray();
        System.out.println(instructions);
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
    public void verifyAliasedFloatWithDefaultSingleCheckLI() throws IOException {
        final IsImmutable result = runImmutabilityVerificationFor(AliasedFloatWithDefault.class);
    }

    private static IsImmutable runImmutabilityVerificationFor(final Class<?> klasse) throws IOException {
        final ClassName dotted = Dotted.fromClass(klasse);
        final ClassReader cr = new ClassReader(dotted.asString());
        final LazyInitializationChecker checker = new LazyInitializationChecker();
        cr.accept(checker, 0);
        return checker.result();
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
