package org.mutabilitydetector.checkers;

import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.collect.ImmutableSet;

class CollectionTypeWrappedInUmodifiableIdiomChecker {
    
    private static final ImmutableSet<String> CAN_BE_WRAPPED = ImmutableSet.of("java.util.Collection",
                                                                               "java.util.Set",
                                                                               "java.util.SortedSet",
                                                                               "java.util.List",
                                                                               "java.util.Map",
                                                                               "java.util.SortedMap");
    private Dotted referenceTypeOfField;
    private FieldInsnNode fieldInsnNode;
    
    public static class UnmodifiableWrapperIdiom {
        public final boolean canBeWrapped;
        public final boolean isWrapped;
        public final boolean safelyCopiesBeforeWrapping;
        
        public UnmodifiableWrapperIdiom(boolean canBeWrapped, boolean isWrapped, boolean safelyCopiesBeforeWrapping) {
            this.canBeWrapped = canBeWrapped;
            this.isWrapped = isWrapped;
            this.safelyCopiesBeforeWrapping = safelyCopiesBeforeWrapping;
        }
    }
    
    
    public CollectionTypeWrappedInUmodifiableIdiomChecker(Dotted referenceTypeOfField, FieldInsnNode fieldInsnNode) {
        this.referenceTypeOfField = referenceTypeOfField;
        this.fieldInsnNode = fieldInsnNode;
    }

    public UnmodifiableWrapperIdiom checkWrappedInUnmodifiable() {
        if (!CAN_BE_WRAPPED.contains(referenceTypeOfField.asString())) {
            return new UnmodifiableWrapperIdiom(false, false, false);
        }
        
        if (wrapsInUnmodifiable()) {
            if (safelyCopiesBeforeWrapping((MethodInsnNode) fieldInsnNode.getPrevious())) {
                return new UnmodifiableWrapperIdiom(true, true, true); 
            } else {
                return new UnmodifiableWrapperIdiom(true, true, false);
            }
        } else {
            return new UnmodifiableWrapperIdiom(true, false, false);
        }
    }
    
    
    private boolean wrapsInUnmodifiable() {
        if (!(fieldInsnNode.getPrevious() instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode previousInvocation = (MethodInsnNode) fieldInsnNode.getPrevious();
        String methodName = previousInvocation.name;
        String onClass = previousInvocation.owner;
        
        return onClass.equals("java/util/Collections") && methodName.equals("unmodifiableList");
    }

    private boolean safelyCopiesBeforeWrapping(MethodInsnNode unmodifiableMethodCall) {
        if (!(unmodifiableMethodCall.getPrevious() instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode shouldBeCopyConstructor = (MethodInsnNode) unmodifiableMethodCall.getPrevious();
        
        return MethodIs.aConstructor(shouldBeCopyConstructor.name) &&
                shouldBeCopyConstructor.owner.equals("java/util/ArrayList");
    }

}