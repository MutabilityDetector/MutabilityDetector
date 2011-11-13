package org.mutabilitydetector;

/**
 * The various classifications for instances of classes.
 * 
 * @author Grundlefleck at gmail dot com
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
     * Instances can be any of {@link #IMMUTABLE}/{@link IsImmutable#IMMUTABLE}/{@link #NOT_IMMUTABLE}, however, an
     * internal error has occurred during analysis, and no result is meaningful.
     */
    COULD_NOT_ANALYSE;
}
