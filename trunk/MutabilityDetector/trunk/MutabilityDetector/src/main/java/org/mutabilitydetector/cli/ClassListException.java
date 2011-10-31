/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */

package org.mutabilitydetector.cli;

/**
 * Used when the list of classes to to be reported on cannot be created.
 */
public class ClassListException extends RuntimeException {

    private static final long serialVersionUID = -2085270386267385221L;

    public ClassListException() {
        super();
    }

    public ClassListException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassListException(String message) {
        super(message);
    }

    public ClassListException(Throwable cause) {
        super(cause);
    }

}
