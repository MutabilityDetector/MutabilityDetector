package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.checkers.AccessModifierQuery.field;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.ClassLocation;
import org.objectweb.asm.FieldVisitor;

public class NonFinalFieldChecker extends AbstractMutabilityChecker {

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (field(access).isNotFinal() && field(access).isNotStatic()) {
            addResult("Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read.",
                    fieldLocation(name, ClassLocation.fromInternalName(ownerClass)),
                    MutabilityReason.NON_FINAL_FIELD);
        }
        return super.visitField(access, name, desc, signature, value);
    }

}
