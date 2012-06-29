package org.mutabilitydetector.asmoverride;

import org.junit.Test;
import org.mutabilitydetector.asmoverride.NonClassloadingSimpleVerifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;

public class NonClassloadingSimpleVerifierTest {

    @Test
    public void testDoesntThrowExceptionOnAnalyzing() throws Exception {
        ClassReader classReader = new ClassReader(InvokesMethodWithSeveralImplementingClasses.class.getName());
        classReader.accept(new VerifyEachMethodClassVisitor(), 0);
    }

    private static class InvokesMethodWithSeveralImplementingClasses {
        
        public void method() {
            MyInterface myInterface = new FirstImplementation();
            if (System.currentTimeMillis() % 2 == 0) {
                myInterface = new SecondImplementation();
            }
            
            takesInterface(myInterface);
        }
        
        private void takesInterface(MyInterface m) {
            System.out.println(m);
        }
        
    }
    
    private static interface MyInterface { }
    private static class FirstImplementation implements MyInterface { }
    private static class SecondImplementation implements MyInterface { }
    
    private static class VerifyEachMethodClassVisitor extends ClassVisitor {

        private String owner;

        public VerifyEachMethodClassVisitor() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.owner = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            super.visitMethod(access, name, desc, signature, exceptions);
            return new MethodNodeWithVisitEnd(access, name, desc, signature, exceptions, owner);
        }

    }

    private static class MethodNodeWithVisitEnd extends MethodNode {

        private final String owner;

        public MethodNodeWithVisitEnd(int access, String name, String desc, String signature, String[] exceptions,
                String owner) {
            super(access, name, desc, signature, exceptions);
            this.owner = owner;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            try {
                new Analyzer<BasicValue>(new NonClassloadingSimpleVerifier()).analyze(owner, this);
            } catch (AnalyzerException e) {
                throw new RuntimeException(e);
            }
        }
    }
    

}
