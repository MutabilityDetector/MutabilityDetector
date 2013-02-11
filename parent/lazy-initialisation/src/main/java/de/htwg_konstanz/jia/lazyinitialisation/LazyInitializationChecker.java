package de.htwg_konstanz.jia.lazyinitialisation;

import static java.lang.String.format;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;
import static org.mutabilitydetector.checkers.AccessModifierQuery.method;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;

import java.util.*;

import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 04.02.2013
 */
public final class LazyInitializationChecker extends AbstractMutabilityChecker {

    private final class InstanceVerifier implements Runnable {

        private final VariableSetterMethodCollection instanceVariableSetters;
        private final List<AssignmentInstruction> putfieldInstructions;
        private final String owner;

        public InstanceVerifier() {
            instanceVariableSetters = VariableSetterMethodCollection.newInstance();
            putfieldInstructions = new ArrayList<AssignmentInstruction>();
            owner = classNode.name;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            collectAndAssociateLazyVariablesAndLazyMethods();
//            printAllInstanceVariableSetters();
            examineLazyVariablesAndLazyMethods();
        }

        private void collectAndAssociateLazyVariablesAndLazyMethods() {
            collectPrivateNonFinalInstanceVariables();
            collectSetterMethods();
            instanceVariableSetters.removeUnassociatedVariables();
        }

        private void collectPrivateNonFinalInstanceVariables() {
            for (final FieldNode fieldNode : (List<FieldNode>) classNode.fields) {
                if (isPrivateAndNonFinalInstanceVariable(fieldNode.access)) {
                    instanceVariableSetters.addVariable(fieldNode);
                }
            }
        }

        private boolean isPrivateAndNonFinalInstanceVariable(final int access) {
            return field(access).isNotStatic() && field(access).isPrivate() && field(access).isNotFinal(); 
        }

        private void collectSetterMethods() {
            for (final MethodNode methodNode : (List<MethodNode>) classNode.methods) {
                if (isNotConstructor(methodNode.name)) {
                    for (final AssignmentInstruction putfieldInstruction : getPutfieldInstructions(methodNode.instructions)) {
                        final String nameOfInstanceVariable = putfieldInstruction.getNameOfAssignedVariable();
                        instanceVariableSetters.addSetterMethodForVariable(nameOfInstanceVariable, methodNode);
                    }
                }
            }
        }

        private List<AssignmentInstruction> getPutfieldInstructions(final InsnList instructionsOfMethod) {
            final List<AssignmentInstruction> result = new ArrayList<AssignmentInstruction>(
                    instructionsOfMethod.size());
            final ListIterator<AbstractInsnNode> iterator = instructionsOfMethod.iterator();
            LabelNode labelNode = null;
            while (iterator.hasNext()) {
                final AbstractInsnNode abstractInstruction = iterator.next();
                if (isLabelNode(abstractInstruction)) {
                    labelNode = (LabelNode) abstractInstruction;
                } else if (isPutfieldOpcodeForInstanceVariable(abstractInstruction)) {
                    result.add(AssignmentInstruction.getInstance(labelNode, (FieldInsnNode) abstractInstruction));
                }
            }
            return result;
        }

        private boolean isLabelNode(final AbstractInsnNode abstractInstructionNode) {
            return AbstractInsnNode.LABEL == abstractInstructionNode.getType();
        }

        private boolean isPutfieldOpcodeForInstanceVariable(final AbstractInsnNode abstractInstruction) {
            return isFieldInstructionNode(abstractInstruction) && isPutfieldOpcode(abstractInstruction);
        }

        private boolean isFieldInstructionNode(final AbstractInsnNode abstractInstructionNode) {
            return AbstractInsnNode.FIELD_INSN == abstractInstructionNode.getType();
        }

        private boolean isPutfieldOpcode(final AbstractInsnNode abstractInstructionNode) {
            return Opcodes.PUTFIELD == abstractInstructionNode.getOpcode();
        }

        private void printAllInstanceVariableSetters() {
            instanceVariableSetters.removeUnassociatedVariables();
            for (final Map.Entry<FieldNode, List<MethodNode>> entry : instanceVariableSetters) {
                final FieldNode instanceVariable = entry.getKey();
                final List<MethodNode> setterMethods = entry.getValue();
                System.out.println(String.format("Instance variable: '%s'", instanceVariable.name));
                for (final MethodNode setterMethod : setterMethods) {
                    System.out.println(String.format("  Setter: '%s'.", setterMethod.name));
                }
            }
        }

