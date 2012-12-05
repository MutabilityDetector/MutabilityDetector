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
package org.mutabilitydetector.checkers;

import static com.google.common.collect.Lists.newArrayList;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.Collection;

import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractMutabilityChecker extends AsmMutabilityChecker {

    protected Collection<MutableReasonDetail> reasons = newArrayList();
    private IsImmutable isImmutable = IsImmutable.IMMUTABLE;

    protected String ownerClass;

    @Override
    public IsImmutable result() {
        return isImmutable;
    }

    @Override
    public Collection<MutableReasonDetail> reasons() {
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
    public final void visitAnalysisException(Throwable toBeThrown) {
        setResult(errorReasonDescription(toBeThrown), getCodeLocationForException(), MutabilityReason.CANNOT_ANALYSE);
    }

    private String errorReasonDescription(Throwable toBeThrown) {
        return "Encountered an unhandled error in analysis.";
    }
    
    

    public CodeLocation<?> getCodeLocationForException() {
        return ownerClass != null 
                ? ClassLocation.from(slashed(ownerClass))
                : CodeLocation.UnknownCodeLocation.UNKNOWN;
    }

    protected MutableReasonDetail createResult(String message, CodeLocation<?> location, Reason reason) {
        return MutableReasonDetail.newMutableReasonDetail(message, location, reason);
    }

    protected void setResult(String message, CodeLocation<?> location, Reason reason) {
        reasons.add(createResult(message, location, reason));
        isImmutable = reason.createsResult();
    }
    
    @Override
    public CheckerResult checkerResult() {
        return new CheckerResult(isImmutable, reasons);
    }

}
