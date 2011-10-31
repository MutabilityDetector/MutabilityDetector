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
package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.checkers.AccessModifierQuery.type;

import org.mutabilitydetector.MutabilityReason;

public class FinalClassChecker extends AbstractMutabilityChecker {

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if(type(access).isNotFinal()) {
			addResult("Can be subclassed, therefore parameters declared to be this type " +
					"could be mutable subclasses at runtime.", 
					null, MutabilityReason.NOT_DECLARED_FINAL);
		} 
	}

}
