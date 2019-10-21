package org.mutabilitydetector.locations;

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


import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Dotted extends ClassName {

    private Dotted(String className) {
        super(className);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Dotted other = (Dotted) obj;
        return asString().equals(other.asString());
    }

    @Override
    public String asInternal() {
        return new ClassNameConverter().toInternal(this);
    }

    @Override
    public int hashCode() {
        return asString().hashCode();
    }

    public static Dotted dotted(String className) {
        return new Dotted(new ClassNameConverter().dotted(className));
    }

    public static Dotted fromSlashed(Slashed className) {
        String converted = new ClassNameConverter().dotted(className.asString());
        return dotted(converted);
    }

    public static Dotted fromSlashedString(String className) {
        String dottedClassNameString = new ClassNameConverter().dotted(className);
        return dotted(dottedClassNameString);
    }

    public static Dotted fromClass(Class<?> clazz) {
        return dotted(clazz.getName());
    }

    public static Dotted fromType(Type type) {
        return dotted(type.getClassName());
    }

    /**
     * @param fieldDescription a field's descriptor (see {@link Type})
     * @return a lightweight representation of a field's declared type
     * @see org.objectweb.asm.ClassVisitor#visitField(int, String, String, String, Object)
     */
    public static Dotted fromFieldDescription(final String fieldDescription) {
        final Type type = Type.getType(fieldDescription);
        final int sort = type.getSort();
        return sort == Type.ARRAY ? dotted(fieldDescription) : fromType(type);
    }

    public static Dotted fromFieldNode(final FieldNode node) {
        return fromFieldDescription(node.desc);
    }

    public static Dotted fromFieldInsnNode(final FieldInsnNode node) {
        return fromFieldDescription(node.desc);
    }

    public String asResource() {
        return asString().replace(".", "/").concat(".class");
    }

}
