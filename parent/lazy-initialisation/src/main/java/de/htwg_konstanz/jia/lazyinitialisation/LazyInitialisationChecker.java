package de.htwg_konstanz.jia.lazyinitialisation;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.repackaged.org.objectweb.asm.FieldVisitor;

/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.12.2012
 */
public final class LazyInitialisationChecker extends AbstractMutabilityChecker {

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // TODO Auto-generated method stub
        return super.visitField(access, name, desc, signature, value);
    }

}
