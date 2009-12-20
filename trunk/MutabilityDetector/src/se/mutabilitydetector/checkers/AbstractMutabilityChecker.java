package se.mutabilitydetector.checkers;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import se.mutabilitydetector.IAnalysisSession.IsImmutable;

public abstract class AbstractMutabilityChecker implements IMutabilityChecker {

	protected Collection<String> reasons = new ArrayList<String>();
	protected IsImmutable result;
	protected String ownerClass;
	
	@Override
	public IsImmutable result() {
		if(result == null && reasons.size() == 0) {
			result = IsImmutable.DEFINITELY;
		}
		return result;
	}
	
	@Override
	public Collection<String> reasons() {
		return reasons;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		ownerClass = name;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
		
	}

	@Override
	public void visitEnd() {
		
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		
	}

	@Override
	public void visitSource(String source, String debug) {
		
	}

	protected boolean isFinal(int access) {
		return (access & ACC_FINAL) != 0;
	}

	protected boolean isPrivate(int access) {
		return (access & ACC_PRIVATE) != 0;
	}

	protected String dottedClassName(Type objectType) {
		String className = objectType.getInternalName();
		String dottedClassName = className.replace("/", ".");
		return dottedClassName;
	}

}
