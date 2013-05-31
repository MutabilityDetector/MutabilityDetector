package org.mutabilitydetector.checkers.settermethod;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * @author Juergen Fickel
 * @version 08.03.2013
 */
public final class AssignmentGuardTest {

    @NotThreadSafe
    private static final class ListBuilder<T> {
        private final List<T> list;

        private ListBuilder() {
            list = new ArrayList<T>();
        }

        public static <T> List<T> asList(final T soleListItem) {
            final ListBuilder<T> b = new ListBuilder<T>();
            b.add(soleListItem);
            return b.toList();
        }

        public ListBuilder<T> add(final T newListItem) {
            list.add(newListItem);
            return this;
        }

        public List<T> toList() {
            return Collections.unmodifiableList(list);
        }
    } // class ListBuilder<T>


    @Test
    public void factoryMethodsChecksDelegationTargetArgument() {
        final String exp = "Argument 'delegationTarget' must not be null!";
        try {
            AssignmentGuard.newInstance(null, Collections.<AbstractInsnNode> emptyList());
            fail(String.format("Expected NullPointerException with message '%s'.", exp));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

    @Test
    public void factoryMethodAssertsThatPredecessorInstructionsAreNotNull() {
        final String exp = "Argument 'predecessorInstructions' must not be null!";
        try {
            AssignmentGuard.newInstance(NullJumpInsn.getInstance(), null);
            fail(String.format("Expected NullPointerException with message '%s'.", exp));
        } catch (final NullPointerException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

    @Test
    public void factoryMethodAssertsThatPredecessorInstructionsAreNotEmpty() {
        final String exp = "Argument 'predecessorInstructions' must not be empty!";
        try {
            AssignmentGuard.newInstance(NullJumpInsn.getInstance(), Collections.<AbstractInsnNode> emptyList());
            fail(String.format("Expected IllegalArgumentException with message '%s'.", exp));
        } catch (final IllegalArgumentException e) {
            assertThat(e.getMessage(), is(equalTo(exp)));
        }
    }

    @Test
    public void assignmentGuardsWithSameDelegationTargetAndSamePredecessorListAreEqual() {
        final List<AbstractInsnNode> list = ListBuilder.asList((AbstractInsnNode) new VarInsnNode(Opcodes.ILOAD, 1));
        final AssignmentGuard a1 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), list);
        final AssignmentGuard a2 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), list);
        assertThat(a1, is(equalTo(a2)));
    }

    @Test
    public void assignmentGuardsWithSameDelegationTargetAndSamePredecessorListHaveSameHashCode() {
        final List<AbstractInsnNode> list = ListBuilder.asList((AbstractInsnNode) new VarInsnNode(Opcodes.ILOAD, 1));
        final AssignmentGuard a1 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), list);
        final AssignmentGuard a2 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), list);
        assertThat(a1.hashCode(), is(equalTo(a2.hashCode())));
    }

    @Test
    public void assignmentGuardsWithSameDelegationTargetAndSameEntriesInPredecessorListAreEqual() {
        final AbstractInsnNode predecessor = new VarInsnNode(Opcodes.ILOAD, 1);
        final AssignmentGuard a1 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(),
                ListBuilder.asList(predecessor));
        final AssignmentGuard a2 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(),
                ListBuilder.asList(predecessor));
        assertThat(a1, is(equalTo(a2)));
    }

    @Test
    public void assignmentGuardsWithSameDelegationTargetAndSameEntriesInPredecessorListHaveSameHashCode() {
        final AbstractInsnNode predecessor = new VarInsnNode(Opcodes.ILOAD, 1);
        final AssignmentGuard a1 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(),
                ListBuilder.asList(predecessor));
        final AssignmentGuard a2 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(),
                ListBuilder.asList(predecessor));
        assertThat(a1.hashCode(), is(equalTo(a2.hashCode())));
    }

    @Test
    public void assignmentGuardsWithSameDelegationTargetAndPracitallyEqualPredecessorsAreEqual() {
        final AbstractInsnNode p1 = new VarInsnNode(Opcodes.ILOAD, 1);
        final AbstractInsnNode p2 = new VarInsnNode(Opcodes.ILOAD, 1);
        final AssignmentGuard a1 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), ListBuilder.asList(p1));
        final AssignmentGuard a2 = AssignmentGuard.newInstance(NullJumpInsn.getInstance(), ListBuilder.asList(p2));
        assertThat(a1, is(equalTo(a2)));
    }

}
