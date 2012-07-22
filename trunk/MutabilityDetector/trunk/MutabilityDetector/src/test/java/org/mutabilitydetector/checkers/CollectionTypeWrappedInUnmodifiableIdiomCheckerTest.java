package org.mutabilitydetector.checkers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.mutabilitydetector.checkers.CollectionTypeWrappedInUmodifiableIdiomChecker.UnmodifiableWrapResult;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;

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

    private CollectionTypeWrappedInUmodifiableIdiomChecker checkerForAssigningToFieldOfType(String fieldType) {
        FieldInsnNode fieldInsnNode = putFieldForType(fieldType);
        return new CollectionTypeWrappedInUmodifiableIdiomChecker(fieldInsnNode);
    }

    private FieldInsnNode putFieldForType(String fieldType) {
        return new FieldInsnNode(Opcodes.PUTFIELD, "some/type/assigning/Field", "fieldName", fieldType);
    }
    
    
}
