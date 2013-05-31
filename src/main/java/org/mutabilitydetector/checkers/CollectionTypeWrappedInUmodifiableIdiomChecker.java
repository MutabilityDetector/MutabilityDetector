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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.base.Objects;
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

    private static final ImmutableMultimap<String, CopyMethod> FIELD_TYPE_TO_COPY_METHODS = ImmutableMultimap.<String, CopyMethod>builder()
            .putAll("java.util.List", 
                    new CopyMethod(dotted("java.util.ArrayList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.LinkedList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.Vector"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.concurrent.CopyOnWriteArrayList"), "<init>", "(Ljava/util/Collection;)V"))
            .putAll("java.util.Set",
                    new CopyMethod(dotted("java.util.HashSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.LinkedHashSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.TreeSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.TreeSet"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListSet"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java.util.concurrent.CopyOnWriteArraySet"), "<init>", "(Ljava/util/Collection;)V"))
            .putAll("java.util.Map",
                    new CopyMethod(dotted("java.util.HashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.IdentityHashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.TreeMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.TreeMap"), "<init>", "(Ljava/util/SortedMap;)V"),
                    new CopyMethod(dotted("java.util.WeakHashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.Hashtable"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.IdentityHashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.LinkedHashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentHashMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListMap"), "<init>", "(Ljava/util/SortedMap;)V"))
            .putAll("java.util.SortedMap",
                    new CopyMethod(dotted("java.util.TreeMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.TreeMap"), "<init>", "(Ljava/util/SortedMap;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListMap"), "<init>", "(Ljava/util/Map;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListMap"), "<init>", "(Ljava/util/SortedMap;)V"))
            .putAll("java.util.SortedSet",
                    new CopyMethod(dotted("java.util.TreeSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.TreeSet"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java.util.concurrent.ConcurrentSkipListSet"), "<init>", "(Ljava/util/SortedSet;)V"))
            .putAll("java.util.Collection",
                    new CopyMethod(dotted("java/util/ArrayList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/CopyOnWriteArrayList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/LinkedList"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/Vector"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/HashSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/LinkedHashSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/TreeSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/TreeSet"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java/util/concurrent/ConcurrentSkipListSet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/ConcurrentSkipListSet"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java/util/concurrent/CopyOnWriteArraySet"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/ConcurrentLinkedQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/DelayQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/LinkedBlockingDeque"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/LinkedBlockingQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/LinkedTransferQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/PriorityBlockingQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/PriorityQueue"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/PriorityQueue"), "<init>", "(Ljava/util/concurrent/PriorityQueue;)V"),
                    new CopyMethod(dotted("java/util/concurrent/PriorityQueue"), "<init>", "(Ljava/util/SortedSet;)V"),
                    new CopyMethod(dotted("java/util/concurrent/ConcurrentLinkedDeque"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/ArrayDeque"), "<init>", "(Ljava/util/Collection;)V"),
                    new CopyMethod(dotted("java/util/concurrent/ArrayBlockingQueue"), "<init>", "(IZLjava/util/Collection;)V"))
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
            return Objects.hashCode(desc, name, owner);
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

    private FieldInsnNode fieldInsnNode;
    private Type typeAssignedToField;
    
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
    
    public CollectionTypeWrappedInUmodifiableIdiomChecker(FieldInsnNode fieldInsnNode, Type typeAssignedToField) {
        checkArgument(fieldInsnNode.getOpcode() == Opcodes.PUTFIELD, "Checking for unmodifiable wrap idiom requires PUTFIELD instruction");
        
        this.fieldInsnNode = fieldInsnNode;
        this.typeAssignedToField = typeAssignedToField;
    }

    public UnmodifiableWrapResult checkWrappedInUnmodifiable() {
        if (!CAN_BE_WRAPPED.contains(typeAssignedToField())) {
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
    
    
    private String typeAssignedToField() {
        return CLASS_NAME_CONVERTER.dotted(typeAssignedToField.getInternalName());
    }

    private boolean wrapsInUnmodifiable() {
        if (!(fieldInsnNode.getPrevious() instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode previousInvocation = (MethodInsnNode) fieldInsnNode.getPrevious();
        String methodName = previousInvocation.name;
        String onClass = previousInvocation.owner;
        
        return UNMODIFIABLE_METHOD_OWNER.equals(CLASS_NAME_CONVERTER.dotted(onClass)) 
                && FIELD_TYPE_TO_UNMODIFIABLE_METHOD.get(typeAssignedToField()).equals(methodName);
    }

    private boolean safelyCopiesBeforeWrapping(MethodInsnNode unmodifiableMethodCall) {
        if (!(unmodifiableMethodCall.getPrevious() instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode shouldBeCopyConstructor = (MethodInsnNode) unmodifiableMethodCall.getPrevious();
        
        return FIELD_TYPE_TO_COPY_METHODS.containsEntry(typeAssignedToField(), CopyMethod.from(shouldBeCopyConstructor));
    }

}