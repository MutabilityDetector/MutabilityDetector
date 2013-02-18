/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 18.02.2013
 */
final class Node<V extends Value> extends Frame<V> {

    private final Set<Node<V>> precedessors = new HashSet<Node<V>>();
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

    public boolean alreadySeen() {
        return 1 < successors.size() || 1 < precedessors.size();
    }

    public void addPrecedessor(final Node<V> precedessor) {
        precedessors.add(precedessor);
    }

    public Set<Node<V>> getPrecedessors() {
        return Collections.unmodifiableSet(precedessors);
    }

}
