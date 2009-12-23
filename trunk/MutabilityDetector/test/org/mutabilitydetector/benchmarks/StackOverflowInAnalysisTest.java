package org.mutabilitydetector.benchmarks;

import java.lang.reflect.Constructor;

import org.junit.Test;
import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.MutableByAssigningAbstractTypeToField.AbstractStringContainer;
import org.mutabilitydetector.benchmarks.types.EnumType;



public class StackOverflowInAnalysisTest {

	private IAnalysisSession session = new AnalysisSession(null);

	@Test
	public void testInnerClassDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(ImmutableExample.class.getName());
		
		AbstractStringContainer abstractNameContainer = new MutableByAssigningAbstractTypeToField(null).new StringContainer();
		session.isImmutable(abstractNameContainer.getClass().getName());
		session.isImmutable(MutableByAssigningAbstractTypeToField.class.getName());
		session.isImmutable(MutableByAssigningAbstractTypeToField.AbstractStringContainer.class.getName());
	}
	
	@Test
	public void testVisitingEnumTypeDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(EnumType.class.getName());
	}
	
	@Test
	public void testAnalysingThisTestDoesNotCauseStackOverflowError() throws Exception {
		session.isImmutable(this.getClass().getName());
	}
	
	@Test
	public void testAnalysingConstructorClassDoesNotCauseStackOverflow() throws Exception {
		// Constructor has a field of type 'Class'
		// Class has a field of type 'Constructor'
		session.isImmutable(Constructor.class.getName());
	}
	
}
