package org.objectweb.asm.apilearning;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import de.htwg_konstanz.jia.lazyinitialisation.Opcode;

/**
 * Example from the ASM 4 Guide.
 *
 * @author Juergen Fickel
 * @version 08.02.2013
 */
public final class CustomDataFlowAnalysis {

    private static final class IsNullInterpreter extends BasicInterpreter {
        private final BasicValue nullValue;
        private final BasicValue referenceValue;
        private final BasicValue maybeNullValue;

        public IsNullInterpreter() {
            super(ASM4);
            nullValue = new BasicValue(null);
            referenceValue = new BasicValue(null);
            maybeNullValue = new BasicValue(null);
        }

        @Override
        public BasicValue newOperation(final AbstractInsnNode insn) throws AnalyzerException {
            if (ACONST_NULL == insn.getOpcode()) {
                return nullValue;
            }
            return super.newOperation(insn);
        }

        @Override
        public BasicValue merge(final BasicValue v, final BasicValue w) {
            if (isRef(v) && isRef(w) && v != w) {
                return maybeNullValue;
            }
            return super.merge(v, w);
        }

        private boolean isRef(final Value v) {
            return isNotNull(v) || isNull(v) || isMaybeNull(v);
        }

        public boolean isNull(final Value v) {
            return nullValue.equals(v);
        }

        public boolean isNotNull(final Value v) {
            return referenceValue.equals(v);
        }

        public boolean isMaybeNull(final Value v) {
            return maybeNullValue.equals(v);
        }
    } // class IsNullInterpreter


    private static final class NullDereferenceAnalyser {
        public List<AbstractInsnNode> findNullDereferences(final String owner, final MethodNode methodNode) throws AnalyzerException {
            final List<AbstractInsnNode> result = new ArrayList<AbstractInsnNode>();
            final IsNullInterpreter interpreter = new IsNullInterpreter();
            final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(interpreter);
            analyser.analyze(owner, methodNode);
            final Frame<BasicValue>[] frames = analyser.getFrames();
            final AbstractInsnNode[] instructions = methodNode.instructions.toArray();
            for (int i = 0; i < instructions.length; i++) {
                final AbstractInsnNode instruction = instructions[i];
                if (null != frames[i]) {
                    final BasicValue v = getTarget(instruction, frames[i]);
                    if (interpreter.isNull(v) || interpreter.isMaybeNull(v)) {
                        final Opcode opcode = Opcode.forInt(instruction.getOpcode());
                        System.out.println(String.format("Instruction: '%s', Target value: '%s'.", opcode,
                                null == v.getType() ? "null" : v));
                        result.add(instruction);
                    }
                }
            }
            return result;
        }

        private static BasicValue getTarget(final AbstractInsnNode instruction, final Frame<BasicValue> f) {
            final Opcode opcode = Opcode.forInt(instruction.getOpcode());
            switch (opcode) {
            case GETFIELD:
            case ARRAYLENGTH:
            case MONITORENTER:
            case MONITOREXIT:
                return getStackValue(f, 0);
            case PUTFIELD:
                return getStackValue(f, 1);
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                final String description = ((MethodInsnNode) instruction).desc;
                return getStackValue(f, Type.getArgumentTypes(description).length);
            default:
                break;
            }
            return null;
        }

        private static BasicValue getStackValue(final Frame<BasicValue> f, final int index) {
            final int top = f.getStackSize() - 1;
            return index <= top ? f.getStack(top - index) : null;
        }
    } // NullDereferenceAnalyser


    private static final class PotentialNullPointer {
        @SuppressWarnings({ "unused", "null" })
        public Object doSomething() {
            Object result = null;
            int i = 9;
            while (i >= 0) {
                System.out.println(String.format("Bin bei %d", i));
                i--;
            }
            System.out.println(String.format("Result: '%s'.", result.toString()));
            return result;
        }
    }


    @Test
    public void findNullDereferences() throws IOException, AnalyzerException {
        final ClassName dotted = Dotted.fromClass(PotentialNullPointer.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final MethodNode hashCodeMethodNode = findMethodWithName(cn, "doSomething");
        final NullDereferenceAnalyser nda = new NullDereferenceAnalyser();
        final List<AbstractInsnNode> nullDereferences = nda.findNullDereferences(cn.name, hashCodeMethodNode);
        assertThat(nullDereferences.size(), is(1));
    }

    private static MethodNode findMethodWithName(final ClassNode cn, final String methodName) {
        for (final MethodNode methodNode : cn.methods) {
            if (methodName.equals(methodNode.name)) {
                return methodNode;
            }
        }
        return null;
    }
}
