package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.checkers.AccessModifierQuery.field;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.ClassLocation;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

public class ArrayFieldMutabilityChecker extends AbstractMutabilityChecker {

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        /*
         * Static fields should not count against the instance.
         */
        if (field(access).isNotStatic()) {
            /*
             * This check was causing far too many classes to be called mutable. It would be better if it was possible
             * to check that an inherently mutable type was *actually mutated*. Calling an entire class mutable for
             * having a mutable field which it doesn't mutate is a bit rubbish.
             */
            if (isPrimitiveArray(desc) && !isTheInternalImmutableArrayFieldInAnEnum(name)) {
                addResult("Field is an array.",
                        fieldLocation(name, ClassLocation.fromInternalName(ownerClass)),
                        MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE);
            }
        }

        return super.visitField(access, name, desc, signature, value);
    }

    private boolean isTheInternalImmutableArrayFieldInAnEnum(String name) {
        return "ENUM$VALUES".equals(name);
    }

    private boolean isPrimitiveArray(String desc) {
        return Type.ARRAY == Type.getType(desc).getSort();
    }
    
}
