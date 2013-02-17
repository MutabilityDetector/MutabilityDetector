package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.lang.Object;
import java.lang.String;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 17.02.2013
 */
final class ConvenienceClassNode {

    private final ClassNode classNode;

    private ConvenienceClassNode(final ClassNode theClassNode) {
        classNode = flatCopy(theClassNode);
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

    public List<FieldNode> fields() {
        return classNode.fields;
    }

    public List<MethodNode> methods() {
        return classNode.methods;
    }

    public FieldNode findVariableWithName(final String variableName) {
        notEmpty(variableName);
        for (final FieldNode variable : fields()) {
            if (variableName.equals(variable.name)) {
                return deepCopy(variable);
            }
        }
        return null;
    }

    private FieldNode deepCopy(final FieldNode source) {
        final int access = source.access;
        final String name = source.name;
        final String desc = source.desc;
        final String signature = source.desc;
        final Object value = source.value;
        return new FieldNode(access, name, desc, signature, value);
    }

    public MethodNode findMethodWithName(final String methodName) {
        notEmpty(methodName);
        for (final MethodNode method : methods()) {
            if (methodName.equals(method.name)) {
                return method;
            }
        }
        return null;
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

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("classNode", classNode);
        return builder.toString();
    }

}
