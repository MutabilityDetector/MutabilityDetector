package org.objectweb.asm.apilearning;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.testsubjects.lazy.BasicSingleCheckLazyInitialisation;


/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 03.12.2012
 */
public final class AsmApiLearningTest {

    private static final class VisitDao {
        private final static class Builder {
            private int version = 0;
            private int access = 0;
            private String name = "";
            private String descriptor = "";
            private String signature = "";
            private String superName = "";
            private List<String> interfaces = Collections.emptyList();
            private List<String> exceptions = Collections.emptyList();

            public Builder version(int version) {
                this.version = version;
                return this;
            }

            public Builder access(int access) {
                this.access = access;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder descriptor(final String descriptor) {
                this.descriptor = descriptor;
                return this;
            }

            public Builder signature(String signature) {
                this.signature = signature;
                return this;
            }

            public Builder superName(final String superName) {
                this.superName = superName;
                return this;
            }

            public Builder interfaces(final String[] interfaces) {
                this.interfaces = toList(interfaces);
                return this;
            }

            private List<String> toList(final String[] stringArray) {
                if (null != stringArray) {
                    return Arrays.asList(stringArray);
                }
                return Collections.emptyList();
            }

            public Builder exceptions(final String[] exceptions) {
                this.exceptions = toList(exceptions);
                return this;
            }

            public VisitDao build() {
                return new VisitDao(this);
            }

        }

        public final int version;
        public final int access;
        public final String name;
        public final String descriptor;
        public final String signature;
        public final String superName;
        public final List<String> interfaces;
        public final List<String> exceptions;

        private VisitDao(final Builder builder) {
            version = builder.version;
            access = builder.access;
            name = builder.name;
            descriptor = builder.descriptor;
            signature = builder.signature;
            superName = builder.superName;
            interfaces = Collections.unmodifiableList(builder.interfaces);
            exceptions = Collections.unmodifiableList(builder.exceptions);
        }
    }

    private static final class TestMethodVisitor extends MethodNode {

        public TestMethodVisitor() {
            super();
        }
        
    }

    private static final class TestClassVisitor extends ClassNode {

        private VisitDao headerData;
        private VisitDao methodData;

        public TestClassVisitor() {
            super();
            final VisitDao.Builder builder = new VisitDao.Builder();
            headerData = builder.build();
            methodData = builder.build();
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            final VisitDao.Builder builder = new VisitDao.Builder();
            builder.version(version).access(access).name(name).signature(signature).superName(superName);
            builder.interfaces(interfaces);
            headerData = builder.build();
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return new TestMethodVisitor();
//            final VisitDao.Builder builder = new VisitDao.Builder();
//            builder.access(access).name(name).descriptor(desc).signature(signature).exceptions(exceptions);
//            methodData = builder.build();
        }

        public VisitDao headerData() {
            return headerData;
        }

        public VisitDao methodData() {
            return methodData;
        }

    }

    private final ClassReader classReader;
    private TestClassVisitor visitor;

    public AsmApiLearningTest() throws IOException {
        super();
        classReader = new ClassReader(BasicSingleCheckLazyInitialisation.class.getName());
        visitor = null;
    }

    @Before
    public void setUp() {
        visitor = new TestClassVisitor();
        classReader.accept(visitor, 0);
    }

    @Test
    public void classHasExpectedVersion() {
        final Integer expectedVersion = 50;
        assertThat(visitor.headerData().version, equalTo(expectedVersion));
    }

    @Test
    public void treeClassNodeHasExpectedVersion() {
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        assertThat(classNode.version, equalTo(50));
    }

    @Test
    public void classHasExpectedAccessFlags() {
        final int expectedAccess = ACC_PUBLIC + ACC_FINAL + ACC_SUPER;
        assertThat(visitor.headerData().access, equalTo(expectedAccess));
    }

    @Test
    public void classIsPublicFinal() {
        final int access = visitor.headerData().access;
        final boolean isPublic = Modifier.isPublic(access);
        final boolean isFinal = Modifier.isFinal(access);
        assertThat(true, allOf(is(isPublic), is(isFinal)));
    }

    @Test
    public void classHasExpectedName() {
        assertThat(visitor.headerData().name, equalTo("de/htwg_konstanz/jia/testsubjects/lazy/BasicLazyInitialisation"));
    }

    @Test
    public void classHasExpectedSignature() {
        final String expectedSignature = null;
        assertThat(visitor.headerData().signature, equalTo(expectedSignature));
    }

    @Test
    public void classHasExpectedSuperName() {
        final String expectedSuperName = "java/lang/Object";
        assertThat(visitor.headerData().superName, equalTo(expectedSuperName));
    }

    @Test
    public void classHasExpectedInterfaces() {
        assertThat(visitor.headerData().interfaces, equalTo(Collections.EMPTY_LIST));
    }

}
