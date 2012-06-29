/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.mutabilitydetector.asmoverride;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.SimpleVerifier;

/**
 * An extended {@link SimpleVerifier} that guarantees not to load classes.
 * 
 * Delegates to an underlying {@link TypeHierarchyReader} to perform the
 * necessary visiting of class files to find the information required for
 * verification.
 * 
 * The default implementation of {@link TypeHierarchyReader} will attempt to
 * read classes using a {@link ClassReader} which reads class files using
 * {@link ClassLoader#getSystemResourceAsStream(String)}. Unlike with native
 * classloading, there is no caching used in this verifier, which will almost
 * certainly degrade performance. To maintain performance, supply an alternative
 * {@link TypeHierarchyReader} which can using a caching strategy best suited to
 * your environment.
 * 
 * @see ClassReader#ClassReader(String)
 * @see SimpleVerifier
 * @see Type
 * @see TypeHierarchyReader
 * 
 * @author Graham Allan
 * 
 */
public class NonClassloadingSimpleVerifier extends SimpleVerifier {

    /**
     * Used to obtain hierarchy information used in verification.
     */
    protected final TypeHierarchyReader typeHierarchyReader;

    /**
     * Default constructor which chooses a naive {@link TypeHierarchyReader}.
     */
    public NonClassloadingSimpleVerifier() {
        typeHierarchyReader = new TypeHierarchyReader();
    }

    /**
     * Constructor which uses the given {@link TypeHierarchyReader} to obtain
     * hierarchy information for given {@link Type}s.
     */
    public NonClassloadingSimpleVerifier(TypeHierarchyReader reader) {
        typeHierarchyReader = reader;
    }

    /**
     * Unconditionally throws an {@link Error}. This method should never be
     * called.
     */
    @Override
    protected Class< ? > getClass(Type t) {
        throw new Error("Programming error: this verifier should "
                + "not be attempting to load classes.");
    }

    /**
     * Immediately delegates and returns the result of the equivalent method of
     * the underlying {@link TypeHierarchyReader}.
     * 
     * @see TypeHierarchyReader#isInterface(Type)
     */
    @Override
    protected boolean isInterface(final Type t) {
        return typeHierarchyReader.isInterface(t);
    }

    /**
     * Immediately delegates and returns the result of the equivalent method of
     * the underlying {@link TypeHierarchyReader}.
     * 
     * @see TypeHierarchyReader#getSuperClass(Type)
     */
    @Override
    protected Type getSuperClass(final Type t) {
        return typeHierarchyReader.getSuperClass(t);
    }

    /**
     * Immediately delegates and returns the result of the equivalent method of
     * the underlying {@link TypeHierarchyReader}.
     * 
     * @see TypeHierarchyReader#isAssignableFrom(Type, Type)
     */
    @Override
    protected boolean isAssignableFrom(Type toType, Type fromType) {
        return typeHierarchyReader.isAssignableFrom(toType, fromType);
    }
}
