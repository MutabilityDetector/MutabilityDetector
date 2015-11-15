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



import static org.mutabilitydetector.checkers.AccessModifierQuery.field;
import static org.mutabilitydetector.locations.CodeLocation.FieldLocation.fieldLocation;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.objectweb.asm.FieldVisitor;

public final class NonFinalFieldChecker extends AsmMutabilityChecker {

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (field(access).isNotFinal() && field(access).isNotStatic()) {
            setResult("Field is not final, if shared across threads the Java Memory Model will not guarantee it is initialised before it is read.",
                    fieldLocation(name, ClassLocation.fromInternalName(ownerClass)),
                    MutabilityReason.NON_FINAL_FIELD);
        }
        return super.visitField(access, name, desc, signature, value);
    }

}
