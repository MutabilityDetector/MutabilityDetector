/*
 * Mutability Detector
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Further licensing information for this project can be found in 
 * 		license/LICENSE.txt
 */
package org.mutabilitydetector.asmoverride;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

public class CustomClassLoadingSimpleVerifier extends SimpleVerifier {

	@Override
	protected Class<?> getClass(Type t) {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			if (t.getSort() == Type.ARRAY) {
				return Class.forName(t.getDescriptor().replace('/', '.'), false, loader);
			}
			return Class.forName(t.getClassName(), false, loader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.toString());
		}
	}
}
