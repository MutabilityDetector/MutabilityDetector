/*
 * Mutability Detector
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * Further licensing information for this project can be found in
 * license/LICENSE.txt
 */

package org.mutabilitydetector.cli;

import static java.lang.String.format;

import java.net.URLClassLoader;

public class URLFallbackClassLoader {

	private final URLClassLoader urlClassLoader;

	public URLFallbackClassLoader() {
		this.urlClassLoader = getURLClassLoader();
	}

	public Class<?> getClass(String dottedClassPath) throws ClassNotFoundException {
		Class<?> toReturn = null;
		try {
			toReturn = Class.forName(dottedClassPath);
			return toReturn;
		} catch (ClassNotFoundException e) {
			toReturn = urlClassLoader.loadClass(dottedClassPath);
			return toReturn;
		}
	}
	
	private URLClassLoader getURLClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader instanceof URLClassLoader) {
			return (URLClassLoader) classLoader;
		} else {
			String message = format("Expected currentThread().getContextClassLoader() to return a URLClassLoader, " +
					"but returned %s.", classLoader);
			throw new ClassCastException(message);
		}
	}
}
