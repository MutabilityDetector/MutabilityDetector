/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
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
