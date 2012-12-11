/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;

import org.apache.commons.lang3.Validate;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

import de.htwg_konstanz.jia.testsubjects.lazy.BasicLazyInitialisation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 05.12.2012
 */
public final class AnalysisApiLearningTest {

    private interface ToAbstractInsnConvertible {
        AbstractInsn convert(AbstractInsnNode abstractInsnNode);
    }

    private enum Type implements ToAbstractInsnConvertible {
        INSN {
            @Override
            public AbstractInsn convert(final AbstractInsnNode abstractInsnNode) {
                return new Insn(abstractInsnNode);
            }
        },
        INT_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new IntInsn(abstractInsnNode);
            }
        },
        VAR_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new VarInsn(abstractInsnNode);
            }
        },
        TYPE_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        FIELD_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new FieldInsn(abstractInsnNode);
            }
        },
        METHOD_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        INVOKE_DYNAMIC_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        JUMP_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new JumpInsn(abstractInsnNode);
            }
        },
        LABEL {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new Label(abstractInsnNode);
            }
        },
        LDC_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        IINC_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        TABLESWITCH_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        LOOKUPSWITCH_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        MULTIANEWARRAY_INSN {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        },
        FRAME {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return new Frame(abstractInsnNode);
            }
        },
        LINE {
            @Override
            public AbstractInsn convert(AbstractInsnNode abstractInsnNode) {
                return null;
            }
        };

        public abstract AbstractInsn convert(AbstractInsnNode abstractInsnNode);
    }

    @Test
    public void numberOfInstructionsIsExpected() throws AnalyzerException {
        final ClassNode classNode = getNodeForClass(BasicLazyInitialisation.class);
        final List<MethodNode> methods = getMethodsWithName("hashCode", classNode);
        assertThat(methods.size(), equalTo(1));

        final MethodNode method = methods.get(0);
        final Analyzer analyser = new Analyzer(new BasicInterpreter());
        analyser.analyze(classNode.name, method);
        final InsnListProcessor processor = new InsnListProcessor(method.instructions);
        processor.run();
    }

    private static abstract class AbstractInsn implements Appendable {

        private final StringBuilder builder;

        public AbstractInsn(final AbstractInsnNode abstractInsnNode) {
            super();
            Validate.notNull(abstractInsnNode);
            builder = new StringBuilder();
            builder.append(String.format("Type: %s", getTypeString(abstractInsnNode.getType())));
            builder.append(getOpcodeString(abstractInsnNode.getOpcode()));
        }

        private String getOpcodeString(final int opcodeAsInt) {
            String result = "";
            if (isNotLabel(opcodeAsInt)) {
                final String template = ", Opcode: %s (int: %d, hex: %s)";
                final Opcode opcode = Opcode.values()[opcodeAsInt];
                result = String.format(template, opcode.name(), opcodeAsInt, opcode.asHex());
            }
            return result;
        }
        
        private String getTypeString(final int type) {
            return Type.values()[type].name();
        }

        private boolean isNotLabel(final int opcodeAsInt) {
            return -1 < opcodeAsInt;
        }

        @Override
        public final AbstractInsn append(final CharSequence csq) {
            builder.append(csq);
            return this;
        }

        @Override
        public final AbstractInsn append(final CharSequence csq, final int start, final int end) {
            builder.append(csq, start, end);
            return this;
        }

        @Override
        public final AbstractInsn append(final char c) {
            builder.append(c);
            return this;
        }

        @Override
        public abstract String toString();

        protected String defaultToString() {
            return builder.toString();
        }

    }

    private static final class Insn extends AbstractInsn {

        public Insn(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class VarInsn extends AbstractInsn {

        public VarInsn(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(VarInsnNode.class, abstractInsnNode);
            final VarInsnNode varInsnNode = (VarInsnNode) abstractInsnNode;
            append(String.format(", Var: %d", varInsnNode.var));
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class FieldInsn extends AbstractInsn {
        
        public FieldInsn(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(FieldInsnNode.class, abstractInsnNode);
            final FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
            append(String.format(", Desc: %s", fieldInsnNode.desc));
            append(String.format(", Name: %s", fieldInsnNode.name));
            append(String.format(", Owner: %s", fieldInsnNode.owner));
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class JumpInsn extends AbstractInsn {

        public JumpInsn(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(JumpInsnNode.class, abstractInsnNode);
            final JumpInsnNode jumpInsnNode = (JumpInsnNode) abstractInsnNode;
            append(String.format(", Label: %s", getLabelInfo(jumpInsnNode)));
        }

        private String getLabelInfo(final JumpInsnNode jumpInsnNode) {
            final LabelNode labelNode = jumpInsnNode.label;
            final org.objectweb.asm.Label label = labelNode.getLabel();
            final Object info = label.info;
            if (null == info) {
                return "null";
            }
            return info.toString();
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class IntInsn extends AbstractInsn {

        public IntInsn(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(IntInsnNode.class, abstractInsnNode);
            final IntInsnNode intInsnNode = (IntInsnNode) abstractInsnNode;
            append(String.format(", Operand: %d", intInsnNode.operand));
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class Label extends AbstractInsn {
        
        public Label(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(LabelNode.class, abstractInsnNode);
            final LabelNode labelNode = (LabelNode) abstractInsnNode;
            append(String.format(", Info: %s", labelNode.getLabel().info));
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class Frame extends AbstractInsn {

        public Frame(final AbstractInsnNode abstractInsnNode) {
            super(abstractInsnNode);
            Validate.isInstanceOf(FrameNode.class, abstractInsnNode);
            final FrameNode frameNode = (FrameNode) abstractInsnNode;
            append(String.format(", Local: %s", frameNode.local));
            append(String.format(", Stack: %s", frameNode.stack));
            append(String.format(", Frame type: %s", getFrameTypeName(frameNode.type)));
        }

        private String getFrameTypeName(final int frameType) {
            String result = "";

            switch (frameType) {
            case Opcodes.F_NEW:
                result = "F_NEW";
                break;
            case Opcodes.F_APPEND:
                result = "F_APPEND";
                break;
            case Opcodes.F_CHOP:
                result = "F_CHOP";
                break;
            case Opcodes.F_SAME:
                result = "F_SAME";
                break;
            case Opcodes.F_SAME1:
                result = "F_SAME1";
                break;
            default:
                result = "F_FULL";
                break;
            }
            return result;
        }

        @Override
        public String toString() {
            return defaultToString();
        }
    }

    private static final class InsnListProcessor {

        private final InsnList insnList;
        private final List<AbstractInsn> abstractInsns;

        public InsnListProcessor(final InsnList insnList) {
            super();
            Validate.notNull(insnList);
            this.insnList = insnList;
            abstractInsns = new ArrayList<AbstractInsn>(insnList.size());
        }

        public void run() {
            @SuppressWarnings("unchecked")
            final ListIterator<AbstractInsnNode> iterator = insnList.iterator();
            while (iterator.hasNext()) {
                final AbstractInsnNode insnNode = iterator.next();
                final Type type = getType(insnNode.getType());
                abstractInsns.add(type.convert(insnNode));
            }
            for (final AbstractInsn abstractInsn : abstractInsns) {
                System.out.println(abstractInsn);
            }
        }

        private Type getType(final int type) {
            return Type.values()[type];
        }
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

    @Test
    public void printStackOpcodes() {
        print("NOP", Opcode.nop());
        print("LOCAL_VARIABLES", Opcode.localVariables());
        print("STACK", Opcode.stack());
        print("CONSTANTS", Opcode.constants());
        print("ARITHMETIC_AND_LOGIC", Opcode.arithmeticAndLogic());
        print("CASTS", Opcode.casts());
        print("OBJECTS", Opcode.objects());
        print("FIELDS", Opcode.fields());
        print("METHODS", Opcode.methods());
        print("ARRAYS", Opcode.arrays());
        print("JUMPS", Opcode.jumps());
        print("RETURN", Opcode.returns());
    }

    private void print(final String category, SortedSet<Opcode> opcodes) {
        final String template = "%s: %s";
        System.out.println(format(template, category, opcodes));
    }

}
