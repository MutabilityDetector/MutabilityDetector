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



import static org.mutabilitydetector.checkers.AccessModifierQuery.type;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.locations.ClassLocation;

/**
 * This checker visits types and fields. Types can be visited separately, fields should be visited as part of visiting
 * an outer type.
 * 
 * The rules of inherent mutability are defined as:
 * 
 * Inherently mutable: Interfaces; Abstract classes
 * 
 * Inherently immutable: Enum types; primitive types ie. boolean, char, byte, short, int, long, float, double
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 * 
 */
public final class InherentTypeMutabilityChecker extends AbstractMutabilityChecker {

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        if (type(access).isAbstract() || type(access).isInterface()) {
            setResult("Is inherently mutable, as declared as an abstract type.",
                    ClassLocation.fromInternalName(name),
                    MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE);
        }
    }

}
