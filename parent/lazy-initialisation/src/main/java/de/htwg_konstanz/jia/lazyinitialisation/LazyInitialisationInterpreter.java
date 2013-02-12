/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 12.02.2013
 */
final class LazyInitialisationInterpreter extends Interpreter<BasicValue> {

    private final List<FieldNode> potentialLazyVariables;
    private final Interpreter<BasicValue> basicInterpreter;

    private LazyInitialisationInterpreter(final List<FieldNode> thePotentialLazyVariables) {
        super(Opcodes.ASM4);
        potentialLazyVariables = Collections.unmodifiableList(thePotentialLazyVariables);
        basicInterpreter = new BasicInterpreter();
    }

    public static LazyInitialisationInterpreter getInstance() {
        return new LazyInitialisationInterpreter(Collections.<FieldNode> emptyList());
    }

    @Override
    public BasicValue newValue(Type type) {
        return basicInterpreter.newValue(type);
    }

    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        return basicInterpreter.newOperation(insn);
    }

    @Override
    public BasicValue copyOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        return basicInterpreter.copyOperation(insn, value);
    }

    @Override
    public BasicValue unaryOperation(AbstractInsnNode insn, BasicValue value) throws AnalyzerException {
        if (isGetfieldInstructions(insn)) {
            System.out.println("Getfield");
        }
        return basicInterpreter.unaryOperation(insn, value);
    }

    private static boolean isGetfieldInstructions(final AbstractInsnNode abstractInstructionNode) {
        return Opcodes.GETFIELD == abstractInstructionNode.getOpcode();
    }

    @Override
    public BasicValue binaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2)
            throws AnalyzerException {
        if (isPutfieldInstruction(insn)) {
            System.out.println("Foo");
        }
        return basicInterpreter.binaryOperation(insn, value1, value2);
    }

    private static boolean isPutfieldInstruction(final AbstractInsnNode abstractInstructionNode) {
        return Opcodes.PUTFIELD == abstractInstructionNode.getOpcode();
    }

    @Override
    public BasicValue ternaryOperation(AbstractInsnNode insn, BasicValue value1, BasicValue value2, BasicValue value3)
            throws AnalyzerException {
        return basicInterpreter.ternaryOperation(insn, value1, value2, value3);
    }

    @Override
    public BasicValue naryOperation(AbstractInsnNode insn, List<? extends BasicValue> values) throws AnalyzerException {
        return basicInterpreter.naryOperation(insn, values);
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, BasicValue value, BasicValue expected) throws AnalyzerException {
        basicInterpreter.returnOperation(insn, value, expected);
    }

    @Override
    public BasicValue merge(BasicValue v, BasicValue w) {
        return basicInterpreter.merge(v, w);
    }

}
