package org.mutabilitydetector.checkers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.DOES_NOT_WRAP_USING_WHITELISTED_METHOD;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_AND_COPIES_SAFELY;
import static org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult.WRAPS_BUT_DOES_NOT_COPY;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.junit.Test;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CollectionTypeWrappedInUnmodifiableIdiomCheckerTest {

    @Test(expected=IllegalArgumentException.class)
    public void requiresTheFieldInstructionNodeToBeAPutFieldInstruction() throws Exception {
        FieldInsnNode insnNode = new FieldInsnNode(Opcodes.GETFIELD, "some/type/Name", "fieldName", "the/assigned/Type");
        new CollectionTypeWrappedInUmodifiableIdiomChecker(insnNode);
    }
    
    @Test
    public void doesNotAllowCopyingIntoAbritraryType() throws Exception {
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "the/assigned/Type");
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
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
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "some/non/whitelisted/Type", "nonWhitelistedMethod", "()V");
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
        assertThat(checker.checkWrappedInUnmodifiable().invokesWhitelistedWrapperMethod, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(DOES_NOT_WRAP_USING_WHITELISTED_METHOD));
    }

    @Test
    public void findsThatAssignmentDoesNotIfCallsNonWhitelistedMethodIfPreviousInstructionWasNotAMethodInvocation() throws Exception {
        final VarInsnNode varInsn = new VarInsnNode(Opcodes.ASTORE, 2);
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return varInsn; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
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
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return varInsn; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_BUT_DOES_NOT_COPY));
    }

    @Test
    public void findsThatAssignmentIsNotSafelyCopiedIfCallToUnmodifiableMethodIsPrecededByInvokingNonWhitelistedMethod() throws Exception {
        final MethodInsnNode nonWhitelistedCopyMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "some/non/whitelisted/Type", "method", "");
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return nonWhitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(false));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_BUT_DOES_NOT_COPY));
    }

    @Test
    public void findsThatAssignmentIsSafelyCopiedIfCallToUnmodifiableMethodIsPrecededByInvokingWhitelistedCopyMethod() throws Exception {
        final MethodInsnNode whitelistedCopyMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/ArrayList", "<init>", "(Ljava/util/Collection;)V");
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "unmodifiableList", "") {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", "java/util/List") {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        
        assertThat(checker.checkWrappedInUnmodifiable().safelyCopiesBeforeWrapping, is(true));
        assertThat(checker.checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
    }

    @Test
    public void copyMethodsOfJdkListImplementingClassesAreWhitelistedCopyMethods() throws Exception {
        assertThat(checkerForPutfieldPrecededByCopyAndWrapMethods("java/util/concurrent/CopyOnWriteArrayList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List")
                .checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
        assertThat(checkerForPutfieldPrecededByCopyAndWrapMethods("java/util/LinkedList", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List")
                .checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
        assertThat(checkerForPutfieldPrecededByCopyAndWrapMethods("java/util/Vector", "<init>", "(Ljava/util/Collection;)V", "unmodifiableList", "java/util/List")
                .checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
        assertThat(checkerForPutfieldPrecededByCopyAndWrapMethods("java/util/Collections", "checkedList", "(Ljava/util/List;Ljava/lang/Class;)Ljava/util/List;", "unmodifiableList", "java/util/List")
                .checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
        assertThat(checkerForPutfieldPrecededByCopyAndWrapMethods("java/util/Collections", "synchronizedList", "(Ljava/util/List;)Ljava/util/List;", "unmodifiableList", "java/util/List")
                .checkWrappedInUnmodifiable(), is(WRAPS_AND_COPIES_SAFELY));
    }

    private CollectionTypeWrappedInUmodifiableIdiomChecker checkerForPutfieldPrecededByCopyAndWrapMethods(
            String copyMethodOwner, String copyMethodName, String copyMethodDesc, String wrappingMethodName,
            String fieldType) {
        final MethodInsnNode whitelistedCopyMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, copyMethodOwner, copyMethodName, copyMethodDesc);
        final MethodInsnNode wrappingMethod = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", wrappingMethodName, "") {
            @Override public AbstractInsnNode getPrevious() { return whitelistedCopyMethod; }
        };
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", fieldType) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        return new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
    }

    private CollectionTypeWrappedInUmodifiableIdiomChecker checkerOfPutFieldPrecededByCall(String fieldType, String methodName, String unmodifiableMethodDesc) {
        final MethodInsnNode wrappingMethod = new MethodInsnNode(INVOKESTATIC, "java/util/Collections", methodName, unmodifiableMethodDesc);
        FieldInsnNode fieldInsnNode = new FieldInsnNode(Opcodes.PUTFIELD, "some/type/Name", "fieldName", fieldType) {
            @Override public AbstractInsnNode getPrevious() { return wrappingMethod; }
        };
        CollectionTypeWrappedInUmodifiableIdiomChecker checker = new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
        return checker;
    }

    private CollectionTypeWrappedInUmodifiableIdiomChecker checkerForAssigningToFieldOfType(String fieldType) {
        FieldInsnNode fieldInsnNode = putFieldForType(fieldType);
        return new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
    }

    private FieldInsnNode putFieldForType(String fieldType) {
        return new FieldInsnNode(Opcodes.PUTFIELD, "some/type/assigning/Field", "fieldName", fieldType);
    }
    
    
}
