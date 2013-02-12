/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
final class PublicMethodVisitor extends MethodVisitor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String owner;
    private final VariableAssignmentCollection variableAssignments;
    private final Map<String, Stack<Value>> fieldStacks;
    private Label currentLabel;

    private final MethodNode methodNode;
    private final List<JumpInsnNode> jumpInstructions;

    public PublicMethodVisitor(final int access, final String name, final String desc, final String signature,
            final String[] exceptions, final String theOwner, final List<FieldNode> thePotentialLazyVariables) {
        super(Opcodes.ASM4);
        owner = theOwner;
        variableAssignments = VariableAssignmentCollection.newInstance();
        for (final FieldNode fieldNode : thePotentialLazyVariables) {
            variableAssignments.addVariable(fieldNode);
        }
        fieldStacks = new HashMap<String, Stack<Value>>();
        methodNode = new MethodNode();
        jumpInstructions = new ArrayList<JumpInsnNode>();
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        logger.debug("Delegating to methodNode.");
        return methodNode.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        logger.debug("Delegating to methodNode; desc: {}, visible: {}.", desc, visible);
        return methodNode.visitAnnotation(desc, visible);
    }

    @Override
    public void visitCode() {
        logger.debug("Delegating to methodNode.");
        methodNode.visitCode();
    }

    @Override
    public void visitLabel(final Label label) {
        logger.debug("Setting current label to '{}'.", label);
        currentLabel = label;
        methodNode.visitLabel(label);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        logger.debug("Delegating to methodNode; opcode: {}, operand: {}.", getOpcodeString(opcode), operand);
        methodNode.visitIntInsn(opcode, operand);
    }

    private static String getOpcodeString(final int theOpcode) {
        final Opcode opcode = Opcode.forInt(theOpcode);
        return opcode.toString();
    }

    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack,
            final Object[] stack) {
        logger.debug("Delegating to methodNode; type: {}, nLocal: {}, local: {}, nStack: {}, stack: {}.", type, nLocal,
                local, nStack, stack);
        methodNode.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        logger.debug("Delegating to methodNode; opcode: {}, var: {}.", getOpcodeString(opcode), var);
        methodNode.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        logger.debug("Delegating to methodNode; opcode: {}, type: {}.", getOpcodeString(opcode), type);
        methodNode.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if (Opcodes.GETFIELD == opcode) {
            if (this.owner.equals(owner)) {
                logger.debug("GETFIELD for variable '{}' of owner '{}'.", name, owner);
                final FieldNode v = findVariable(name, desc);
                if (null != v) {
                    final Stack<Value> stack = new Stack<Value>();
                    stack.push(new BasicValue(Type.getType(desc)));
                    fieldStacks.put(name, stack);
                }
            }
        }

        if (Opcodes.PUTFIELD == opcode) {
            final LabelNode labelNode = getLabelNode(currentLabel);
            final FieldInsnNode assignmentNode = new FieldInsnNode(opcode, owner, name, desc);
            logger.debug("Adding assignment instruction; labelNode: '{}', assignmentInstructionNode: '{}'.", labelNode,
                    assignmentNode);
            variableAssignments.addAssignmentInstructionForVariable(name,
                    AssignmentInsn.getInstance(labelNode, assignmentNode));
        }
        logger.debug("Delegating to methodNode; opcode: {}, owner: {}, name: {}, desc: {}.", getOpcodeString(opcode),
                owner, name, desc);
        methodNode.visitFieldInsn(opcode, owner, name, desc);
    }

    private FieldNode findVariable(final String aName, final String aDesc) {
        for (final FieldNode v : variableAssignments.getVariables()) {
            if (v.name.equals(aName) && v.desc.equals(aDesc)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public void visitInsn(final int opcode) {
        logger.debug("Delegating to methodNode; opcode: {}.", getOpcodeString(opcode));
        methodNode.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        logger.debug("Delegating to methodNode; opcode: {}, owner: {}, name: {}, desc: {}.", getOpcodeString(opcode),
                owner, name, desc);
        methodNode.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        methodNode.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        jumpInstructions.add(new JumpInsnNode(opcode, getLabelNode(label)));
        logger.debug("Delegating to methodNode; opcode:{}, label: {}", getOpcodeString(opcode), label);
        methodNode.visitJumpInsn(opcode, label);
    }

    private static LabelNode getLabelNode(final Label label) {
        if (!(label.info instanceof LabelNode)) {
            label.info = new LabelNode(label);
        }
        return (LabelNode) label.info;
    }

}
