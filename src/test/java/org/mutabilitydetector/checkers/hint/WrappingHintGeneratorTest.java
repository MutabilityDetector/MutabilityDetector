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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomOrMadeByImmutableFactoryChecker.Configuration;
import org.mutabilitydetector.checkers.info.CopyMethod;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class WrappingHintGeneratorTest {

    private static final Multimap<String, CopyMethod> EMPTY_USER_DEFINED_METHODS = ImmutableMultimap.of();

    @Test
    public void testCollectionHintWithoutGenerics() {
        String emptySignature = null;
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                emptySignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is(""));
    }

    @Test
    public void testCollectionHintWithExactGenerics() {
        String exactSignature = "Ljava/util/Collection<Ljava/lang/String;>;";
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                exactSignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        
        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is("<String>"));
    }

    @Test
    public void testCollectionHintWithExtendsGenerics() {
        String extendsSignature = "Ljava/util/Collection<-Ljava/lang/String;>;";
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                extendsSignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is("<String>"));
    }

    @Test
    public void testCollectionHintWithSuperGenerics() {
        String superSignature = "Ljava/util/Collection<+Ljava/lang/String;>;";
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                superSignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is("<String>"));
    }

    @Test
    public void testCollectionHintWithWildcard() {
        String wildcardSignature = "Ljava/util/Collection<*>;";
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                wildcardSignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is("<Object>"));
    }

    @Test
    public void testMapHintWithComplexGenerics() {
        String complexSignature = "Ljava/util/Map<+Lcom/google/common/collect/ImmutableMap<-Ljava/util/List<Ljava/lang/String;>;Ljava/lang/Integer;>;*>;";
        Type mapType = Type.getType(Map.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                complexSignature, mapType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertMapHint(hint);
        assertThat(hint.copyTypeParameterName, is("<ImmutableMap<List<String>, Integer>, Object>"));
    }

    @Test
    public void testCollectionHintMessage() {
        String exactSignature = "Ljava/util/Collection<Ljava/lang/String;>;";
        Type collectionType = Type.getType(Collection.class);
        WrappingHintGenerator generator = new WrappingHintGenerator(Configuration.INSTANCE,
                exactSignature, collectionType, EMPTY_USER_DEFINED_METHODS);
        WrappingHint hint = generator.generate();

        assertCollectionHint(hint);
        assertThat(hint.copyTypeParameterName, is("<String>"));
        assertThat(hint.getWrappingHint("field"), is(" You can use this expression: Collections.unmodifiableCollection(new ArrayList<String>(field))"));
    }

    private void assertMapHint(WrappingHint hint) {
        assertThat(hint.isEmpty(), is(false));
        assertThat(hint.copyMethodName, is("<init>"));
        assertThat(hint.copyMethodOwnerName, is("java.util.HashMap"));
        assertThat(hint.wrappingMethodName, is("unmodifiableMap"));
        assertThat(hint.wrappingMethodOwnerName, is("java.util.Collections"));
    }

    private void assertCollectionHint(WrappingHint hint) {
        assertThat(hint.isEmpty(), is(false));
        assertThat(hint.copyMethodName, is("<init>"));
        assertThat(hint.copyMethodOwnerName, is("java.util.ArrayList"));
        assertThat(hint.wrappingMethodName, is("unmodifiableCollection"));
        assertThat(hint.wrappingMethodOwnerName, is("java.util.Collections"));
    }
}