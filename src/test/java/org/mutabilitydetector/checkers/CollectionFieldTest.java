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


import org.junit.Test;
import org.mutabilitydetector.checkers.CollectionField.GenericType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mutabilitydetector.checkers.CollectionField.GenericType.exact;
import static org.mutabilitydetector.checkers.CollectionField.GenericType.wildcard;
import static org.mutabilitydetector.locations.Dotted.fromClass;

@SuppressWarnings("unused")
public class CollectionFieldTest {

    @Test
    public void retrievesGenericTypeOfListField() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithGenericListField.class);
        
        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(GenericType.exact(fromClass(String.class))));
        assertThat(collectionField.asString(), equalTo("java.util.List<java.lang.String>"));
    }

    @Test
    public void retrievesGenericTypeOfRawListField() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithRawListField.class);
        
        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), is(nullValue()));
        assertThat(collectionField.asString(), equalTo("raw java.util.List"));
    }

    @Test
    public void retrievesGenericTypeOfListFieldWithExtendsWildcard() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithExtendsWildcardsGenericsListField.class);
        
        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(GenericType.extends_(fromClass(String.class))));
        assertThat(collectionField.asString(), equalTo("java.util.List<? extends java.lang.String>"));
    }

    @Test
    public void retrievesGenericTypeOfListFieldWithSuperWildcard() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithSuperWildcardsGenericsListField.class);
        
        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(GenericType.super_(fromClass(String.class))));
        assertThat(collectionField.asString(), equalTo("java.util.List<? super java.lang.String>"));
    }

    @Test
    public void retrievesGenericTypeOfListFieldWithWildcard() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithWildcardGenericsListField.class);
        
        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(wildcard()));
        assertThat(collectionField.asString(), equalTo("java.util.List<?>"));
    }

    @Test
    public void retrievesGenericTypeOfMapField() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithGenericMapField.class);

        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);
        
        assertThat(collectionField.collectionType, equalTo(fromClass(Map.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(GenericType.exact(fromClass(String.class)),
                GenericType.exact(fromClass(Date.class))));
        assertThat(collectionField.asString(), equalTo("java.util.Map<java.lang.String, java.util.Date>"));
    }

    @Test
    public void retrievesGenericTypeOfNestedListField() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(WithNestedListField.class);

        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);

        assertThat(collectionField.collectionType, equalTo(fromClass(List.class)));
        assertThat(collectionField.getGenericParameterTypes(), contains(exact(fromClass(Reference.class)), exact(fromClass(Date.class))));
        assertThat(collectionField.asString(), equalTo("java.util.List<java.lang.ref.Reference<java.util.Date>>"));
    }


    @Test(expected = AssertionError.class)
    public void recognisesPrimitiveArrayAsGenericTypeOfCollectionField() throws Exception {
        String[] descAndSignature = descAndSignatureOfSingleFieldIn(DeclaresGenericCollectionButAssignsRawCollection.class);

        CollectionField collectionField = CollectionField.from(descAndSignature[0], descAndSignature[1]);

        assertThat(collectionField.getGenericParameterTypes(), contains(exact(fromClass(int[].class))));
    }

    private static class WithGenericListField {
        public List<String> listOfString;
    }

    private static class WithRawListField {
        @SuppressWarnings("rawtypes")
        public List listOfString;
    }

    private static class WithGenericMapField {
        public Map<String, Date> stringToDateMap;
    }

    private static class WithNestedListField {
        public List<Reference<Date>> stringToDateMap;
    }

    private static class WithExtendsWildcardsGenericsListField {
        public List<? extends String> listOfString;
    }

    private static class WithSuperWildcardsGenericsListField {
        public List<? super String> listOfString;
    }

    private static class WithWildcardGenericsListField {
        public List<?> listOfString;
    }

    private static class DeclaresGenericCollectionButAssignsRawCollection {
        public final Collection<int[]> genericListWithRawTypeAssigned = new ArrayList<>();
    }

    private String[] descAndSignatureOfSingleFieldIn(Class<?> class1) throws IOException {
        final String[] fieldDescSignature = new String[2]; 
        
        ClassReader classReader = new ClassReader(class1.getName());
        classReader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                fieldDescSignature[0] = desc;
                fieldDescSignature[1] = signature;
                return super.visitField(access, name, desc, signature, value);
                
            };
        }, 0);
        return fieldDescSignature;
    }

}
