package org.mutabilitydetector.checkers.hint;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
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

import org.mutabilitydetector.locations.Dotted;

/**
 * <p>
 * When collection field of class is initialized, it is common to use
 * copy-and-wrap idiom to make the collection immutable.
 * 
 * <p>
 * For example:
 * <p>
 * <code>
 *     this.list = Collections.unmodifiableList(new ArrayList<String>(list))
 * </code>
 * 
 * <p>
 * When mentioned idiom is not used correctly, or not used at all class
 * {@code WrappingHint} will represent a hint to help user apply copy-and-wrap
 * idiom in a correct way.
 * 
 */
public final class WrappingHint {
    public static WrappingHint NO_HINT = WrappingHint.builder().build();

    final String copyMethodOwnerName;
    final String copyMethodName;
    final String wrappingMethodOwnerName;
    final String wrappingMethodName;
    final String copyTypeParameterName;

    private WrappingHint(String copyMethodOwnerName, String copyMethodName,
                         String wrappingMethodOwnerName, String wrappingMethodName, String copyTypeParameterName) {
        this.copyMethodOwnerName = copyMethodOwnerName;
        this.copyMethodName = copyMethodName;
        this.wrappingMethodOwnerName = wrappingMethodOwnerName;
        this.wrappingMethodName = wrappingMethodName;
        this.copyTypeParameterName = copyTypeParameterName;
    }

    /**
     * For given field name get the actual hint message
     */
    public String getWrappingHint(String fieldName) {
        if (isEmpty()) {
            return "";
        }

        return String.format(" You can use this expression: %s(%s(%s))",
                formatMethod(wrappingMethodOwnerName, wrappingMethodName, ""),
                formatMethod(copyMethodOwnerName, copyMethodName, copyTypeParameterName),
                fieldName);
    }

    private String formatMethod(String typeName, String methodName, String typeParameter) {
        String simpleTypeName = Dotted.fromSlashedString(typeName).asSimpleString();

        if ("<init>".equals(methodName)) {
            return String.format("new %s%s", simpleTypeName, typeParameter);
        }

        return String.format("%s.%s%s", simpleTypeName, typeParameter, methodName);
    }

    public boolean isEmpty() {
        return copyMethodOwnerName.isEmpty() &&
                copyMethodName.isEmpty() &&
                wrappingMethodOwnerName.isEmpty() &&
                wrappingMethodName.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String copyMethodOwnerName = "";
        private String copyMethodName = "";
        private String wrappingMethodOwnerName = "";
        private String wrappingMethodName = "";
        private String copyTypeParameterName = "";

        public Builder setCopyMethodOwnerName(String copyMethodOwnerName) {
            this.copyMethodOwnerName = copyMethodOwnerName;
            return this;
        }

        public Builder setCopyMethodName(String copyMethodName) {
            this.copyMethodName = copyMethodName;
            return this;
        }

        public Builder setWrappingMethodOwnerName(String wrappingMethodOwnerName) {
            this.wrappingMethodOwnerName = wrappingMethodOwnerName;
            return this;
        }

        public Builder setWrappingMethodName(String wrappingMethodName) {
            this.wrappingMethodName = wrappingMethodName;
            return this;
        }

        public Builder setCopyTypeParameterName(String copyTypeParameterName) {
            this.copyTypeParameterName = copyTypeParameterName;
            return this;
        }

        public WrappingHint build() {
            return new WrappingHint(copyMethodOwnerName, copyMethodName, wrappingMethodOwnerName, wrappingMethodName, copyTypeParameterName);
        }
    }

}
