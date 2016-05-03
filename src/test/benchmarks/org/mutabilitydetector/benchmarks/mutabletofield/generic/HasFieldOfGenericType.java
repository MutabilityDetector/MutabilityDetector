package org.mutabilitydetector.benchmarks.mutabletofield.generic;


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

import java.util.ArrayList;

import org.mutabilitydetector.benchmarks.mutabletofield.generic.HasFieldOfGenericType.MyInterface;

public final class HasFieldOfGenericType<T extends Enum<T> & MyInterface, N> extends ArrayList<T> {

    public final T fieldOfT;
    public final N fieldOfN;
    public final String string = "Hi";

    public HasFieldOfGenericType(T t, N n) {
        this.fieldOfT = t;
        this.fieldOfN = n;
    }

    static interface MyInterface {}

    static enum MyEnum implements MyInterface {}
}
