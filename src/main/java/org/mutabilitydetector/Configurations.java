package org.mutabilitydetector;

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




import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mutabilitydetector.unittesting.MutabilityAssert;

public class Configurations {

    /**
     * Non-exhaustive list of immutable classes from the standard JDK.
     * 
     * @see String
     * @see Boolean
     * @see Byte
     * @see Character
     * @see Short
     * @see Integer
     * @see Long
     * @see Float
     * @see Double
     * @see BigDecimal
     * @see BigInteger
     * @see Class
     */
    public static final Configuration JDK_CONFIGURATION = new ConfigurationBuilder() {
        @Override
        public void configure() {
            hardcodeAsDefinitelyImmutable(String.class);
            hardcodeAsDefinitelyImmutable(Boolean.class);
            hardcodeAsDefinitelyImmutable(Byte.class);
            hardcodeAsDefinitelyImmutable(Character.class);
            hardcodeAsDefinitelyImmutable(Short.class);
            hardcodeAsDefinitelyImmutable(Integer.class);
            hardcodeAsDefinitelyImmutable(Long.class);
            hardcodeAsDefinitelyImmutable(Float.class);
            hardcodeAsDefinitelyImmutable(Double.class);
            hardcodeAsDefinitelyImmutable(BigDecimal.class);
            hardcodeAsDefinitelyImmutable(BigInteger.class);
            hardcodeAsDefinitelyImmutable(Class.class);

            hardcodeAsImmutableContainerType("java.util.Optional");
        }
    }.build();
    
    /**
     * List of collection copying methods in Guava collections which Mutability detector recognises
     * 
     * @see com.google.common.collect.Lists
     * @see com.google.common.collect.Sets
     * @see com.google.common.collect.Maps
     */
    public static final Configuration GUAVA_CONFIGURATION = new ConfigurationBuilder() {
        @Override
        public void configure() {
            hardcodeValidCopyMethod(List.class, "com.google.common.collect.Lists.newArrayList", Iterable.class);
            hardcodeValidCopyMethod(Set.class, "com.google.common.collect.Sets.newHashSet", Iterable.class);
            hardcodeValidCopyMethod(Map.class, "com.google.common.collect.Maps.newHashMap", Map.class);
        }
    }.build();
    
    /**
     * Configurations with default settings and no hardcoded results.
     */
    public static final Configuration NO_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() { }
    }.build();

    /**
     * Configuration with default settings and the standard hardcoded results.
     * <p>
     * As of version 0.9 the default configuration differs from
     * {@link #NO_CONFIGURATION} only in that it merges in the hardcoded results
     * from {@link #JDK_CONFIGURATION}.
     * <p>
     * It is recommended that this configuration is merged into any custom
     * Configuration, in order to remain consistent with
     * {@link MutabilityAssert}.
     * 
     * @since 0.9
     */
    public static final Configuration OUT_OF_THE_BOX_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() {
            mergeHardcodedResultsFrom(JDK_CONFIGURATION);
            mergeValidCopyMethodsFrom(GUAVA_CONFIGURATION);
        }
    }.build();

}
