package org.mutabilitydetector.misc;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2016 Graham Allan
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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.io.PrintWriter;
import java.util.List;

/**
 * Used exclusively for debug and testing ASM visitors functionality.
 */
public final class TracingSignatureVisitor extends SignatureVisitor {
    private List<Integer> treePath;
    private int nextBranch = 1;
    private PrintWriter log;

    private TracingSignatureVisitor(List<Integer> treePath, PrintWriter log) {
        super(Opcodes.ASM5);

        this.treePath = treePath;
        this.log = new PrintWriter(log, true);
    }

    public TracingSignatureVisitor(PrintWriter log) {
        this(ImmutableList.of(1), log);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        trace(String.format("visitFormalTypeParameter(%s)", name));
    }

    @Override
    public SignatureVisitor visitClassBound() {
        trace("visitClassBound()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        trace("visitInterfaceBound()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        trace("visitSuperclass()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitInterface() {
        trace("visitInterface()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitParameterType() {
        trace("visitParameterType()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitReturnType() {
        trace("visitReturnType()");
        return nextVisitor();
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        trace("visitExceptionType()");
        return nextVisitor();
    }

    @Override
    public void visitBaseType(char descriptor) {
        trace("visitBaseType(%s)", "" + descriptor);
    }

    @Override
    public void visitTypeVariable(String name) {
        trace("visitTypeVariable(%s)", name);
    }

    @Override
    public SignatureVisitor visitArrayType() {
        trace("visitArrayType()");
        return nextVisitor();
    }

    @Override
    public void visitClassType(String name) {
        trace("visitClassType(%s)", name);
    }

    @Override
    public void visitInnerClassType(String name) {
        trace("visitInnerClassType(%s)", name);
    }

    @Override
    public void visitTypeArgument() {
        trace("visitTypeArgument()");
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        trace("visitTypeArgument(%s)", "" + wildcard);
        return nextVisitor();
    }

    @Override
    public void visitEnd() {
        trace("visitEnd()");
    }

    private SignatureVisitor nextVisitor() {
        List<Integer> newPath = ImmutableList.<Integer>builder().addAll(treePath).add(nextBranch++).build();
        return new TracingSignatureVisitor(newPath, log);
    }

    private void trace(String formatString, Object... args) {
        log.println(toString() + " " + String.format(formatString, args));
    }

    @Override
    public String toString() {
        return Joiner.on("-").join(treePath);
    }
}
