package org.mutabilitydetector.checkers;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.mutabilitydetector.locations.CodeLocationFactory;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

public class ArrayFieldMutabilityChecker extends AsmMutabilityChecker {
    private CodeLocationFactory codeLocationFactory;

    public ArrayFieldMutabilityChecker(CodeLocationFactory codeLocationFactory) {
        this.codeLocationFactory = codeLocationFactory;
    }

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
            if (isArray(desc) && !isTheInternalImmutableArrayFieldInAnEnum(name)) {
                setResult("Field is an array.",
                        codeLocationFactory.fieldLocation(name, ClassLocation.fromInternalName(ownerClass)),
                        MutabilityReason.ARRAY_TYPE_INHERENTLY_MUTABLE);
            }
        }

        return super.visitField(access, name, desc, signature, value);
    }

    private boolean isTheInternalImmutableArrayFieldInAnEnum(String name) {
        return "ENUM$VALUES".equals(name);
    }

    private boolean isArray(String desc) {
        return Type.ARRAY == Type.getType(desc).getSort();
    }

}
