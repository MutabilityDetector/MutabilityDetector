package org.mutabilitydetector.locations;

/*-
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2019 Graham Allan
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

import static org.junit.Assert.assertEquals;
import static org.mutabilitydetector.locations.CodeLocation.FieldLocation.fieldLocation;

import java.lang.reflect.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.checkers.AsmMutabilityChecker;
import org.mutabilitydetector.locations.CodeLocation.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation.FieldLocation;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class DottedTest {

    @NotThreadSafe
    protected static class FieldExtractor extends AsmMutabilityChecker {

        private final Map<? super String, FieldLocation> fields = new HashMap<>();

        @Override
        public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
            fields.put(name,
                    fieldLocation(name, ClassLocation.fromInternalName(name), Dotted.fromFieldDescription(desc)));
            return super.visitField(access, name, desc, signature, value);
        }

        public FieldLocation getFieldLocation(final String fieldName) {
            return fields.get(fieldName);
        }

    }

    /**
     * Verify that simple types extracted by a {@link ClassVisitor} match what would
     * be obtained by Reflection.
     */
    @Test
    public final void simplePojoFieldTypesExtracted() {
        // given
        final FieldExtractor extractor = new FieldExtractor();
        final Class<?> toAnalyse = Pojo.class;

        // when

        TestUtil.runChecker(extractor, toAnalyse);

        // then
        assertAsmTypesMatcheReflection(extractor, toAnalyse);
    }

    /**
     * A class with simple types only, no generics.
     */
    protected static class Pojo {
        int intField;
        Function<Integer, Long> functionField;
        byte[] byteArrayField;
        Predicate<String> predicateField;
        String objectField;
    }

    /**
     * Verify that parameterised (generic) types extracted by a {@link ClassVisitor}
     * match what would be obtained by Reflection.
     */
    @Test
    public final void parameterisedFieldTypesExtracted() {
        // given
        final FieldExtractor extractor = new FieldExtractor();
        final Class<?> toAnalyse = GenericPojo.class;

        // when
        TestUtil.runChecker(extractor, toAnalyse);

        // then
        assertAsmTypesMatcheReflection(extractor, toAnalyse);
    }

    /**
     * A class with a generic type parameter.
     *
     * @param <T> Some unbounded type parameter
     */
    protected static class GenericPojo<T> {
        T objectField;
        Function<T, T> functionField;
        T[] arrayField;
    }

    /**
     * Verify that bounded parameterised (generic) types extracted by a
     * {@link ClassVisitor} match what would be obtained by Reflection.
     */
    @Test
    public final void boundedParameterisedFieldTypesExtracted() {
        // given
        final FieldExtractor extractor = new FieldExtractor();
        final Class<?> toAnalyse = BoundedGenericPojo.class;

        // when
        TestUtil.runChecker(extractor, toAnalyse);

        // then
        assertAsmTypesMatcheReflection(extractor, toAnalyse);
    }

    /**
     * A class with bounded generic type parameters.
     *
     * @param <S> some generic type parameter
     * @param <T> some bounded generic type parameter
     */
    protected static class BoundedGenericPojo<S extends Object , T extends List<? extends S>> {
        T listField;
        S singleField;
        Function<? extends S, T> functionField;
        T[] arrayField;
        Predicate<? super S> predicateField;
    }

    protected void assertAsmTypesMatcheReflection(final FieldExtractor extractor, final Class<?> toAnalyse) {
        for (final Field field : toAnalyse.getDeclaredFields()) {
            final Class<?> type = field.getType();
            final Dotted reflectionDerivedClass = Dotted.fromClass(type);
            final String fieldName = field.getName();
            final FieldLocation fieldLocation = extractor.getFieldLocation(fieldName);
            assertEquals("Unexpected type for field: " + fieldName, reflectionDerivedClass, fieldLocation.fieldType());
        }
    }

}
