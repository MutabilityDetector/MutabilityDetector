package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 17.02.2013
 */
public final class EffectiveConditionCheckFinderTest {

    private static final class Asserter {

        private Asserter() {
            super();
        }

        public static void assertEffectiveJumpInsnIsFound(final Class<?> klasse,
                final String variableName, final String setterName, final int indexOfExpectedJumpInsn) {
            final EffectiveConditionCheckFinder ejif = createEffectiveJumpInsnFinder(klasse, variableName, setterName);
            assertThat(ejif, is(not(equalTo(null))));
            assertThat(ejif.hasMoreThanOneAssociatedJumpInstruction(), is(false));
            assertThat(ejif.getEffectiveJumpInsn().getIndexWithinBlock(), is(indexOfExpectedJumpInsn));
        }

        private static EffectiveConditionCheckFinder createEffectiveJumpInsnFinder(final Class<?> klasse,
                final String variableName, final String setterName) {
            EffectiveConditionCheckFinder result = null;
            final ConvenienceClassNode classNode = createConvenienceClassNodeFor(klasse);
            final List<MethodNode> setters = classNode.findMethodByName(setterName);
            final MethodNode setter = setters.get(0);
            final InsnList setterInstructions = setter.instructions;
            if (isNotNull(setter)) {
                final AssignmentInsn effectiveAssignmentInsn = findEffectivePutfieldInsnFor(classNode, variableName,
                        setterInstructions);
                if (!effectiveAssignmentInsn.isNull()) {
                    result = EffectiveConditionCheckFinder.newInstance(effectiveAssignmentInsn, setterInstructions);
                }
            }
            return result;
        }

        private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.getConvenienceClassNodeFor(klasse);
        }

        private static boolean isNotNull(final Object ref) {
            return null != ref;
        }

        private static AssignmentInsn findEffectivePutfieldInsnFor(final ConvenienceClassNode classNode,
                final String variableName,
                final InsnList setterInstructions) {
            AssignmentInsn result = NullAssignmentInsn.getInstance();

            final FieldNode variable = classNode.findVariableWithName(variableName);
            if (isNotNull(variable)) {
                final EffectiveAssignmentInsnFinder epif = EffectiveAssignmentInsnFinder.newInstance(variable,
                        setterInstructions);
                result = epif.getEffectiveAssignmentInstruction();
            }
            return result;
        }

    } // Asserter


    @Test
    @Ignore("MethodNotFoundException caused by Mutability Detector occurs.")
    public void assertImmutability() {
        assertInstancesOf(EffectiveConditionCheckFinder.class, areImmutable());
    }

    @Test
    public void findInJavaLangString() {
        Asserter.assertEffectiveJumpInsnIsFound(String.class, "hash", "hashCode", 20);
    }

    @Test
//    @Ignore("Implementation of 'EffectiveJumpInsnFinder is incomplete.'")
    public void findInValidIntegerWithJvmInitialValueDcli() {
        Asserter.assertEffectiveJumpInsnIsFound(WithoutAlias.WithJvmInitialValue.IntegerValid.class, "hash",
                "hashCode", 4);
    }

}
