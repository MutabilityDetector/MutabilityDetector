package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;

/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 17.02.2013
 */
@NotThreadSafe
final class ConvenienceClassNode {

    private final ClassNode classNode;
    private VariableInitialisersAssociation variableInitialisersAssociation;

    private ConvenienceClassNode(final ClassNode theClassNode) {
        classNode = flatCopy(theClassNode);
        variableInitialisersAssociation = null;
    }

    private static ClassNode flatCopy(final ClassNode source) {
        final ClassNode result = new ClassNode();
        result.access = source.access;
        result.name = source.name;
        result.signature = source.signature;
        result.superName = source.superName;
        result.interfaces = unmodifiableOrEmpty(source.interfaces);
        result.sourceFile = source.sourceFile;
        result.sourceDebug = source.sourceDebug;
        result.outerClass = source.outerClass;
        result.outerMethod = source.outerMethod;
        result.outerMethodDesc = source.outerMethodDesc;
        result.visibleAnnotations = unmodifiableOrEmpty(source.visibleAnnotations);
        result.invisibleAnnotations = unmodifiableOrEmpty(source.invisibleAnnotations);
        result.attrs = unmodifiableOrEmpty(source.attrs);
        result.innerClasses = unmodifiableOrEmpty(source.innerClasses);
        result.fields = unmodifiableOrEmpty(source.fields);
        result.methods = unmodifiableOrEmpty(source.methods);
        return result;
    }

    private static <T> List<T> unmodifiableOrEmpty(final List<T> source) {
        List<T> result = Collections.emptyList();
        if (null != source) {
            result = Collections.unmodifiableList(source);
        }
        return result;
    }

    public static ConvenienceClassNode newInstance(final ClassNode classNode) {
        return new ConvenienceClassNode(notNull(classNode));
    }

    public String getName() {
        return classNode.name;
    }

    public List<FieldNode> getFields() {
        return classNode.fields;
    }

    public List<MethodNode> getMethods() {
        return classNode.methods;
    }

    public FieldNode findVariableWithName(final String variableName) {
        notEmpty(variableName);
        for (final FieldNode variable : getFields()) {
            if (variableName.equals(variable.name)) {
                return variable;
            }
        }
        return null;
    }

    public List<MethodNode> findMethodByName(final String methodName) {
        notEmpty(methodName);
        final ArrayList<MethodNode> result = new ArrayList<MethodNode>();
        for (final MethodNode method : getMethods()) {
            if (methodName.equals(method.name)) {
                result.add(method);
            }
        }
        result.trimToSize();
        return result;
    }

    public MethodNode findMethodByDescriptor(final String methodName, final Type returnType,
            final Type... argumentTypes) {
        notEmpty(methodName, "Parameter 'methodName' must neither be null nor empty!");
        notNull(returnType, "Parameter 'returnType' must not be null!");
        final String desc = createDescriptorFor(methodName, returnType, argumentTypes);
        for (final MethodNode method : getMethods()) {
            if (method.name.equals(methodName) && method.desc.equals(desc)) {
                return method;
            }
        }
        return null;
    }

    private static String createDescriptorFor(final String methodName, final Type returnType,
            final Type[] argumentTypes) {
        final Method method = new Method(methodName, returnType, argumentTypes);
        return method.getDescriptor();
    }

    @SuppressWarnings("unused")
    private MethodNode deepCopy(final MethodNode source) {
        final int access = source.access;
        final String name = source.name;
        final String desc = source.desc;
        final String signature = source.signature;
        final String[] exceptions = source.exceptions.toArray(new String[source.exceptions.size()]);
        
        final MethodNode result = new MethodNode(access, name, desc, signature, exceptions);
        result.instructions = source.instructions;
        return result;
    }

    public synchronized VariableInitialisersAssociation getVariableInitialisersAssociation() {
        VariableInitialisersAssociation result = variableInitialisersAssociation;
        if (null == result) {
            final CandidatesFinder cf = CandidatesFinder.newInstance(getFields());
            final VariableInitialisersAssociation candidates = cf.find();
            final InitialisingMethodsFinder imf = InitialisingMethodsFinder.newInstance(getMethods(), candidates);
            result = imf.find();
            variableInitialisersAssociation = result;
        }
        return result;
    }

    /**
     * Delivers all {@link ControlFlowBlock}s of the given {@link MethodNode}.
     * 
     * @param method
     *            the method. Must not be {@code null}.
     * @return a {@link List} containing all {@link ControlFlowBlock}s of
     *         {@code method}. Is never {@code null}.
     * @see #findMethodByDescriptor(String, Type, Type...)
     * @see #findMethodByName(String)
     * @see #getControlFlowBlocksForMethod(String, Type, Type...)
     */
    public List<ControlFlowBlock> getControlFlowBlocksForMethod(final MethodNode method) {
        List<ControlFlowBlock> result = Collections.emptyList();
        if (isNotNull(method)) {
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(classNode.name, method);
            result = cfbFactory.getAllControlFlowBlocksForMethod();
        }
        return result;
    }

    private static boolean isNotNull(final Object ref) {
        return null != ref;
    }

    /**
     * Delivers all {@link ControlFlowBlock}s of a method which is described by
     * its name, its return type as well as its argumentTypes.
     * 
     * @param methodName
     *            name of the method. Must neither be {@code null} nor empty.
     * @param returnType
     *            the {@link Type} of the return value of the described method.
     *            Must not be {@code null}.
     * @param argumentTypes
     *            (optional) the types of the method's arguments.
     * @return a {@link List} containing all {@link ControlFlowBlock}s of the
     *         method which is described by {@code methodName},
     *         {@code returnType} and {@code argumentTypes}. Is never
     *         {@code null}.
     * @see #getControlFlowBlocksForMethod(MethodNode)
     */
    public List<ControlFlowBlock> getControlFlowBlocksForMethod(final String methodName, final Type returnType,
            final Type... argumentTypes) {
        final MethodNode method = findMethodByDescriptor(methodName, returnType, argumentTypes);
        return getControlFlowBlocksForMethod(method);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ConvenienceClassNode [").append("classNode=").append(classNode).append("]");
        return builder.toString();
    }

}
