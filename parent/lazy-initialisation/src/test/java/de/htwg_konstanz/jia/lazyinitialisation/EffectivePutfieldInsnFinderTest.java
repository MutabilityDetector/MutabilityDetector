package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * 
 *
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 17.02.2013
 */
public final class EffectivePutfieldInsnFinderTest {

    private static final class Asserter {

        private Asserter() {
            super();
        }

        public static void assertEffectivePutfieldInsnIsFound(final Class<?> klasse,
                final String variableName,
                final String setterName,
                final int indexOfExpectedPutfieldInsn) {
            final AssignmentInsn actual = findEffectivePutfieldInsnFor(klasse, variableName, setterName);
            assertIndexIsEqual(indexOfExpectedPutfieldInsn, actual);
        }

        private static AssignmentInsn findEffectivePutfieldInsnFor(final Class<?> klasse,
                final String variableName,
                final String setterName) {
            AssignmentInsn result = NullAssignmentInsn.getInstance();
            final ConvenienceClassNode classNode = createConvenienceClassNodeFor(klasse);
            final FieldNode variable = classNode.findVariableWithName(variableName);
            if (isNotNull(variable)) {
                final MethodNode setter = classNode.findMethodWithName(setterName);
                if (isNotNull(setter)) {
                    final EffectivePutfieldInsnFinder epif = EffectivePutfieldInsnFinder.newInstance(variable,
                            setter.instructions);
                    result = epif.getEffectivePutfieldInstruction();
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

        private static void assertIndexIsEqual(final int index, final AssignmentInsn possibleEffectivePutfieldInsn) {
            assertThat(possibleEffectivePutfieldInsn.getIndexOfAssignmentInstruction(), is(index));
        }

    } // class Asserter


    @Test
    public void findForJavaLangString() {
        Asserter.assertEffectivePutfieldInsnIsFound(String.class, "hash", "hashCode", 53);
    }

    @Test
    public void validIntegerWithJvmInitialValue() {
        Asserter.assertEffectivePutfieldInsnIsFound(WithoutAlias.WithJvmInitialValue.IntegerValid.class, "hash",
                "hashCode", 9);
    }

    @Test
    public void aliasedValidFloatWithJvmInitialValue() {
        Asserter.assertEffectivePutfieldInsnIsFound(WithAlias.WithJvmInitialValue.FloatValid.class, "hash",
                "hashCodeFloat", 19);
    }

}
