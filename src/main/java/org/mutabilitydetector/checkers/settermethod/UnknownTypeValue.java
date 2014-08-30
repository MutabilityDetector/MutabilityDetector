package org.mutabilitydetector.checkers.settermethod;

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



/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.02.2013
 */
public interface UnknownTypeValue {

    enum Default {
        NULL,
        UNKNOWN_PRIMITIVE,
        UNKNOWN_REFERENCE
    } // enum Default

    boolean asBoolean();

    char asChar();

    boolean isBoolean();

    boolean isByte();

    boolean isChar();

    boolean isShort();

    boolean isInt();

    boolean isLong();

    boolean isFloat();

    boolean isDouble();

    boolean isString();

    boolean isObject();

    boolean isZero();

    boolean isNotZero();

    boolean isNull();

    boolean isNotNull();

    boolean isUnknownPrimitive();

    boolean isUnknownReference();

}
