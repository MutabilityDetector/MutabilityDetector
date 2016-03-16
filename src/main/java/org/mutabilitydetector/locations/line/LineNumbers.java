package org.mutabilitydetector.locations.line;

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

import com.google.common.collect.Maps;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.objectweb.asm.ClassReader.SKIP_FRAMES;
import static org.objectweb.asm.Type.ARRAY;

/**
 * Looks up line numbers for classes and their members.
 * <p/>Copypasted from Guice with slight modifications.
 *
 * @author Chris Nokleberg
 */
final class LineNumbers {

    private final Map<String, Integer> lines = Maps.newHashMap();
    private String source;
    private int firstLine = Integer.MAX_VALUE;

    /**
     * Reads line number information from the given class, if available.
     *
     * @param type the class to read line number information from
     * @throws IllegalArgumentException if the bytecode for the class cannot be found
     * @throws java.io.IOException      if an error occurs while reading bytecode
     */
    public LineNumbers(Type type) throws IOException {
        if (isNotArray(type)) {
            InputStream in = LineNumbers.class.getResourceAsStream("/" + type.getInternalName() + ".class");
            if (in != null) {
                try {
                    new ClassReader(in).accept(new LineNumberReader(), SKIP_FRAMES);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    private boolean isNotArray(Type type) {
        return ARRAY != type.getSort();
    }

    /**
     * Get the source file name as read from the bytecode.
     *
     * @return the source file name if available, or null
     */
    public String getSource() {
        return source;
    }

    public Integer getLineNumberOfField(String fieldName) {
        return lines.get(fieldMemberKey(fieldName));
    }

    /**
     * Gets the first line number.
     */
    public int getFirstLine() {
        return firstLine == Integer.MAX_VALUE ? 1 : firstLine;
    }

    private String fieldMemberKey(String memberName) {
        return memberName;
    }

    private class LineNumberReader extends ClassVisitor {

        private int line = -1;
        private String pendingMethod;
        private String name;

        LineNumberReader() {
            super(Opcodes.ASM5);
        }

        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {
            this.name = name;
        }

        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            pendingMethod = name + desc;
            line = -1;
            return new LineNumberMethodVisitor();
        }

        public void visitSource(String source, String debug) {
            LineNumbers.this.source = source;
        }

        public void visitLineNumber(int line, Label start) {
            if (line < firstLine) {
                firstLine = line;
            }

            this.line = line;
            if (pendingMethod != null) {
                lines.put(pendingMethod, line);
                pendingMethod = null;
            }
        }

        public FieldVisitor visitField(int access, String name, String desc,
                                       String signature, Object value) {
            return null;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return new LineNumberAnnotationVisitor();
        }

        class LineNumberMethodVisitor extends MethodVisitor {
            LineNumberMethodVisitor() {
                super(Opcodes.ASM5);
            }

            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return new LineNumberAnnotationVisitor();
            }

            public AnnotationVisitor visitAnnotationDefault() {
                return new LineNumberAnnotationVisitor();
            }

            public void visitFieldInsn(int opcode, String owner, String name,
                                       String desc) {
                if (opcode == Opcodes.PUTFIELD && LineNumberReader.this.name.equals(owner)
                        && !lines.containsKey(name) && line != -1) {
                    lines.put(name, line);
                }
            }

            public void visitLineNumber(int line, Label start) {
                LineNumberReader.this.visitLineNumber(line, start);
            }
        }

        class LineNumberAnnotationVisitor extends AnnotationVisitor {
            LineNumberAnnotationVisitor() {
                super(Opcodes.ASM5);
            }

            public AnnotationVisitor visitAnnotation(String name, String desc) {
                return this;
            }

            public AnnotationVisitor visitArray(String name) {
                return this;
            }
        }

    }
}