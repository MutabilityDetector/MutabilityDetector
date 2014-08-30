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



import javax.annotation.concurrent.Immutable;

@Immutable
public final class Slashed extends ClassName {

    private Slashed(String slashedClassName) {
        super(slashedClassName);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Slashed other = (Slashed) obj;
        return asString().equals(other.asString());
    }
    
    @Override
    public int hashCode() {
        return asString().hashCode();
    }

    public Dotted toDotted() {
        return Dotted.fromSlashed(this);
    }

    public static Slashed slashed(String slashedClassName) {
        return new Slashed(slashedClassName);
    }
}
