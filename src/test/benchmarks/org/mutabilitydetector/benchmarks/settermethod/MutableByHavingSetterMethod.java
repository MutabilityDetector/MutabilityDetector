package org.mutabilitydetector.benchmarks.settermethod;

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

@SuppressWarnings("unused")
public final class MutableByHavingSetterMethod {

    private String name;
    private int primitiveField;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrimitiveField(int foo) {
        this.primitiveField = foo;
    }

    public void setNameIndirectly(String name) {
        this.setName(name);
    }

}
