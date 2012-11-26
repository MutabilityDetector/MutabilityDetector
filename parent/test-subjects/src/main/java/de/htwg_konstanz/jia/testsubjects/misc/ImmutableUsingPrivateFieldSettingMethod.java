/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package de.htwg_konstanz.jia.testsubjects.misc;

/**
 * This class is immutable, but fields are set outwith the constructor. Notice
 * that the private field setter method is only called from within the
 * constructor, and while it does modify the fields, no changes to the instance
 * can be viewed by clients of the class.
 * 
 * This pattern can occur with serialisation of an immutable object.
 * 
 * Several examples were found within JodaTime.
 */
public final class ImmutableUsingPrivateFieldSettingMethod {

    private int field1 = 1;
    private double field2 = 1.0d;
    private long field3 = 1l;

    public ImmutableUsingPrivateFieldSettingMethod() {
        setFields();
    }

    private void setFields() {
        field1++;
        field2++;
        field3++;
    }

    public int getField1() {
        return field1;
    }

    public double getField2() {
        return field2;
    }

    public long getField3() {
        return field3;
    }

}
