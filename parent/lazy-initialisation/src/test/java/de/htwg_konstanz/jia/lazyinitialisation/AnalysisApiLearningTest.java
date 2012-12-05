/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.Frame;

import de.htwg_konstanz.jia.testsubjects.lazy.BasicLazyInitialisation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 05.12.2012
 */
public final class AnalysisApiLearningTest {

    private enum Type {
        INSN,
        INT_INSN,
        VAR_INSN,
        TYPE_INSN,
        FIELD_INSN,
        METHOD_INSN,
        INVOKE_DYNAMIC_INSN,
        JUMP_INSN,
        LABEL,
        LDC_INSN,
        IINC_INSN,
        TABLESWITCH_INSN,
        LOOKUPSWITCH_INSN,
        MULTIANEWARRAY_INSN,
        FRAME,
        LINE;
    }

    @Test
    public void numberOfInstructionsIsExpected() throws AnalyzerException {
        final int expected = 4;
        final ClassNode classNode = getNodeForClass(BasicLazyInitialisation.class);
        final List<MethodNode> methods = getMethodsWithName("hashCode", classNode);
        assertThat(methods.size(), equalTo(1));

        final MethodNode method = methods.get(0);
        final Analyzer analyser = new Analyzer(new BasicInterpreter());
        analyser.analyze(classNode.name, method);
//        final Frame[] frames = analyser.getFrames();
        final InsnList instructions = method.instructions;
        @SuppressWarnings("unchecked")
        final ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        final String msgTemplate = "Instruction: '%s', Type: %s, Opcode: %d";
        while (iterator.hasNext()) {
            final AbstractInsnNode instn = iterator.next();
            System.out.println(String.format(msgTemplate, instn.toString(), getTypeString(instn.getType()),
                    instn.getOpcode()));
        }
        assertThat(instructions.size(), equalTo(expected));
    }

    private String getTypeString(final int type) {
        return Type.values()[type].name();
    }

    private List<MethodNode> getMethodsWithName(final String methodName, final ClassNode classNode) {
        @SuppressWarnings("unchecked")
        final List<MethodNode> methods = classNode.methods;
        final List<MethodNode> result = new ArrayList<MethodNode>(methods.size());
        for (final MethodNode methodNode : methods) {
            if (methodName.equals(methodNode.name)) {
                result.add(methodNode);
            }
        }
        return result;
    }

    private ClassNode getNodeForClass(final Class<?> klasse) {
        final String className = klasse.getName();
        final ClassReader classReader = tryToInstantiateClassReader(className);
        final ClassNode result = new ClassNode();
        classReader.accept(result, ClassReader.SKIP_DEBUG);
        return result;
    }

    private ClassReader tryToInstantiateClassReader(final String className) {
        try {
            return new ClassReader(className);
        } catch (final IOException e) {
            final String msgTemplate = "ClassReader could not have been instantiated for class '%s'.";
            final RuntimeException exception = new NullPointerException(String.format(msgTemplate, className));
            exception.initCause(e);
            throw exception;
        }
    }

}
