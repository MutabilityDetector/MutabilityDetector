package org.mutabilitydetector.checkers;

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


import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.asmoverride.AsmClassVisitor;
import org.mutabilitydetector.locations.CodeLocation;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.mutabilitydetector.checkers.CheckerResult.IMMUTABLE_CHECKER_RESULT;

@NotThreadSafe
public abstract class AsmMutabilityChecker extends AsmClassVisitor {

    private CheckerResult checkerResult = IMMUTABLE_CHECKER_RESULT;

    protected Collection<MutableReasonDetail> reasons = newArrayList();
    
    private boolean isClassSelfReferenced=false;
    
    public String ownerClass() {
        return ownerClass;
    }

    protected void setIsClassSelfReferenced(boolean isClassSeflReferenced) {
        this.isClassSelfReferenced = isClassSeflReferenced;
    }
    
    protected Boolean isClassSelfReferenced() {
        return new Boolean(isClassSelfReferenced);
    }
    protected void setResult(String message, CodeLocation<?> location, Reason reason) {
        reasons.add(createReasonDetail(message, location, reason));
        this.checkerResult = CheckerResult.withNoErrors(reason.createsResult(), reasons);
    }

    public CheckerResult checkerResult() {
        return checkerResult;
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

    protected MutableReasonDetail createReasonDetail(String message, CodeLocation<?> location, Reason reason) {
        return MutableReasonDetail.newMutableReasonDetail(message, location, reason);
    }

}
