package org.mutabilitydetector.checkers;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */




import static com.google.common.base.Preconditions.checkArgument;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.DOES_NOT_WRAP_USING_WHITELISTED_METHOD;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.FIELD_TYPE_CANNOT_BE_WRAPPED;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_AND_COPIES_SAFELY;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_BUT_DOES_NOT_COPY;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.mutabilitydetector.checkers.info.CopyMethod;
import org.mutabilitydetector.locations.ClassNameConverter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

public class CollectionTypeWrappedInUnmodifiableIdiomChecker {
    
    private static final ClassNameConverter CLASS_NAME_CONVERTER = new ClassNameConverter();

    private static final ImmutableSet<String> CAN_BE_WRAPPED = ImmutableSet.of("java.util.Collection",
                                                                               "java.util.Set",
                                                                               "java.util.SortedSet",
                                                                               "java.util.List",
                                                                               "java.util.Map",
                                                                               "java.util.SortedMap",
                                                                               "com.google.common.collect.ImmutableList");
    
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
    
    
    private final FieldInsnNode fieldInsnNode;
    private final Type typeAssignedToField;
    private final ImmutableMultimap<String, CopyMethod> userDefinedCopyMethods;
    
    public CollectionTypeWrappedInUnmodifiableIdiomChecker(FieldInsnNode fieldInsnNode, Type typeAssignedToField, 
            ImmutableMultimap<String, CopyMethod> userDefinedCopyMethods) {
        checkArgument(fieldInsnNode.getOpcode() == Opcodes.PUTFIELD, "Checking for unmodifiable wrap idiom requires PUTFIELD instruction");
        this.fieldInsnNode = fieldInsnNode;
        this.typeAssignedToField = typeAssignedToField;
        this.userDefinedCopyMethods = userDefinedCopyMethods;
    }

    public UnmodifiableWrapResult checkWrappedInUnmodifiable() {
        if (!CAN_BE_WRAPPED.contains(typeAssignedToField())) {
            return FIELD_TYPE_CANNOT_BE_WRAPPED;
        } 
        
        if (wrapsInUnmodifiable()) {
            MethodInsnNode wrappingMethodCall = (MethodInsnNode) lastMeaningfulNode(fieldInsnNode);
            if (safelyCopiesBeforeWrapping(wrappingMethodCall)) {
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
        AbstractInsnNode lastMeaningfulNode = lastMeaningfulNode(fieldInsnNode);
        if (!(lastMeaningfulNode instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode previousInvocation = (MethodInsnNode) lastMeaningfulNode;
        String methodName = previousInvocation.name;
        String onClass = previousInvocation.owner;
        
        return UNMODIFIABLE_METHOD_OWNER.equals(CLASS_NAME_CONVERTER.dotted(onClass)) 
                && FIELD_TYPE_TO_UNMODIFIABLE_METHOD.get(typeAssignedToField()).equals(methodName);
    }

    private boolean safelyCopiesBeforeWrapping(MethodInsnNode unmodifiableMethodCall) {
        AbstractInsnNode lastMeaningfulNode = lastMeaningfulNode(unmodifiableMethodCall);
        if (!(lastMeaningfulNode instanceof MethodInsnNode)) {
            return false;
        }
        
        MethodInsnNode shouldBeCopyConstructor = (MethodInsnNode) lastMeaningfulNode;
        return configuratedAsImmutableCopyMethod(shouldBeCopyConstructor);
    }

    private boolean configuratedAsImmutableCopyMethod(MethodInsnNode shouldBeCopyConstructor) {
        return (FIELD_TYPE_TO_COPY_METHODS.containsEntry(typeAssignedToField(), CopyMethod.from(shouldBeCopyConstructor))
                 || userDefinedCopyMethods.containsEntry(typeAssignedToField(), CopyMethod.from(shouldBeCopyConstructor)));
    }
    
    private AbstractInsnNode lastMeaningfulNode(AbstractInsnNode node) {
        AbstractInsnNode previous = node.getPrevious();
        return (previous instanceof LabelNode) || (previous instanceof LineNumberNode)
                ? lastMeaningfulNode(previous)
                : previous;
    }
    
    public enum UnmodifiableWrapResult {
        FIELD_TYPE_CANNOT_BE_WRAPPED(false, false, false),
        DOES_NOT_WRAP_USING_WHITELISTED_METHOD(true, false, false),
        WRAPS_BUT_DOES_NOT_COPY(true, true, false),
        WRAPS_AND_COPIES_SAFELY(true, true, true);
        
        public final boolean canBeWrapped;
        public final boolean invokesWhitelistedWrapperMethod;
        public final boolean safelyCopiesBeforeWrapping;
        
        UnmodifiableWrapResult(boolean canBeWrapped, boolean isWrapped, boolean safelyCopiesBeforeWrapping) {
            this.canBeWrapped = canBeWrapped;
            this.invokesWhitelistedWrapperMethod = isWrapped;
            this.safelyCopiesBeforeWrapping = safelyCopiesBeforeWrapping;
        }
    }
}
