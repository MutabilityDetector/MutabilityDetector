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

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;

import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.locations.ClassNameConvertor;

public class InheritedMutabilityChecker extends AbstractMutabilityChecker {

	private IAnalysisSession analysisSession;
	
	public InheritedMutabilityChecker(IAnalysisSession analysisSession) {
		this.analysisSession = analysisSession;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		
		if(superName == null) {
			result = DEFINITELY; // for java.lang.Object
			return;
		}
		
		String dottedSuperName = new ClassNameConvertor().dotted(superName);
		if(analysisSession.isImmutable(dottedSuperName).equals(DEFINITELY_NOT)) {
			result = DEFINITELY_NOT;
		} else {
			result = DEFINITELY;
		}
	}

}
