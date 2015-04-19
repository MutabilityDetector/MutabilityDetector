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



import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;
import static java.util.Collections.unmodifiableList;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.mutabilitydetector.checkers.info.MutableTypeInformation;
import org.mutabilitydetector.checkers.info.MutableTypeInformation.MutabilityLookup;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

public abstract class CollectionField {
    public final Dotted collectionType;
    public final Iterable<GenericType> genericParameterTypes;

    private static final class RawCollection extends CollectionField {
        private RawCollection(Dotted collectionType) {
            super(collectionType, null);
        }
        @Override
        public boolean isGeneric() {
            return false;
        }

        public String asString() {
            return "raw " + collectionType.asString();
        }
    }
    
    private static final class GenericCollection extends CollectionField {
        public GenericCollection(Dotted collectionType, Iterable<GenericType> elementTypes) {
            super(collectionType, elementTypes);
        }

        @Override
        public boolean isGeneric() {
            return true;
        }
        public String asString() {
            return collectionType.asString() + "<" + Joiner.on(", ").join(genericParameterTypes) + ">";
        }
    }
    
    public abstract boolean isGeneric();
    public abstract String asString();

    protected CollectionField(Dotted collectionType, Iterable<GenericType> elementTypes) {
        this.collectionType = collectionType;
        this.genericParameterTypes = elementTypes;
    }

    public static CollectionField from(String collectionType, String signature) {
        if (signature == null) {
            return new RawCollection(dotted(collectionType));
        }
        GenericCollectionReader collectionTypeReader = new GenericCollectionReader();
        
        new SignatureReader(signature).accept(collectionTypeReader);
        
        return new GenericCollection(collectionTypeReader.collectionType, 
                                     unmodifiableList(collectionTypeReader.genericParameters()));
        
    }
    
    private static final class GenericCollectionReader extends SignatureVisitor {

        private Dotted collectionType;
        private final Map<Integer, Dotted> elementTypes = Maps.newHashMap();
        private final Map<Integer, Dotted> typeVariables = Maps.newHashMap();
        private final Map<Integer, String> wildcards = Maps.newHashMap();
        private int genericParameterIndex = 0;

        boolean seenOuterCollectionType = false;

        public GenericCollectionReader() {
            super(Opcodes.ASM5);
        }

        @Override
        public void visitClassType(String name) {
            if (!seenOuterCollectionType) {
                this.collectionType = dotted(name);
                seenOuterCollectionType = true;
            } else {
                elementTypes.put(genericParameterIndex, dotted(name));
                genericParameterIndex++;
            }
        }

        @Override
        public void visitTypeVariable(String name) {
            typeVariables.put(genericParameterIndex, dotted(name));
            genericParameterIndex++;
        }

        @Override
        public void visitTypeArgument() {
            wildcards.put(genericParameterIndex, "?");
            elementTypes.put(genericParameterIndex, null);
            genericParameterIndex++;
        }
        
        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            wildcards.put(genericParameterIndex, valueOf(wildcard));
            
            return this;
        }
        
        public List<GenericType> genericParameters() {
            List<GenericType> genericParameters = new ArrayList<GenericType>();
            for (int i = 0; i < genericParameterIndex; i++) {
                Dotted elementType = elementTypes.get(i);
                Dotted typeVariable = typeVariables.get(i);
                boolean isVariable = typeVariable != null;

                String wildcard = wildcards.get(i);
                genericParameters.add(new GenericType(isVariable ? typeVariable : elementType, wildcard, isVariable));
            }
            
            return unmodifiableList(new ArrayList<GenericType>(genericParameters));
        }
    }

    static class GenericType {
        public final Dotted type;
        public final String wildcard;
        public final boolean isVariable;

        public GenericType(Dotted type, String wildcard, boolean isVariable) {
            this.type = type;
            this.wildcard = checkNotNull(wildcard, "wildcard");
            this.isVariable = isVariable;
        }

        public static GenericType wildcard() {
            return new GenericType(null, "?", false);
        }

        public static GenericType exact(Dotted type) {
            return new GenericType(type, "=", false);
        }

        public static GenericType extends_(Dotted type) {
            return new GenericType(type, "+", false);
        }

        public static GenericType super_(Dotted type) {
            return new GenericType(type, "-", false);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type, wildcard);
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
            GenericType other = (GenericType) obj;
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            if (wildcard == null) {
                if (other.wildcard != null) {
                    return false;
                }
            } else if (!wildcard.equals(other.wildcard)) {
                return false;
            }
            return true;
        }

        
        @Override
        public String toString() {
            if (type == null) {
                return wildcard;
            } else {
                if (wildcard.equals("=")) {
                    return type.asString();
                } else if (wildcard.equals("+")){
                    return "? extends " + type.asString();
                } else if (wildcard.equals("-")){
                    return "? super " + type.asString();
                }
            }
            
            throw new IllegalStateException();
        }


        
    }

}
