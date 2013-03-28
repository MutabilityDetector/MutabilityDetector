package org.mutabilitydetector.checkers.settermethod;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.mutabilitydetector.benchmarks.settermethod.singlecheck.WithoutAlias;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 28.02.2013
 */
public final class ConvenienceClassNodeTest {

    @Test
    public void findConstructorWithoutArgumentsByDescriptor() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final EnhancedClassNode ccn = createAppropriateClassNode(klasse);
        final MethodNode actual = ccn.findMethodByDescriptor("<init>", Type.VOID_TYPE);
        assertNotNull(actual);
    }

    private static EnhancedClassNode createAppropriateClassNode(final Class<?> targetClass) {
        final ClassNodeFactory factory = ClassNodeFactory.getInstance();
        return factory.getConvenienceClassNodeFor(targetClass);
    }

    @Test
    public void findConstructorWithSingleArgumentByDescriptor() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final EnhancedClassNode ccn = createAppropriateClassNode(klasse);
        final MethodNode actual = ccn.findMethodByDescriptor("<init>", Type.VOID_TYPE, Type.FLOAT_TYPE);
        assertNotNull(actual);
    }

    @Test
    public void noMethodForFantasyValues() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final EnhancedClassNode ccn = createAppropriateClassNode(klasse);
        final MethodNode actual = ccn.findMethodByDescriptor("foo", Type.BOOLEAN_TYPE);
        assertNull(actual);
    }

    @Test
    public void getAllControlFlowBlocksForParticularMethod() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final EnhancedClassNode ccn = createAppropriateClassNode(klasse);
        final List<ControlFlowBlock> cfbs = ccn.getControlFlowBlocksForMethod("hashCodeFloat", Type.FLOAT_TYPE);
        assertThat(cfbs.size(), is(3));
    }

    @Test
    public void noControlFlowBlocksForFantasyMethod() {
        final Class<?> klasse = WithoutAlias.WithCustomInitialValue.FloatInvalidWithMultipleInitialValues.class;
        final EnhancedClassNode ccn = createAppropriateClassNode(klasse);
        final List<ControlFlowBlock> cfbs = ccn.getControlFlowBlocksForMethod("foo", Type.BOOLEAN_TYPE);
        assertTrue(cfbs.isEmpty());
    }

}