        private void examineLazyVariablesAndLazyMethods() {
            for (final Map.Entry<FieldNode, List<MethodNode>> entry : instanceVariableSetters) {
                final String variableName = entry.getKey().name;
                final List<MethodNode> setterMethods = entry.getValue();
                assertAllAreNotPrivate(variableName, setterMethods);
                assertOnlyOneSetterMethodForVariable(variableName, setterMethods);
                
                for (final MethodNode publicSetterMethod : setterMethods) {
                    final List<JumpInsnNode> jumpInstructions = getJumpInstructions(publicSetterMethod.instructions);
                    System.out.println(jumpInstructions);
                }
            }
        }

        private List<JumpInsnNode> getJumpInstructions(final InsnList instructions) {
            final List<JumpInsnNode> result = new ArrayList<JumpInsnNode>();
            final ListIterator<AbstractInsnNode> iterator = instructions.iterator();
            while (iterator.hasNext()) {
                final AbstractInsnNode abstractInstruction = iterator.next();
                if (isJumpInstruction(abstractInstruction)) {
                    result.add((JumpInsnNode) abstractInstruction);
                }
            }
            return result;
        }

        private boolean isJumpInstruction(final AbstractInsnNode abstractInstruction) {
            return AbstractInsnNode.JUMP_INSN == abstractInstruction.getType();
        }

        private void assertAllAreNotPrivate(final String variableName, final List<MethodNode> setterMethods) {
            for (final MethodNode setterMethod : setterMethods) {
                 if (isNotPrivate(setterMethod)) {
                     setIsImmutableResult(variableName, setterMethod.name);
                 }
            }
        }

        private boolean isNotPrivate(final MethodNode methodNode) {
            return method(methodNode.access).isNotPrivate();
        }

        private void setIsImmutableResult(final String variableName, final String methodName) {
            final String message = format("Field [%s] can be reassigned within method [%s]", variableName, methodName);
            setResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
        }

        private void assertOnlyOneSetterMethodForVariable(final String variableName, final List<MethodNode> setterMethods) {
            if (1 < setterMethods.size()) {
                final String message = format("Field [%s] can be reassigned by more than one method.", variableName);
                setResult(message, fromInternalName(owner), MutabilityReason.FIELD_CAN_BE_REASSIGNED);
            }
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<FieldNode> potentialLazyInstanceVariables;
    private final ClassNode classNode;

    public LazyInitializationChecker() {
        classNode = new ClassNode();
        potentialLazyInstanceVariables = new ArrayList<FieldNode>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classNode.visit(version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitSource(String source, String debug) {
        classNode.visitSource(source, debug);
        super.visitSource(source, debug);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        classNode.visitOuterClass(owner, name, desc);
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        classNode.visitInnerClass(name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        final FieldVisitor result;
        if (isPrivateAndNonFinalInstanceVariable(access)) {
            logger.debug("Found potential lazy instance variable: '{}'.", name);
            result = new FieldNode(access, name, desc, signature, value);
            potentialLazyInstanceVariables.add((FieldNode) result);
        } else {
            result = classNode.visitField(access, name, desc, signature, value);
        }
        return result;
    }

    private static boolean isPrivateAndNonFinalInstanceVariable(final int access) {
        return field(access).isNotStatic() && field(access).isPrivate() && field(access).isNotFinal(); 
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (isNotConstructor(name)) {
            logger.debug("Create new 'PublicMethodVisitor' instance.");
            return new PublicMethodVisitor(access, name, desc, signature, exceptions, classNode.name,
                    potentialLazyInstanceVariables);
        }
        logger.debug("Delegating to classNode; access: {}, name: {}, desc: {}, signature: {}, exceptions: {}", access,
                name, desc, signature, exceptions);
        return classNode.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        classNode.visitEnd();
        final InstanceVerifier verifier = new InstanceVerifier();
        verifier.run();
        super.visitEnd();
    }

    private static boolean isNotConstructor(final String name) {
        return !MethodIs.aConstructor(name);
    }

}
