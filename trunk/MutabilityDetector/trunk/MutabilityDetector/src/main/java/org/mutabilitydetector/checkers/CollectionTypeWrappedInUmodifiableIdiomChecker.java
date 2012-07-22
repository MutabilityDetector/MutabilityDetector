package org.mutabilitydetector.checkers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.sun.xml.internal.ws.org.objectweb.asm.Opcodes.PUTFIELD;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.DOES_NOT_TRY_TO_WRAP;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.FIELD_TYPE_CANNOT_BE_WRAPPED;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_AND_COPIES_SAFELY;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_BUT_DOES_NOT_COPY;

import org.mutabilitydetector.locations.ClassNameConverter;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.collect.ImmutableSet;

class CollectionTypeWrappedInUmodifiableIdiomChecker {
    
    private static final ClassNameConverter CLASS_NAME_CONVERTER = new ClassNameConverter();

    private static final ImmutableSet<String> CAN_BE_WRAPPED = ImmutableSet.of("java.util.Collection",
                                                                               "java.util.Set",
                                                                               "java.util.SortedSet",
                                                                               "java.util.List",
                                                                               "java.util.Map",
                                                                               "java.util.SortedMap");
    
    private FieldInsnNode fieldInsnNode;
    
    public static enum UnmodifiableWrapResult {
        FIELD_TYPE_CANNOT_BE_WRAPPED(false, false, false),
        DOES_NOT_TRY_TO_WRAP(true, false, false),
        WRAPS_BUT_DOES_NOT_COPY(true, true, false),
        WRAPS_AND_COPIES_SAFELY(true, true, true);
        
        public final boolean canBeWrapped;
        public final boolean isWrapped;
        public final boolean safelyCopiesBeforeWrapping;
        
        private UnmodifiableWrapResult(boolean canBeWrapped, boolean isWrapped, boolean safelyCopiesBeforeWrapping) {
            this.canBeWrapped = canBeWrapped;
            this.isWrapped = isWrapped;
            this.safelyCopiesBeforeWrapping = safelyCopiesBeforeWrapping;
        }
    }
    
    public CollectionTypeWrappedInUmodifiableIdiomChecker(FieldInsnNode fieldInsnNode) {
        checkArgument(fieldInsnNode.getOpcode() == PUTFIELD, "Checking for unmodifiable wrap idiom requires PUTFIELD instruction");
        
        this.fieldInsnNode = fieldInsnNode;
    }

    public UnmodifiableWrapResult checkWrappedInUnmodifiable() {
        if (!CAN_BE_WRAPPED.contains(typeOfField())) {
            return FIELD_TYPE_CANNOT_BE_WRAPPED;
        }
        
        if (wrapsInUnmodifiable()) {
            if (safelyCopiesBeforeWrapping((MethodInsnNode) fieldInsnNode.getPrevious())) {
                return WRAPS_AND_COPIES_SAFELY;
            } else {
                return WRAPS_BUT_DOES_NOT_COPY;
            }
        } else {
            return DOES_NOT_TRY_TO_WRAP;
        }
    }
    
    
    private String typeOfField() {
        return CLASS_NAME_CONVERTER.dotted(fieldInsnNode.desc);
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