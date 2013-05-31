/**
 * 
 */
package org.mutabilitydetector.checkers.settermethod;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.objectweb.asm.tree.FieldNode;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 06.03.2013
 */
public final class AssignmentGuardVerifierTest {

    @Test(expected = NullPointerException.class)
    public void factoryMethodAssertsThatInitialValuesIsNotNull() {
        AssignmentGuardVerifier.newInstance(null, Collections.<FieldNode, Collection<JumpInsn>> emptyMap(),
                CandidatesInitialisersMapping.newInstance(), null);
    }

    @Test(expected = NullPointerException.class)
    public void factoryMethodAssertsThatAssignmentGuardsIsNotNull() {
        AssignmentGuardVerifier.newInstance(Collections.<FieldNode, Collection<UnknownTypeValue>> emptyMap(), null,
                CandidatesInitialisersMapping.newInstance(), null);
    }

    @Test(expected = NullPointerException.class)
    public void factoryMethodAssertsThatVariableInitialisersAssociationIsNotNull() {
        AssignmentGuardVerifier.newInstance(Collections.<FieldNode, Collection<UnknownTypeValue>> emptyMap(),
                Collections.<FieldNode, Collection<JumpInsn>> emptyMap(), null, null);
    }

    @Test(expected = NullPointerException.class)
    public void factoryMethodAssertsThatAbstractSetterMethodCheckerIsNotNull() {
        AssignmentGuardVerifier.newInstance(Collections.<FieldNode, Collection<UnknownTypeValue>> emptyMap(),
                Collections.<FieldNode, Collection<JumpInsn>> emptyMap(),
                CandidatesInitialisersMapping.newInstance(), null);
    }

}
