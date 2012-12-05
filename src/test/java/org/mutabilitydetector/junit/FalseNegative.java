package org.mutabilitydetector.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An analysis incorrectly passes, the class is mutable, but is reported as immutable.
 * 
 * @author Graham Allan / Grundlefleck at gmail dot com

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface FalseNegative {
    String value() default "";
}
