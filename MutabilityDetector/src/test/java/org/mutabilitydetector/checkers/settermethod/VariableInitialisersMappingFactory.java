package org.mutabilitydetector.checkers.settermethod;

import static org.mutabilitydetector.checkers.AccessModifierQuery.field;

import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

final class VariableInitialisersMappingFactory {

    private static final class InstanceHolder {
        private static final VariableInitialisersMappingFactory INSTANCE = new VariableInitialisersMappingFactory();
    }

    private VariableInitialisersMappingFactory() {
        super();
    }

    public static VariableInitialisersMappingFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public CandidatesInitialisersMapping getVariableInitialisersAssociationFor(final EnhancedClassNode classNode) {
        final CandidatesInitialisersMapping candidates = collectCandidates(classNode);
        return collectInitialisers(classNode, candidates);
    }

    private static CandidatesInitialisersMapping collectCandidates(final EnhancedClassNode classNode) {
        final CandidatesInitialisersMapping result = CandidatesInitialisersMapping.newInstance();
        for (final FieldNode variable : classNode.getFields()) {
            if (isCandidate(variable.access)) {
                result.addCandidate(variable);
            }
        }
        return result;
    }

    private static boolean isCandidate(final int access) {
        return field(access).isPrivate() && field(access).isNotFinal();
    }

    private static CandidatesInitialisersMapping collectInitialisers(final EnhancedClassNode classNode,
            final CandidatesInitialisersMapping m) {
        final List<MethodNode> methodsOfAnalysedClass = classNode.getMethods();
        final Finder<CandidatesInitialisersMapping> f = InitialisersFinder.newInstance(methodsOfAnalysedClass, m);
        return f.find();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " []";
    }

}
