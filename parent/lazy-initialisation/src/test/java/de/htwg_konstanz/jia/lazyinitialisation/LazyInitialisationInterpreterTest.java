package de.htwg_konstanz.jia.lazyinitialisation;

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
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * Example from the ASM 4 Guide.
 *
 * @author Juergen Fickel
 * @version 08.02.2013
 */
public final class LazyInitialisationInterpreterTest {

    private static final class LazyInitialisationAnalyser {
        public List<AbstractInsnNode> findLazyMethods(final String owner, final MethodNode methodNode) throws AnalyzerException {
            final List<AbstractInsnNode> result = new ArrayList<AbstractInsnNode>();
            final LazyInitialisationInterpreter interpreter = LazyInitialisationInterpreter.getInstance();
            final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(interpreter);
            analyser.analyze(owner, methodNode);
            final Frame<BasicValue>[] frames = analyser.getFrames();
            final AbstractInsnNode[] instructions = methodNode.instructions.toArray();
            for (int i = 0; i < instructions.length; i++) {
                final AbstractInsnNode instruction = instructions[i];
                final Opcode opcode = Opcode.forInt(instruction.getOpcode());
                final Frame<BasicValue> currentFrame = frames[i];
                if (null != currentFrame) {
                    final BasicValue target = getTarget(instruction, currentFrame);
//                    if (interpreter.isNull(target) || interpreter.isMaybeNull(target)) {
//                        final Opcode opcode = Opcode.forInt(instruction.getOpcode());
//                        System.out.println(String.format("Instruction: '%s', Target value: '%s'.", opcode,
//                                null == target.getType() ? "null" : target));
//                        result.add(instruction);
//                    }
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
    } // class NullDereferenceAnalyser


    @Test
    public void findNullDereferences() throws IOException, AnalyzerException {
        final ClassName dotted = Dotted.fromClass(WithoutAlias.WithJvmInitialValue.IntegerValid.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final MethodNode hashCodeMethodNode = findMethodWithName(cn, "hashCode");
        final LazyInitialisationAnalyser analyser = new LazyInitialisationAnalyser();
        final List<AbstractInsnNode> nullDereferences = analyser.findLazyMethods(cn.name, hashCodeMethodNode);
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

    @Test
    public void name() throws IOException, AnalyzerException {
        final ClassName dotted = Dotted.fromClass(WithoutAlias.WithJvmInitialValue.FloatValid.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final MethodNode hashCodeMethodNode = findMethodWithName(cn, "hashCodeFloat");
        final LazyInitialisationAnalyser analyser = new LazyInitialisationAnalyser();
        final List<AbstractInsnNode> nullDereferences = analyser.findLazyMethods(cn.name, hashCodeMethodNode);
    }

}
