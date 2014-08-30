/**
 * 
 */package org.mutabilitydetector.checkers.settermethod;

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
 * Implementations of this interface are supposed to find something within a
 * provided context. This context is out of the scope of this interface.
 * 
 * @param <T>
 *            type which is found by the implementation of this interface.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.03.2013
 */
public interface Finder<T> {

    /**
     * @return an object of the found type or {@code null}. Have a look at
     *         documentation of implementations for specific behaviour.
     */
    T find();

}
