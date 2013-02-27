package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
public final class AliasFinderTest {

    public static final class Asserter {

        private final ConvenienceClassNode classNode;
        private int blockNumber = -1;
        private ControlFlowBlock controlFlowBlockToCheck = null;
        private Alias alias = Alias.newInstance(false, Integer.MIN_VALUE);
        private int indexOfLocalVariable = -1;
        
        public Asserter(final Class<?> dieKlasse) {
            final ClassNodeFactory classNodeFactory = ClassNodeFactory.getInstance();
            classNode = classNodeFactory.convenienceClassNodeFor(dieKlasse);
        }

        public void assertThat(final Asserter theAsserter) {
            MatcherAssert.assertThat(alias.doesExist, is(true));
            MatcherAssert.assertThat(alias.localVariable, is(indexOfLocalVariable));
        }

        public Asserter block(final int theBlockNumber) {
            blockNumber = theBlockNumber;
            return this;
        }

        public Asserter ofMethod(final String theMethodName) {
            final String owner = classNode.name();
            final MethodNode setter = classNode.findMethodWithName(theMethodName);
            final ControlFlowBlockFactory controlFlowBlockFactory = ControlFlowBlockFactory.newInstance(owner, setter);
            final Map<Integer, ControlFlowBlock> allControlFlowBlocksForMethod = controlFlowBlockFactory
                        .getAllControlFlowBlocksForMethodInMap();
            controlFlowBlockToCheck = allControlFlowBlocksForMethod.get(blockNumber);
            return this;
        }

        public Asserter storesAliasForVariable(final String theVariableName) {
            final AliasFinder aliasFinder = AliasFinder.newInstance(theVariableName);
            alias = aliasFinder.searchForAliasInBlock(controlFlowBlockToCheck);
            return this;
        }

        public Asserter inLocalVariable(final int theIndexOfLocalVariable) {
            indexOfLocalVariable = theIndexOfLocalVariable;
            return this;
        }

    } // class Asserter


    @Test
    public void lazyMethodOfValidIntegerWithCustomInitialValueUsesAlias() {
        final Asserter a = new Asserter(WithAlias.WithCustomInitialValue.IntegerValid.class);
        a.assertThat(a.block(0).ofMethod("getMessageLength").storesAliasForVariable("cachedValue").inLocalVariable(1));
    }

    @Test
    public void lazyMethodOfValidFloatWithJvmInitialValueUsesAlias() {
        final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.FloatValid.class);
        a.assertThat(a.block(0).ofMethod("hashCodeFloat").storesAliasForVariable("hash").inLocalVariable(1));
    }

}
