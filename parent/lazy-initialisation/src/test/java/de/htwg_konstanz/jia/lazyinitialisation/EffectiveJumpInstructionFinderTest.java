/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.objectweb.asm.Type;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 04.03.2013
 */
@RunWith(Enclosed.class)
public class EffectiveJumpInstructionFinderTest {

    @NotThreadSafe
    private static final class Reason {

        private final ConvenienceClassNode ccn;
        private final List<ControlFlowBlock> cfbs;
        private String variableName;
        private int index;
        private EffectiveJumpInstructionFinder finder;

        public Reason(final Class<?> klasse) {
            ccn = createConvenienceClassNodeFor(klasse);
            cfbs = new ArrayList<ControlFlowBlock>();
            variableName = "";
            index = Integer.MIN_VALUE;
            finder = null;
        }

        private static ConvenienceClassNode createConvenienceClassNodeFor(final Class<?> klasse) {
            final ClassNodeFactory factory = ClassNodeFactory.getInstance();
            return factory.convenienceClassNodeFor(klasse);
        }

        public Reason forMethod(final String methodName, final Type returnType,
                final Type... argumentTypes) {
            cfbs.addAll(ccn.getControlFlowBlocksForMethod(methodName, returnType, argumentTypes));
            return this;
        }

        public Reason andVariable(final String theVariableName) {
            variableName = notEmpty(theVariableName);

            return this;
        }

        public Reason andIndex(final int theIndex) {
            index = theIndex;
            return this;
        }

        public Reason ofBlock(final int theBlockNumber) {
            final ControlFlowBlock b = cfbs.get(theBlockNumber);
            finder = EffectiveJumpInstructionFinder.newInstance(variableName, b);
            return this;
        }

        public boolean isEffectiveJumpInstruction() {
            return finder.isEffectiveJumpInstruction(index);
        }

    } // class Reason


    public static final class InvalidSingleCheckLazyInitialisationWithoutAlias {

        @Test
        public void charWithJvmInitialValue() {
            final Reason r = new Reason(WithoutAlias.WithJvmInitialValue.CharInvalid.class)
                    .forMethod("hashCodeChar", Type.CHAR_TYPE).andVariable("hash").andIndex(4).ofBlock(0);
            assertFalse(r.isEffectiveJumpInstruction());
        }
        
    } // InvalidSingleCheckLazyInitialisationWithoutAlias

}
