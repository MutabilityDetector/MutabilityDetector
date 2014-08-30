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



import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class FieldLocation implements CodeLocation<FieldLocation> {

    private final @Nonnull String fieldName;
    private final @Nonnull ClassLocation ownerOfField;

    private FieldLocation(String fieldName, ClassLocation ownerOfField) {
        this.fieldName = fieldName;
        this.ownerOfField = ownerOfField;
    }

    public static FieldLocation fieldLocation(String fieldName, ClassLocation ownerOfField) {
        return new FieldLocation(fieldName, ownerOfField);
    }

    public String fieldName() {
        return fieldName;
    }

    @Override
    public String typeName() {
        return ownerOfField.typeName();
    }

    @Override
    public int compareTo(FieldLocation other) {
        int comparingOwner = ownerOfField.compareTo(other.ownerOfField);
        int comparingFieldName = fieldName.compareTo(other.fieldName);
        return comparingOwner == 0 ? comparingFieldName : comparingOwner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fieldName.hashCode();
        result = prime * result + ownerOfField.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FieldLocation other = (FieldLocation) obj;

        return fieldName.equals(other.fieldName) && ownerOfField.equals(other.ownerOfField);
    }

    @Override
    public String prettyPrint() {
        return String.format("[Field: %s, Class: %s]", fieldName(), typeName());
    }

}
