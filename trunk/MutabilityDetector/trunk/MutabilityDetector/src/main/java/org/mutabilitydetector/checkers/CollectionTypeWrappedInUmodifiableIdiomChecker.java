package org.mutabilitydetector.checkers;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.DOES_NOT_WRAP_USING_WHITELISTED_METHOD;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.FIELD_TYPE_CANNOT_BE_WRAPPED;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_AND_COPIES_SAFELY;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_BUT_DOES_NOT_COPY;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.mutabilitydetector.locations.ClassNameConverter;
import org.mutabilitydetector.locations.Dotted;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

class CollectionTypeWrappedInUmodifiableIdiomChecker {
    
    private static final ClassNameConverter CLASS_NAME_CONVERTER = new ClassNameConverter();

    private static final ImmutableSet<String> CAN_BE_WRAPPED = ImmutableSet.of("java.util.Collection",
                                                                               "java.util.Set",
                                                                               "java.util.SortedSet",
                                                                               "java.util.List",
                                                                               "java.util.Map",
                                                                               "java.util.SortedMap");
    private static final String UNMODIFIABLE_METHOD_OWNER = "java.util.Collections";
    private static final ImmutableMap<String, String> FIELD_TYPE_TO_UNMODIFIABLE_METHOD = ImmutableMap.<String, String>builder()
            .put("java.util.Collection", "unmodifiableCollection")
            .put("java.util.Set", "unmodifiableSet")
            .put("java.util.SortedSet", "unmodifiableSortedSet")
            .put("java.util.List", "unmodifiableList")
            .put("java.util.Map", "unmodifiableMap")
            .put("java.util.SortedMap", "unmodifiableSortedMap")
            .build();

    private static class CopyMethod {
        private final Dotted owner;
        private final String name;
        private final String desc;
        
        public CopyMethod(Dotted owner, String name, String desc) {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }
        
        public static CopyMethod from(MethodInsnNode methodNode) {
            return new CopyMethod(Dotted.dotted(methodNode.owner), methodNode.name, methodNode.desc);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * desc.hashCode();
            result = prime * name.hashCode();
            result = prime * owner.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CopyMethod other = (CopyMethod) obj;
            return desc.equals(other.desc) && name.equals(other.name) && owner.equals(other.owner);
        }
        
        
    }
    
    private static final ImmutableMultimap<String, CopyMethod> FIELD_TYPE_TO_COPY_METHODS = ImmutableMultimap.<String, CopyMethod>builder()
            .putAll("java.util.List", 
                    new CopyMethod(dotted("java.util.ArrayList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.LinkedList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.Vector"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.concurrent.CopyOnWriteArrayList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.Collections"), "checkedList", "(Ljava/util/List;Ljava/lang/Class;)Ljava/util/List;"),
                    new CopyMethod(dotted("java.util.Collections"), "synchronizedList", "(Ljava/util/List;)Ljava/util/List;"))
            .build();
    
    
    private FieldInsnNode fieldInsnNode;
    
    public static enum UnmodifiableWrapResult {
        FIELD_TYPE_CANNOT_BE_WRAPPED(false, false, false),
        DOES_NOT_WRAP_USING_WHITELISTED_METHOD(true, false, false),
        WRAPS_BUT_DOES_NOT_COPY(true, true, false),
        WRAPS_AND_COPIES_SAFELY(true, true, true);
        
        public final boolean canBeWrapped;
        public final boolean invokesWhitelistedWrapperMethod;
        public final boolean safelyCopiesBeforeWrapping;
        
        private UnmodifiableWrapResult(boolean canBeWrapped, boolean isWrapped, boolean safelyCopiesBeforeWrapping) {
            this.canBeWrapped = canBeWrapped;
            this.invokesWhitelistedWrapperMethod = isWrapped;
            this.safelyCopiesBeforeWrapping = safelyCopiesBeforeWrapping;
        }
    }
    
    public CollectionTypeWrappedInUmodifiableIdiomChecker(FieldInsnNode fieldInsnNode) {
        checkArgument(fieldInsnNode.getOpcode() == Opcodes.PUTFIELD, "Checking for unmodifiable wrap idiom requires PUTFIELD instruction");
        
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
            return DOES_NOT_WRAP_USING_WHITELISTED_METHOD;
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
        
        return UNMODIFIABLE_METHOD_OWNER.equals(CLASS_NAME_CONVERTER.dotted(onClass)) 
                && FIELD_TYPE_TO_UNMODIFIABLE_METHOD.get(typeOfField()).equals(methodName);
    }

    private boolean safelyCopiesBeforeWrapping(MethodInsnNode unmodifiableMethodCall) {
        if (!(unmodifiableMethodCall.getPrevious() instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode shouldBeCopyConstructor = (MethodInsnNode) unmodifiableMethodCall.getPrevious();
        
        return FIELD_TYPE_TO_COPY_METHODS.containsEntry(typeOfField(), CopyMethod.from(shouldBeCopyConstructor));
    }

}