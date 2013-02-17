/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import de.htwg_konstanz.jia.lazyinitialisation.InitialValueFinder.BaseInitialValue;
import de.htwg_konstanz.jia.lazyinitialisation.InitialValueFinder.InitialValue;
import de.htwg_konstanz.jia.lazyinitialisation.VariableSetterCollection.Setters;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.FloatWithMultiple;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithSemantic;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.StringWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.StringWithSemantic;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
public final class InitialValueFinderTest {

    @ThreadSafe
    private static final class InitialValuesFactory {

        private static final InitialValuesFactory INSTANCE = new InitialValuesFactory();

        private InitialValuesFactory() {
            super();
        }

        public static InitialValuesFactory getInstance() {
            return INSTANCE;
        }

        public Set<InitialValue> getPossibleInitialValuesFor(final Class<?> targetClass, final String variableName) {
            final ClassNode classNode = createAppropriateClassNode(targetClass);
            final VariableSetterCollection varSetters = createVariableSetterCollection(classNode);
            for (final Entry<FieldNode, Setters> entry : varSetters) {
                final FieldNode variable = entry.getKey();
                if (variable.name.equals(variableName)) {
                    final Setters setters = entry.getValue();
                    final InitialValueFinder initialValueFinder = InitialValueFinder.newInstance(variable, setters);
                    initialValueFinder.run();
                    return initialValueFinder.getPossibleInitialValues();
                }
            }
            return Collections.emptySet();
        }

        private static ClassNode createAppropriateClassNode(final Class<?> targetClass) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.classNodeFor(targetClass);
        }

        private static VariableSetterCollection createVariableSetterCollection(final ClassNode classNode) {
            final VariableSetterCollection result = collectPrivateNonFinalInstanceVariables(classNode.fields);
            collectSetters(result, classNode.methods);
            result.removeUnassociatedVariables();
            return result;
        }

        private static VariableSetterCollection
                collectPrivateNonFinalInstanceVariables(final List<FieldNode> variables) {
            final VariableSetterCollection result = VariableSetterCollection.newInstance();
            for (final FieldNode variable : variables) {
                if (isPrivateAndNonFinalInstanceVariable(variable.access)) {
                    result.addVariable(variable);
                }
            }
            return result;
        }

        private static boolean isPrivateAndNonFinalInstanceVariable(final int access) {
            return field(access).isNotStatic() && field(access).isPrivate() && field(access).isNotFinal(); 
        }

        private static void collectSetters(final VariableSetterCollection instanceVariableSetters,
                final List<MethodNode> methods) {
            for (final MethodNode methodNode : methods) {
                for (final AssignmentInsn putfieldInstruction : getPutfieldInstructions(methodNode.instructions)) {
                    final String nameOfInstanceVariable = putfieldInstruction.getNameOfAssignedVariable();
                    instanceVariableSetters.addSetterForVariable(nameOfInstanceVariable, methodNode);
                    break;
                }
            }
        }

        private static List<AssignmentInsn> getPutfieldInstructions(final InsnList instructionsOfMethod) {
            final List<AssignmentInsn> result = new ArrayList<AssignmentInsn>(
                    instructionsOfMethod.size());
            final AbstractInsnNode[] instructions = instructionsOfMethod.toArray();
            for (int i = 0; i < instructions.length; i++) {
                final AbstractInsnNode abstractInsn = instructions[i];
                if (isPutfieldOpcode(abstractInsn)) {
                    result.add(DefaultAssignmentInsn.getInstance((FieldInsnNode) abstractInsn, i));
                }
            }
            return result;
        }

        private static boolean isPutfieldOpcode(final AbstractInsnNode abstractInstructionNode) {
            return Opcodes.PUTFIELD == abstractInstructionNode.getOpcode();
        }

    } // class InitialValuesFactory


    @Test
    public void testForFloatWithMultipleScli() {
        final Set<InitialValue> expected = createExpected(Float.valueOf(-1.0F), Float.valueOf(23.0F));
        final Set<InitialValue> actual = getPossibleInitialValuesFor(FloatWithMultiple.class, "hash");
        assertThat(actual, is(expected));
    }

    private static Set<InitialValue> getPossibleInitialValuesFor(final Class<?> klasse, final String variableName) {
        final InitialValuesFactory factory = InitialValuesFactory.getInstance();
        return factory.getPossibleInitialValuesFor(klasse, variableName);
    }

    private static Set<InitialValue> createExpected(final Object first, final Object ... further)  {
        final Set<InitialValue> result = new HashSet<InitialValue>();
        result.add(BaseInitialValue.getInstance(first));
        for (final Object next : further) {
            result.add(BaseInitialValue.getInstance(next));
        }
        return result;
    }

    @Test
    public void testForJavaLangString() {
        final Set<InitialValue> expected = createExpected(Integer.valueOf(0));
        final Set<InitialValue> actual = getPossibleInitialValuesFor(String.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void testForIntegerWithSemanticScli() {
        final Set<InitialValue> expected = createExpected(Integer.valueOf(-1));
        final Set<InitialValue> actual = getPossibleInitialValuesFor(IntegerWithSemantic.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void testForStringWithDefaultScli() {
        final Set<InitialValue> expected = createExpected(null);
        final Set<InitialValue> actual = getPossibleInitialValuesFor(StringWithDefault.class, "hash");
        assertThat(actual, is(expected));
    }

    @Test
    public void testForStringWithSemanticScli() {
        final Set<InitialValue> expected = createExpected("");
        final Set<InitialValue> actual = getPossibleInitialValuesFor(StringWithSemantic.class, "hash");
        assertThat(actual, is(expected));
    }

}
