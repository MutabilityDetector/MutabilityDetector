package org.mutabilitydetector.config;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2015 Graham Allan
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

import org.mutabilitydetector.ConfigurationBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

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
public class JdkConfiguration extends ConfigurationBuilder {
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
        hardcodeAsDefinitelyImmutable(URI.class);

        hardcodeAsImmutableContainerType("java.util.Optional");
    }
}
