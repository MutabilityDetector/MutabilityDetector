package org.mutabilitydetector.checkers;

import static org.mutabilitydetector.IAnalysisSession.IsImmutable.DEFINITELY_NOT;
import static org.mutabilitydetector.IAnalysisSession.IsImmutable.PROBABLY;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * This checker visits types and fields. Types can be visited separately, fields
 * should be visited as part of visiting an outer type.
 * 
 * The rules of inherent mutability are defined as:
 * 
 * Inherently mutable: Interfaces; Abstract classes; primitive Array types
 * 
 * Inherently immutable: Enum types; primitive types ie. boolean, char, byte,
 * short, int, long, float, double
 * 
 * @author graham
 * 
 */
public class InherentTypeMutabilityChecker extends AbstractMutabilityChecker {

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if(isAbstract(access) || isInterface(access)) {
			reasons.add("Inherently mutable as declared as an abstract type.");
			result = DEFINITELY_NOT;
		}
	}

	private boolean isInterface(int access) {
		return (access & Opcodes.ACC_INTERFACE) != 0;
	}

	private boolean isAbstract(int access) {
		return (access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		/*This check was causing far too many classes to be called mutable. It would be better if
		it was possible to check that an inherently mutable type was *actually mutated*. Calling
		an entire class mutable for having a mutable field which it doesn't mutate is a bit rubbish.
		 */		
		if(isPrimitiveArray(desc) && !("ENUM$VALUES".equals(name))){
			reasons.add("Field [" + name + "] is a primitive array. Array types are inherently mutable.");
			result = PROBABLY;
		}
		
		return super.visitField(access, name, desc, signature, value);
	}

	private boolean isPrimitiveArray(String desc) {
		return Type.ARRAY == Type.getType(desc).getSort();
	}

}