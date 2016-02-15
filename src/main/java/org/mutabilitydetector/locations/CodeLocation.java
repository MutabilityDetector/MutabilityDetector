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


import org.mutabilitydetector.locations.line.LineNumbersUtil;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

public abstract class CodeLocation<T extends CodeLocation<T>> implements Comparable<T> {

    private CodeLocation() {
    }

    public abstract String typeName();

    public abstract String prettyPrint();

    @Immutable
    public final static class UnknownCodeLocation extends CodeLocation<UnknownCodeLocation> {

        public static final UnknownCodeLocation UNKNOWN = new UnknownCodeLocation();

        private UnknownCodeLocation() {
        }

        @Override
        public int compareTo(@Nonnull UnknownCodeLocation o) {
            return 0; // There can be only one.
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String typeName() {
            return "unknown code location";
        }

        @Override
        public String prettyPrint() {
            return "[Unknown code location]";
        }

    }

    @Immutable
    public static final class ClassLocation extends CodeLocation<ClassLocation> {

        private final
        @Nonnull
        String dottedClassName;

        public ClassLocation(String dottedClassName) {
            this.dottedClassName = dottedClassName;
        }

        @Override
        public String typeName() {
            return dottedClassName;
        }

        @Override
        public int compareTo(@Nonnull ClassLocation other) {
            return typeName().compareTo(other.typeName());
        }

        @Override
        public int hashCode() {
            return dottedClassName.hashCode();
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
            ClassLocation other = (ClassLocation) obj;

            return dottedClassName.equals(other.dottedClassName);
        }

        public static ClassLocation fromInternalName(String internalClassName) {
            String dottedClassName = new ClassNameConverter().dotted(internalClassName);
            return new ClassLocation(dottedClassName);
        }

        public static ClassLocation from(Slashed slashed) {
            String dottedClassName = ClassIdentifier.forClass(slashed).asDotted().asString();
            return new ClassLocation(dottedClassName);
        }

        public static ClassLocation from(Dotted dotted) {
            return new ClassLocation(dotted.asString());
        }

        @Override
        public String prettyPrint() {
            return String.format("[Class: %s%s]", typeName(), sourceLocation());
        }

        private String sourceLocation() {
            try {
                Class<?> sourceClass = Class.forName(typeName());
                return LineNumbersUtil.newClassLocation(sourceClass).toString();
            } catch (ClassNotFoundException e) {
                return LineNumbersUtil.newUnknownLocation().toString();
            }
        }
    }

    @Immutable
    public static final class FieldLocation extends CodeLocation<FieldLocation> {

        private final
        @Nonnull
        String fieldName;
        private final
        @Nonnull
        ClassLocation ownerOfField;

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
        public int compareTo(@Nonnull FieldLocation other) {
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
            return String.format("[Field: %s.%s%s]", typeName(), fieldName(), sourceLocation());
        }

        private String sourceLocation() {
            try {
                Class<?> sourceClass = Class.forName(typeName());
                return LineNumbersUtil.newFieldLocation(sourceClass, fieldName).toString();
            } catch (ClassNotFoundException e) {
                return LineNumbersUtil.newUnknownLocation().toString();
            }
        }
    }
}
