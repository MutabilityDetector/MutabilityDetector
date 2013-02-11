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
        private final List<AssignmentInstruction> assignmentInstructions;
        private final List<JumpInstruction> jumpInstructions;

        public Builder(final MethodNode theMethodNode) {
            methodNode = theMethodNode;
            assignmentInstructions = new ArrayList<AssignmentInstruction>();
            jumpInstructions = new ArrayList<JumpInstruction>();
        }

        public Builder assignmentInsn(final AssignmentInstruction anAssignmentInsn) {
            assignmentInstructions.add(anAssignmentInsn);
            return this;
        }

        public Builder jumpInsn(final JumpInstruction aJumpInstruction) {
            jumpInstructions.add(aJumpInstruction);
            return this;
        }

        public SetterMethod build() {
            return new SetterMethod(this);
        }

    }

    private final MethodNode methodNode;
    private final List<AssignmentInstruction> assignmentInstructions;
    private final List<JumpInstruction> jumpInstructions;

    private SetterMethod(final Builder builder) {
        methodNode = builder.methodNode;
        assignmentInstructions = new ArrayList<AssignmentInstruction>(builder.assignmentInstructions);
        jumpInstructions = new ArrayList<JumpInstruction>(builder.jumpInstructions);
    }

}
