package de.htwg_konstanz.jia.lazyinitialisation;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.objectweb.asm.tree.MethodNode;

import de.htwg_konstanz.jia.lazyinitialisation.ControlFlowBlock.ControlFlowBlockFactory;
import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithAlias;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
@RunWith(Enclosed.class)
public final class AliasFinderTest {

    @NotThreadSafe
    public static final class Asserter {

        private final ConvenienceClassNode classNode;
        private int blockNumber = -1;
        private ControlFlowBlock controlFlowBlockToCheck = null;
        private Alias alias = Alias.newInstance(false, Integer.MIN_VALUE);
        private int indexOfLocalVariable = -1;
        
        public Asserter(final Class<?> dieKlasse) {
            final ClassNodeFactory classNodeFactory = ClassNodeFactory.getInstance();
            classNode = classNodeFactory.getConvenienceClassNodeFor(dieKlasse);
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
            final String owner = classNode.getName();
            final List<MethodNode> setters = classNode.findMethodByName(theMethodName);
            final MethodNode setter = setters.get(0);
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


    /**
     * Test cases for classes whose lazy method harness the Single Check Idiom.
     */
    public static final class SingleCheckTests {

        @Test
        public void lazyMethodOfValidIntegerWithCustomInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithCustomInitialValue.IntegerValid.class);
            a.assertThat(a.block(0).ofMethod("getMessageLength").storesAliasForVariable("cachedValue").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidStringWithCustomInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithCustomInitialValue.StringValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeString").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidByteWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.ByteValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeByte").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidFloatWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.FloatValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeFloat").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidShortWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.ShortValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeShort").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidObjectWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.ObjectValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeObject").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void synchronizedLazyMethodOfValidObjectWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.SynchronizedObjectValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeObject").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfValidStringWithJvmInitialValueUsesAlias() {
            final Asserter a = new Asserter(WithAlias.WithJvmInitialValue.StringValid.class);
            a.assertThat(a.block(0).ofMethod("hashCodeString").storesAliasForVariable("hash").inLocalVariable(1));
        }

        @Test
        public void lazyMethodOfJavaLangStringUsesAlias() {
            final Asserter a = new Asserter(String.class);
            a.assertThat(a.block(0).ofMethod("hashCode").storesAliasForVariable("hash").inLocalVariable(1));
        }

    } // class SinglecheckTests


    /**
     * Test cases for classe which 
     */
    public static final class DoubleCheckTests {
        
    }

}
