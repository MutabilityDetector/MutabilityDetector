/*
 *    Copyright (c) 2008-2013 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.mutabilitydetector.checkers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.DOES_NOT_WRAP_USING_WHITELISTED_METHOD;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_AND_COPIES_SAFELY;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_BUT_DOES_NOT_COPY;
import static org.objectweb.asm.Type.getType;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUnmodifiableIdiomChecker.UnmodifiableWrapResult;
import org.mutabilitydetector.checkers.info.CopyMethod;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.ImmutableMultimap;

@RunWith(Theories.class)
public class CollectionTypeWrappedInUnmodifiableIdiomCheckerTest {

    private final ImmutableMultimap<String, CopyMethod> NO_USER_DEFINED_COPY_METHODS = ImmutableMultimap.<String, CopyMethod>of();


    @Test(expected=IllegalArgumentException.class)
    public void requiresTheFieldInstructionNodeToBeAPutFieldInstruction() throws Exception {
        FieldInsnNode insnNode = new FieldInsnNode(Opcodes.GETFIELD, "some/type/Name", "fieldName", "the/field/Type");
        new CollectionTypeWrappedInUnmodifiableIdiomChecker(insnNode, null, NO_USER_DEFINED_COPY_METHODS);
    }

    @Test
    public void doesNotAllowCopyingIntoAbritraryType() throws Exception {
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "the/field/Type");
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, getType("the/assigned/Type"), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable(), is(UnmodifiableWrapResult.FIELD_TYPE_CANNOT_BE_WRAPPED));
    }

    @Test
    public void onlyUnmodifiableTypesOfferedByCollectionsAreRecognised() throws Exception {
        assertThat(checkerForAssigningToFieldOfType("java/util/Collection").checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checkerForAssigningToFieldOfType("java/util/List").checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checkerForAssigningToFieldOfType("java/util/Set").checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checkerForAssigningToFieldOfType("java/util/SortedSet").checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checkerForAssigningToFieldOfType("java/util/Map").checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checkerForAssigningToFieldOfType("java/util/SortedMap").checkWrappedInUnmodifiable().canBeWrapped, is(true));
    }

    @Test
    public void findsThatAssignmentDoesNotCallNonWhitelistedMethod() throws Exception {
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("some/non/whitelisted/Type", "nonWhitelistedMethod", "()V");
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(DOES_NOT_WRAP_USING_WHITELISTED_METHOD));
    }

    private static class StaticConcreteMethodInsnNode extends MethodInsnNode {
      StaticConcreteMethodInsnNode(String owner, String name, String desc) {
        super(Opcodes.INVOKESTATIC, owner, name, desc, false);
      }
    }

    @Test
    public void findsThatAssignmentDoesNotIfCallsNonWhitelistedMethodIfPreviousInstructionWasNotAMethodInvocation() throws Exception {
        final VarInsnNode varInsn = new VarInsnNode(Opcodes.ASTORE, 2);
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return varInsn; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(DOES_NOT_WRAP_USING_WHITELISTED_METHOD));
    }

    @Test
    public void findsAssignmentDoesTryToWrapInWhitelistedMethodIfCallingCollections_unmodifiableMethod() throws Exception {
        assertThat(checkerOfPutFieldPrecededByCall("java/util/List", "unmodifiableList", "(Ljava/util/List;)Ljava/util/List;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
        assertThat(checkerOfPutFieldPrecededByCall("java/util/Set", "unmodifiableSet", "(Ljava/util/Set;)Ljava/util/Set;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
        assertThat(checkerOfPutFieldPrecededByCall("java/util/SortedSet", "unmodifiableSortedSet", "(Ljava/util/SortedSet;)Ljava/util/SortedSet;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
        assertThat(checkerOfPutFieldPrecededByCall("java/util/Map", "unmodifiableMap", "(Ljava/util/Map;)Ljava/util/Map;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
        assertThat(checkerOfPutFieldPrecededByCall("java/util/SortedMap", "unmodifiableSortedMap", "(Ljava/util/SortedMap;)Ljava/util/SortedMap;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
        assertThat(checkerOfPutFieldPrecededByCall("java/util/Collection", "unmodifiableCollection", "(Ljava/util/Collection;)Ljava/util/Collection;")
                .checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(true));
    }

    @Test
    public void findsThatAssignmentIsNotSafelyCopiedIfCallToUnmodifiableMethodIsNotPrecededByMethodInstruction() throws Exception {
        final VarInsnNode varInsn = new VarInsnNode(Opcodes.ASTORE, 2);
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return varInsn; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_BUT_DOES_NOT_COPY));
    }

    @Test
    public void findsThatAssignmentIsNotSafelyCopiedIfCallToUnmodifiableMethodIsPrecededByInvokingNonWhitelistedMethod() throws Exception {
        final MethodInsnNode nonWhitelistedCopyMethod = new StaticConcreteMethodInsnNode("some/non/whitelisted/Type", "method", "");
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return nonWhitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_BUT_DOES_NOT_COPY));
    }

    @Test
    public void findsThatAssignmentIsSafelyCopiedIfCallToUnmodifiableMethodIsPrecededByInvokingWhitelistedCopyMethod() throws Exception {
        final MethodInsnNode whitelistedCopyMethod = new StaticConcreteMethodInsnNode("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/lang/Iterable") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().canBeWrapped, is(true));
        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(true));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
    }

    @Test
    public void decidesIfTypeCanBeSafelyWrappedUsingTypeThatIsActuallyAssignedRatherThanDeclarationOfField() throws Exception {
        final MethodInsnNode whitelistedCopyMethod = new StaticConcreteMethodInsnNode("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "unmodifiableList", "", false) {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };

        String typeOfField = "java/lang/Iterable";

        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", typeOfField) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(true));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
    }

    @Test
    public void ignoresNodesWhichDoNotResultInDifferentCodeSemantics() throws Exception {
        final MethodInsnNode whitelistedCopyMethod = new StaticConcreteMethodInsnNode("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        final LabelNode label = new LabelNode(new Label()) {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };
        final LineNumberNode lineNumber = new LineNumberNode(42, new LabelNode()) {
            @Override public AbstractInsnNode getPrevious() { return label; }
        };
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return lineNumber; }
        };

        String typeOfField = "java/util/List";

        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", typeOfField) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };

        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(List.class), NO_USER_DEFINED_COPY_METHODS);

        UnmodifiableWrapResult result = checker.checkWrappedInUnmodifiable();

        assertThat(result.invokesWhitelistedWrapperMethod, is(true));
        assertThat(result.safelyCopiesBeforeWrapping, is(true));
        assertThat(result, is(WRAPS_AND_COPIES_SAFELY));
    }

    @Theory
    public void validAssignmentsHold(ValidUnmodifiableAssignment dataPoint) throws Exception {
        CollectionTypeWrappedInUnmodifiableIdiomChecker checker = checkerForPutfieldPrecededByCopyAndWrapMethods(
                dataPoint.copyMethodOwner,
                dataPoint.copyMethodName,
                dataPoint.copyMethodDesc,
                dataPoint.wrappingMethodName,
                dataPoint.fieldType);
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
    }

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_List = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List"),
        new ValidUnmodifiableAssignment("java/util/LinkedList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List"),
        new ValidUnmodifiableAssignment("java/util/Vector", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List"),
        new ValidUnmodifiableAssignment("java/util/concurrent/CopyOnWriteArrayList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List"),
    };

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_Collection = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/CopyOnWriteArrayList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/LinkedList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/Vector", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/HashSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/LinkedHashSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListSet", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/CopyOnWriteArraySet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentLinkedQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/DelayQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/LinkedBlockingDeque", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/LinkedBlockingQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/LinkedTransferQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/PriorityBlockingQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/PriorityQueue", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/PriorityQueue", "<init>", "(Ljava/util/concurrent/PriorityQueue;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/PriorityQueue", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentLinkedDeque", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/ArrayDeque", "<init>", "(Ljava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ArrayBlockingQueue", "<init>", "(IZLjava/util/Collection;)V", "unmodifiableCollection", "java/util/Collection")
    };

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_Map = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/HashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/IdentityHashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/TreeMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/TreeMap", "<init>", "(Ljava/util/SortedMap;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/WeakHashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/Hashtable", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/IdentityHashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/LinkedHashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentHashMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableMap", "java/util/Map"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListMap", "<init>", "(Ljava/util/SortedMap;)V", "unmodifiableMap", "java/util/Map")
    };

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_SortedMap = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/TreeMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableSortedMap", "java/util/SortedMap"),
        new ValidUnmodifiableAssignment("java/util/TreeMap", "<init>", "(Ljava/util/SortedMap;)V", "unmodifiableSortedMap", "java/util/SortedMap"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListMap", "<init>", "(Ljava/util/SortedMap;)V", "unmodifiableSortedMap", "java/util/SortedMap"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListMap", "<init>", "(Ljava/util/Map;)V", "unmodifiableSortedMap", "java/util/SortedMap"),
    };

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_Set = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/HashSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSet", "java/util/Set"),
        new ValidUnmodifiableAssignment("java/util/LinkedHashSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSet", "java/util/Set"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSet", "java/util/Set"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableSet", "java/util/Set"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSet", "java/util/Set"),
        new ValidUnmodifiableAssignment("java/util/concurrent/CopyOnWriteArraySet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSet", "java/util/Set"),
    };

    @DataPoints public static ValidUnmodifiableAssignment[] java_util_SortedSet = new ValidUnmodifiableAssignment[] {
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSortedSet", "java/util/SortedSet"),
        new ValidUnmodifiableAssignment("java/util/concurrent/ConcurrentSkipListSet", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableSortedSet", "java/util/SortedSet"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/Collection;)V", "unmodifiableSortedSet", "java/util/SortedSet"),
        new ValidUnmodifiableAssignment("java/util/TreeSet", "<init>", "(Ljava/util/SortedSet;)V", "unmodifiableSortedSet", "java/util/SortedSet"),
    };


    private static class ValidUnmodifiableAssignment {
        public final String copyMethodOwner;
        public final String copyMethodName;
        public final String copyMethodDesc;
        public final String wrappingMethodName;
        public final String fieldType;

        public ValidUnmodifiableAssignment(String copyMethodOwner, String copyMethodName, String copyMethodDesc, String wrappingMethodName, String fieldType) {
            this.copyMethodOwner = copyMethodOwner;
            this.copyMethodName = copyMethodName;
            this.copyMethodDesc = copyMethodDesc;
            this.wrappingMethodName = wrappingMethodName;
            this.fieldType = fieldType;
        }
    }

    private CollectionTypeWrappedInUnmodifiableIdiomChecker checkerForPutfieldPrecededByCopyAndWrapMethods(
            String copyMethodOwner, String copyMethodName, String copyMethodDesc, String wrappingMethodName,
            String fieldType) {
        final MethodInsnNode whitelistedCopyMethod = new StaticConcreteMethodInsnNode(copyMethodOwner, copyMethodName, copyMethodDesc);
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", wrappingMethodName, "") {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", fieldType) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        return new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(fieldType), NO_USER_DEFINED_COPY_METHODS);
    }

    private CollectionTypeWrappedInUnmodifiableIdiomChecker checkerOfPutFieldPrecededByCall(String fieldType, String methodName, String unmodifiableMethodDesc) {
        final MethodInsnNode wrappingMethod = new StaticConcreteMethodInsnNode("java/util/Collections", methodName, unmodifiableMethodDesc);
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", fieldType) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        return new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(fieldType), NO_USER_DEFINED_COPY_METHODS);
    }

    private CollectionTypeWrappedInUnmodifiableIdiomChecker checkerForAssigningToFieldOfType(String fieldType) {
        FieldInsnNode fieldInsnNode = putFieldForType(fieldType);
        return new CollectionTypeWrappedInUnmodifiableIdiomChecker(fieldInsnNode, Type.getType(fieldType), NO_USER_DEFINED_COPY_METHODS);
    }

    private FieldInsnNode putFieldForType(String fieldType) {
        return new FieldInsnNode(Opcodes.PUTFIELD, "some/type/assigning/Field", "fieldName", fieldType);
    }

}
