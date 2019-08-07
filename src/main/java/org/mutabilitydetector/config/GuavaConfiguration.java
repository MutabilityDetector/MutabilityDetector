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

public class GuavaConfiguration extends ConfigurationBuilder {

    // String gymnastics to avoid class relocation by maven shade plugin.
    private final String packageBase = String.format("%s.%s.%s.", "com", "google", "common");

    @Override
    public void configure() {
        hardcodeAsImmutableContainerType(packageBase + "collect.ContiguousSet");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableCollection");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableClassToInstanceMap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableBiMap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableCollection");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableList");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableListMultimap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableMap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableMultimap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableMultiset");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableRangeMap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableRangeSet");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableSet");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableSetMultimap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableSortedMap");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableSortedMultiset");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableSortedSet");
        hardcodeAsImmutableContainerType(packageBase + "collect.ImmutableTable");
        hardcodeAsImmutableContainerType(packageBase + "collect.Range");
        hardcodeAsImmutableContainerType(packageBase + "base.Optional");
        hardcodeAsDefinitelyImmutable(packageBase + "primitives.ImmutableDoubleArray");
        hardcodeAsDefinitelyImmutable(packageBase + "primitives.ImmutableIntArray");
        hardcodeAsDefinitelyImmutable(packageBase + "primitives.ImmutableLongArray");
    }

}
