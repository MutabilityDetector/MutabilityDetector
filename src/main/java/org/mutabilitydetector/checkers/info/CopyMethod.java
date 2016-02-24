package org.mutabilitydetector.checkers.info;

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


import com.google.common.base.Objects;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.tree.MethodInsnNode;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CopyMethod {
    public final Dotted owner;
    public final String name;
    public final String desc;
    public final boolean isGeneric;

    public CopyMethod(Dotted owner, String name, String desc) {
        this(owner, name, desc, false);
    }

    public CopyMethod(Dotted owner, String name, String desc, boolean isGeneric) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.isGeneric = isGeneric;
    }

    public static CopyMethod from(MethodInsnNode methodNode) {
        return new CopyMethod(Dotted.dotted(methodNode.owner), methodNode.name, methodNode.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(desc, name, owner);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CopyMethod other = (CopyMethod) obj;
        return desc.equals(other.desc) && name.equals(other.name) && owner.equals(other.owner);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("owner", owner)
                .add("name", name)
                .add("desc", desc)
                .toString();
    }

}
