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

import java.util.ArrayList;
import java.util.Collection;

import org.mutabilitydetector.CheckerReasonDetail;
import org.mutabilitydetector.IAnalysisSession.IsImmutable;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;


public abstract class AbstractMutabilityChecker implements IMutabilityChecker {

	protected Collection<CheckerReasonDetail> reasons = new ArrayList<CheckerReasonDetail>();
	private IsImmutable result = IsImmutable.DEFINITELY;
	protected String ownerClass;
	
	@Override
	public IsImmutable result() {
		return result;
	}
	
	@Override
	public Collection<CheckerReasonDetail> reasons() {
		return reasons;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		ownerClass = name;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
		
	}

	@Override
	public void visitEnd() {
		
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		
	}

	@Override
	public void visitSource(String source, String debug) {
		
	}
	
	@Override
	public void visitAnalysisException(Throwable toBeThrown) {
	    
	}

	protected String dottedClassName(Type objectType) {
		String className = objectType.getInternalName();
		String dottedClassName = className.replace("/", ".");
		return dottedClassName;
	}
	
	protected CheckerReasonDetail createResult(String message, CodeLocation location, Reason reason) {
		return new CheckerReasonDetail(message, location, reason);
	}
	
	protected void addResult(String message, CodeLocation location, Reason reason) {
		reasons.add(createResult(message, location, reason));
		result = reason.createsResult();
	}

}
