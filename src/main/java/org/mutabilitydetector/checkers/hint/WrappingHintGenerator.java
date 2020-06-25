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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.mutabilitydetector.checkers.CollectionField;
import org.mutabilitydetector.checkers.CollectionField.GenericType;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomOrMadeByImmutableFactoryChecker.Configuration;
import org.mutabilitydetector.checkers.hint.exceptions.WrappingHintGenerationException;
import org.mutabilitydetector.checkers.info.CopyMethod;
import org.mutabilitydetector.locations.ClassNameConverter;
import org.objectweb.asm.Type;

import static org.mutabilitydetector.checkers.hint.WrappingHint.NO_HINT;

/**
 * The class is used to generate {@link WrappingHint}
 */
public final class WrappingHintGenerator {
    private Configuration configuration;
    private String typeSignature;
    private String typeAssignedToField;
    private Multimap<String, CopyMethod> userDefinedCopyMethods;
    
    private static final ClassNameConverter CLASS_NAME_CONVERTER = new ClassNameConverter();

    public WrappingHintGenerator(Configuration configuration, String typeSignature, Type fieldType,
                                 Multimap<String, CopyMethod> userDefinedCopyMethods) {
        this.configuration = configuration;
        this.typeSignature = typeSignature;
        this.typeAssignedToField = typeToString(fieldType);
        this.userDefinedCopyMethods = userDefinedCopyMethods;
    }

    public WrappingHint generate() {
        try {
            WrappingHint.Builder builder = WrappingHint.builder();
            generateWrappingPart(builder);
            generateCopyingPart(builder);
            return builder.build();
        } catch (WrappingHintGenerationException e) {
            return NO_HINT;
        }
    }

    /**
     * Pick arbitrary copying method from available configuration and don't forget to 
     * set generic method type if required.
     * @param builder
     */
    private void generateCopyingPart(WrappingHint.Builder builder) {
        ImmutableCollection<CopyMethod> copyMethods = ImmutableMultimap.<String, CopyMethod>builder()
                .putAll(configuration.FIELD_TYPE_TO_COPY_METHODS)
                .putAll(userDefinedCopyMethods)
                .build()
                .get(typeAssignedToField);
        
        if (copyMethods.isEmpty()) {
            throw new WrappingHintGenerationException();
        }
        
        CopyMethod firstSuitable = copyMethods.iterator().next();
        builder.setCopyMethodOwnerName(firstSuitable.owner.toString())
                .setCopyMethodName(firstSuitable.name);
        
        if (firstSuitable.isGeneric && typeSignature != null) {
            CollectionField withRemovedWildcards = CollectionField.from(typeAssignedToField, typeSignature)
                    .transformGenericTree(GenericType::withoutWildcard);
            builder.setCopyTypeParameterName(formatTypeParameter(withRemovedWildcards.asSimpleString()));
        }
    }

    /**
     * Pick arbitrary wrapping method. No generics should be set.
     * @param builder
     */
    private void generateWrappingPart(WrappingHint.Builder builder) {
        builder.setWrappingMethodOwnerName(configuration.UNMODIFIABLE_METHOD_OWNER)
                .setWrappingMethodName(configuration.FIELD_TYPE_TO_UNMODIFIABLE_METHOD.get(typeAssignedToField));
    }

    private static String formatTypeParameter(String typeParameter) {
        return typeParameter.substring(typeParameter.indexOf('<'), typeParameter.lastIndexOf('>') + 1);
    }

    private static String typeToString(Type type) {
        return CLASS_NAME_CONVERTER.dotted(type.getInternalName());
    }
}
