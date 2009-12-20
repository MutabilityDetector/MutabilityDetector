package se.mutabilitydetector.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;

public abstract class FieldVisitorAdapter implements FieldVisitor {

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

}
