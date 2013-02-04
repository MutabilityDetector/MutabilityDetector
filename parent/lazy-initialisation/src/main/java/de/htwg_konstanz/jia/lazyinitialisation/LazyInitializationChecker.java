package de.htwg_konstanz.jia.lazyinitialisation;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 04.02.2013
 */
public final class LazyInitializationChecker extends AbstractMutabilityChecker {

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // TODO Auto-generated method stub
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // TODO Auto-generated method stub
        if (isNotConstructor(name)) {
            
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private static boolean isNotConstructor(final String name) {
        return !MethodIs.aConstructor(name);
    }

}
