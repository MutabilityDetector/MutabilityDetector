package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.doublecheck.AliasedIntegerWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.AliasedFloatWithDefault;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.IntegerWithDefault;

/**
 * 
 *
 * @author Juergen Fickel
 * @version 17.02.2013
 */
public final class EffectiveJumpInsnFinderTest {

    private static final class Asserter {

        private Asserter() {
            super();
        }

        public static void assertEffectiveJumpInsnIsFound(final Class<?> klasse,
                final String variableName, final String setterName, final int indexOfExpectedJumpInsn) {
            final EffectiveJumpInsnFinder ejif = createEffectiveJumpInsnFinder(klasse, variableName, setterName);
            assertThat(ejif, is(not(equalTo(null))));
            assertThat(ejif.hasMoreThanOneAssociatedJumpInstruction(), is(false));
            assertThat(ejif.getEffectiveJumpInsn().getIndexOfJumpInsn(), is(indexOfExpectedJumpInsn));
        }

        private static EffectiveJumpInsnFinder createEffectiveJumpInsnFinder(final Class<?> klasse,
                final String variableName, final String setterName) {
            EffectiveJumpInsnFinder result = null;
            final ConvenienceClassNode classNode = createConvenienceClassNodeFor(klasse);
            final MethodNode setter = classNode.findMethodWithName(setterName);
            
            final ControlFlowBlockFactory cfbFactory = ControlFlowBlockFactory.newInstance(classNode.name(), setter);
            final Set<ControlFlowBlock> allControlFlowBlocksForMethod = cfbFactory.getAllControlFlowBlocksForMethod();
            
            
            final InsnList setterInstructions = setter.instructions;
            if (isNotNull(setter)) {
                final AssignmentInsn effectiveAssignmentInsn = findEffectivePutfieldInsnFor(classNode, variableName,
                        setterInstructions);
                if (!effectiveAssignmentInsn.isNull()) {
                    result = EffectiveJumpInsnFinder.newInstance(effectiveAssignmentInsn, setterInstructions);
                }
            }
            return result;
        }

        private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.convenienceClassNodeFor(klasse);
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
                final EffectivePutfieldInsnFinder epif = EffectivePutfieldInsnFinder.newInstance(variable,
                        setterInstructions);
                result = epif.getEffectivePutfieldInstruction();
            }
            return result;
        }

    }

    @Test
    @Ignore("MethodNotFoundException caused by Mutability Detector occurs.")
    public void assertImmutability() {
        assertInstancesOf(EffectiveJumpInsnFinder.class, areImmutable());
    }

    @Test
    public void findInJavaLangString() {
        Asserter.assertEffectiveJumpInsnIsFound(String.class, "hash", "hashCode", 20);
    }

    @Test
//    @Ignore("Implementation of 'EffectiveJumpInsnFinder is incomplete.'")
    public void findInAliasedIntegerWithDefaultDcli() {
        Asserter.assertEffectiveJumpInsnIsFound(IntegerWithDefault.class, "hash", "hashCode", 4);
    }

}
