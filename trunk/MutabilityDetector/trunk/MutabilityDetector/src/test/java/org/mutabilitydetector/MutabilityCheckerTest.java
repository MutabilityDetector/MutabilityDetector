/*
 * Mutability Detector
 * 
 * Copyright 2009 Graham Allan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mutabilitydetector;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mutabilitydetector.ImmutableAssert.assertDefinitelyNotImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertImmutable;
import static org.mutabilitydetector.ImmutableAssert.assertMaybeImmutable;
import static org.mutabilitydetector.TestUtil.formatReasons;
import static org.mutabilitydetector.TestUtil.getAnalysisResult;

import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mutabilitydetector.benchmarks.ImmutableExample;
import org.mutabilitydetector.benchmarks.MutableByHavingMutableFieldAssigned;
import org.mutabilitydetector.benchmarks.MutableByNoCopyOfIndirectlyConstructedField;
import org.mutabilitydetector.benchmarks.MutableByNotBeingFinalClass;
import org.mutabilitydetector.benchmarks.abstracttofield.MutableByAssigningAbstractTypeToField;
import org.mutabilitydetector.benchmarks.abstracttofield.MutableByAssigningInterfaceToField;
import org.mutabilitydetector.benchmarks.settermethod.MutableByHavingSetterMethod;
import org.mutabilitydetector.benchmarks.types.EnumType;

/**
 * This test acts as an overall progress checker as well as a general acceptance
 * of the tool so far. There are several classes used as micro benchmarks to
 * tell if the tool is correct. Once the checker can correctly assess these
 * classes the tool is correct for our definition.
 * 
 * @author Graham Allan (grundlefleck@gmail.com)
 * 
 */
public class MutabilityCheckerTest {

	@Test public void immutableExample() throws Exception {
		assertImmutable(ImmutableExample.class);
	}

	@Test public void mutableByAssigningAbstractTypeToField() throws Exception {
		assertDefinitelyNotImmutable(MutableByAssigningInterfaceToField.class);
	}

	@Test public void mutableByHavingMutableFieldAssigned() throws Exception {
		assertDefinitelyNotImmutable(MutableByHavingMutableFieldAssigned.class);
	}

	@Test public void mutableByHavingSetterMethod() throws Exception {
		assertDefinitelyNotImmutable(MutableByHavingSetterMethod.class);
	}

	@Test public void mutableByNoCopyOfIndirectlyConstructedField() throws Exception {
		assertDefinitelyNotImmutable(MutableByNoCopyOfIndirectlyConstructedField.class);
	}

	@Test public void mutableByNotBeingFinalClass() throws Exception {
		assertMaybeImmutable(MutableByNotBeingFinalClass.class);
	}

	@Test public void enumTypesAreImmutable() throws Exception {
		assertImmutable(EnumType.class);
	}

	@Test public void javaLangIntegerIsImmutable() throws Exception {
		assertImmutable(Integer.class);
	}

	@Test public void onlyOneReasonIsRaisedForAssigningAbstractTypeToField() throws Exception {
		AnalysisResult analysisResult = getAnalysisResult(MutableByAssigningAbstractTypeToField.class);
		Collection<CheckerReasonDetail> reasons = analysisResult.reasons;
		assertThat(formatReasons(reasons), reasons.size(), is(1));
		
		Reason reason = reasons.iterator().next().reason();
		assertThat(reason, CoreMatchers.<Reason>is(MutabilityReason.ABSTRACT_TYPE_TO_FIELD));
	}
	
	
}
