package org.objectweb.asm.apilearning;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mutabilitydetector.locations.ClassName;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import de.htwg_konstanz.jia.lazyinitialisation.singlecheck.WithoutAlias;

/**
 * Example taken from ASM 4 Guide.
 *
 * @author Juergen Fickel
 * @version 08.02.2013
 */
public final class CustomControlFlowAnalysis {

    private static final class Node<V extends Value> extends Frame<V> {
        private final Set<Node<V>> successors = new HashSet<Node<V>>();

        public Node(final int nLocals, final int nStack) {
            super(nLocals, nStack);
        }

        public Node(Frame<? extends V> src) {
            super(src);
        }

        public void addSuccessor(final Node<V> successor) {
            successors.add(successor);
        }

        public Set<Node<V>> getSuccessors() {
            return Collections.unmodifiableSet(successors);
        }
    } // class Node


    private static final class CyclomaticComplexity {
        private final Analyzer<BasicValue> analyser = new Analyzer<BasicValue>(new BasicInterpreter()) {
            @Override
            protected Frame<BasicValue> newFrame(final int nLocals, final int nStack) {
                return new Node<BasicValue>(nLocals, nStack);
            }

            @Override
            protected Frame<BasicValue> newFrame(final Frame<? extends BasicValue> src) {
                return new Node<BasicValue>(src);
            }

            @Override
            protected void newControlFlowEdge(final int src, final int dst) {
                final Node<BasicValue> s = (Node<BasicValue>) getFrames()[src];
                s.addSuccessor((Node<BasicValue>) getFrames()[dst]);
            }
        };

        public int getCyclomaticComplexity(final String owner, final MethodNode methodNode) throws AnalyzerException {
            analyser.analyze(owner, methodNode);
            return calculateCyclomaticComplexity(analyser.getFrames());
        }

        private static int calculateCyclomaticComplexity(final Frame<BasicValue>[] frames) {
            int edges = 0;
            int nodes = 0;
            for (final Frame<BasicValue> frame : frames) {
                if (null != frame) {
                    edges += ((Node<BasicValue>) frame).getSuccessors().size();
                    nodes ++;
                }
            }
            return edges - nodes + 2;
        }
    } // class CyclomaticComplexity


    @Test
    public void getCyclomaticComplexity() throws IOException, AnalyzerException {
        final ClassName dotted = Dotted.fromClass(WithoutAlias.WithJvmInitialValue.IntegerValid.class);
        final ClassReader cr = new ClassReader(dotted.asString());
        final ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        final MethodNode hashCodeMethodNode = findMethodWithName(cn, "hashCode");
        final CyclomaticComplexity cc = new CyclomaticComplexity();
        final int cyclomaticComplexity = cc.getCyclomaticComplexity(cn.name, hashCodeMethodNode);
        assertThat(cyclomaticComplexity, is(2));
    }

    private static MethodNode findMethodWithName(final ClassNode cn, final String methodName) {
        for (final MethodNode methodNode : cn.methods) {
            if (methodName.equals(methodNode.name)) {
                return methodNode;
            }
        }
        return null;
    }

}
