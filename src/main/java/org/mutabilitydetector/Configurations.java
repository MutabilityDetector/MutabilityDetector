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


import org.mutabilitydetector.config.GuavaConfiguration;
import org.mutabilitydetector.config.JdkConfiguration;
import org.mutabilitydetector.unittesting.MutabilityAssert;

public class Configurations {


    /**
     * @see JdkConfiguration
     */
    public static final Configuration JDK_CONFIGURATION = new JdkConfiguration().build();
    
    /**
     * @see GuavaConfiguration
     */
    public static final Configuration GUAVA_CONFIGURATION = new GuavaConfiguration().build();
    
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
     * from {@link #JDK_CONFIGURATION} {@link #GUAVA_CONFIGURATION}.
     * <p>
     * It is recommended that this configuration is merged into any custom
     * Configuration, in order to remain consistent with
     * {@link MutabilityAssert}.
     * 
     * @since 0.9.3
     * @see JdkConfiguration
     * @see GuavaConfiguration
     */
    public static final Configuration OUT_OF_THE_BOX_CONFIGURATION = new ConfigurationBuilder() {
        @Override public void configure() {
            merge(JDK_CONFIGURATION);
            merge(GUAVA_CONFIGURATION);
        }
    }.build();

}
