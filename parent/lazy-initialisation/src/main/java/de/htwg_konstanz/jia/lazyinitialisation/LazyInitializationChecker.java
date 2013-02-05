package de.htwg_konstanz.jia.lazyinitialisation;

import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import java.util.*;
import java.util.Map.Entry;

import org.mutabilitydetector.checkers.AbstractMutabilityChecker;
import org.mutabilitydetector.checkers.MethodIs;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 04.02.2013
 */
public final class LazyInitializationChecker extends AbstractMutabilityChecker {

    private static final class InstanceVariableSetterMethodCollection implements
            Iterable<Map.Entry<FieldNode, List<MethodNode>>> {
        private final Map<FieldNode, List<MethodNode>> instanceVariableSetterMethods;
        
        public InstanceVariableSetterMethodCollection() {
            instanceVariableSetterMethods = new HashMap<FieldNode, List<MethodNode>>();
        }

        public boolean addInstanceVariable(final FieldNode instanceVariableNode) {
            final boolean result = !instanceVariableSetterMethods.containsKey(instanceVariableSetterMethods);
            instanceVariableSetterMethods.put(instanceVariableNode, new ArrayList<MethodNode>(2));
            return result;
        }

        public boolean addSetterMethodForInstanceVariable(final String instanceVariableName,
                final MethodNode setterMethodNode) {
            boolean result = false;
            final FieldNode instanceVariableNode = getInstanceVariableNodeForName(instanceVariableName);
            if (null != instanceVariableNode) {
                result = addSetterMethodForInstanceVariable(instanceVariableNode, setterMethodNode);
            }
            return result;
        }

        private FieldNode getInstanceVariableNodeForName(final String variableName) {
            for (final Map.Entry<FieldNode, List<MethodNode>> entry : instanceVariableSetterMethods.entrySet()) {
                final FieldNode instanceVariableNode = entry.getKey();
                if (instanceVariableNode.name.equals(variableName)) {
                    return instanceVariableNode;
                }
            }
            return null;
        }

        private boolean addSetterMethodForInstanceVariable(final FieldNode instanceVariableNode,
                final MethodNode setterMethodNode) {
            final List<MethodNode> setterMethodsForInstanceVariable = instanceVariableSetterMethods
                    .get(instanceVariableNode);
            return setterMethodsForInstanceVariable.add(setterMethodNode);
        }

        public List<MethodNode> getSetterMethodsFor(final String instanceVariableName) {
            List<MethodNode> result = Collections.emptyList();
            final FieldNode instanceVariableNode = getInstanceVariableNodeForName(instanceVariableName);
            if (null != instanceVariableNode) {
                final List<MethodNode> setterMethodsForInstanceVariable = instanceVariableSetterMethods
                        .get(instanceVariableNode);
                result = new ArrayList<MethodNode>(setterMethodsForInstanceVariable);
            }
            return result;
        }

        @Override
        public Iterator<Entry<FieldNode, List<MethodNode>>> iterator() {
            final Set<Entry<FieldNode, List<MethodNode>>> entrySet = new HashSet<Entry<FieldNode, List<MethodNode>>>(
                    instanceVariableSetterMethods.entrySet());
            return entrySet.iterator();
        }

    }

    private final class InstanceVerifier implements Runnable {

        private final InstanceVariableSetterMethodCollection instanceVariableSetters;
        private final List<MethodNode> setterMethods = new ArrayList<MethodNode>();
        private final String owner = classNode.name;

        public InstanceVerifier() {
            instanceVariableSetters = new InstanceVariableSetterMethodCollection();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            collectPrivateNonFinalInstanceVariables();
            collectSetterMethods();
            printAllInstanceVariableSetters();
        }

        private void printAllInstanceVariableSetters() {
            for (final Map.Entry<FieldNode, List<MethodNode>> entry : instanceVariableSetters) {
                final FieldNode instanceVariable = entry.getKey();
                final List<MethodNode> setterMethods = entry.getValue();
                System.out.println(String.format("Instance variable: '%s'", instanceVariable.name));
                for (final MethodNode setterMethod : setterMethods) {
                    System.out.println(String.format("  Setter: '%s'.", setterMethod.name));
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void collectPrivateNonFinalInstanceVariables() {
            for (final FieldNode fieldNode : (List<FieldNode>) classNode.fields) {
                if (isPrivateAndNonFinalInstanceVariable(fieldNode.access)) {
                    instanceVariableSetters.addInstanceVariable(fieldNode);
                }
            }
        }

        private boolean isPrivateAndNonFinalInstanceVariable(final int access) {
            return field(access).isNotStatic() && field(access).isPrivate() && field(access).isNotFinal(); 
        }

        @SuppressWarnings("unchecked")
        private void collectSetterMethods() {
            for (final MethodNode methodNode : (List<MethodNode>) classNode.methods) {
                if (isNotConstructor(methodNode.name)) {
                    for (final FieldInsnNode putfieldInstruction : getPutfieldInstructions(methodNode.instructions)) {
                        final String nameOfInstanceVariable = putfieldInstruction.name;
                        instanceVariableSetters.addSetterMethodForInstanceVariable(nameOfInstanceVariable, methodNode);
                    }
                }
            }
        }

        private List<FieldInsnNode> getPutfieldInstructions(final InsnList instructionsOfMethod) {
            final List<FieldInsnNode> result = new ArrayList<FieldInsnNode>(instructionsOfMethod.size());
            @SuppressWarnings("unchecked")
            final ListIterator<AbstractInsnNode> iterator = instructionsOfMethod.iterator();
            while (iterator.hasNext()) {
                final AbstractInsnNode instructionNode = iterator.next();
                if (isPutfieldOpcodeForInstanceVariable(instructionNode)) {
                    result.add((FieldInsnNode) instructionNode);
                }
            }
            return result;
        }

        private boolean isPutfieldOpcodeForInstanceVariable(final AbstractInsnNode abstractInstructionNode) {
            return isFieldInstructionNode(abstractInstructionNode) && isPutfieldOpcode(abstractInstructionNode);
        }

        private boolean isFieldInstructionNode(final AbstractInsnNode abstractInstructionNode) {
            return AbstractInsnNode.FIELD_INSN == abstractInstructionNode.getType();
        }

        private boolean isPutfieldOpcode(final AbstractInsnNode abstractInstructionNode) {
            final int opcodeInt = abstractInstructionNode.getOpcode();
            final Opcode opcode = Opcode.forInt(opcodeInt);
            return Opcode.PUTFIELD == opcode;
        }

    }

    private final ClassNode classNode;

    public LazyInitializationChecker() {
        classNode = new ClassNode();
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
        return classNode.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
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
