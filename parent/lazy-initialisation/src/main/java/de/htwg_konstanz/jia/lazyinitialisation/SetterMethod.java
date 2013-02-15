/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.MethodNode;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 11.02.2013
 */
public final class SetterMethod {

    public static final class Builder {
        private final MethodNode methodNode;
        private final List<AssignmentInsn> assignmentInstructions;
        private final List<JumpInsn> jumpInstructions;

        public Builder(final MethodNode theMethodNode) {
            methodNode = theMethodNode;
            assignmentInstructions = new ArrayList<AssignmentInsn>();
            jumpInstructions = new ArrayList<JumpInsn>();
        }

        public Builder assignmentInsn(final AssignmentInsn anAssignmentInsn) {
            assignmentInstructions.add(anAssignmentInsn);
            return this;
        }

        public Builder jumpInsn(final JumpInsn aJumpInstruction) {
            jumpInstructions.add(aJumpInstruction);
            return this;
        }

        public SetterMethod build() {
            return new SetterMethod(this);
        }

    }

    private final MethodNode methodNode;
    private final List<AssignmentInsn> assignmentInstructions;
    private final List<JumpInsn> jumpInstructions;

    private SetterMethod(final Builder builder) {
        methodNode = builder.methodNode;
        assignmentInstructions = new ArrayList<AssignmentInsn>(builder.assignmentInstructions);
        jumpInstructions = new ArrayList<JumpInsn>(builder.jumpInstructions);
    }

}
