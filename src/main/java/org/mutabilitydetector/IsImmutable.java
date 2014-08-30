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



import java.util.HashMap;

/**
 * The various classifications for instances of classes.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com
 *
 */
public enum IsImmutable {

    /**
     * Instances are perfectly immutable, can be safely published without extra synchronisation.
     */
    IMMUTABLE,

    /**
     * Instances are only immutable when safely published. Extra synchronisation is required to publish effectively
     * immutable objects safely.
     */
    EFFECTIVELY_IMMUTABLE,

    /**
     * Instances are not immutable, do not cache and/or share instances, do not publish to multiple threads, do not use
     * as keys in {@link HashMap}s.
     */
    NOT_IMMUTABLE,

    /**
     * Instances can be any of {@link #IMMUTABLE}/{@link IsImmutable#EFFECTIVELY_IMMUTABLE}/{@link #NOT_IMMUTABLE}, however, an
     * internal error has occurred during analysis, and no result is meaningful.
     */
    COULD_NOT_ANALYSE;
}
