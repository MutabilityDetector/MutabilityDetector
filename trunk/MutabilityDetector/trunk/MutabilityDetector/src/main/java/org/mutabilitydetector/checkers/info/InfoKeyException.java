/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.checkers.info;

final class InfoKeyException extends RuntimeException {
    public InfoKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfoKeyException(String message) {
        super(message);
    }
}
