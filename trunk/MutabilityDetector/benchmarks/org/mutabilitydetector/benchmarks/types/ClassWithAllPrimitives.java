/* 
 * Mutability Detector
 *
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector.benchmarks.types;

@SuppressWarnings("unused")
public class ClassWithAllPrimitives {
	public static class Boolean {
		private boolean aBoolean;
	}
	
	public static final class Byte {
		private byte aByte;
	}
	
	public static final class Char {
		private char aChar;
	}
	
	public static final class Short {
		private short aShort;
	}

	public static final class Int {
		private int anInt;
	}

	public static final class Long {
		private long aLong;
	}

	public static final class Float {
		private float aFloat;
	}

	public static final class Double {
		private double aDouble;
	}

	public static final class Array {
		private Object[] anArray;
	}
	
	
	
}
