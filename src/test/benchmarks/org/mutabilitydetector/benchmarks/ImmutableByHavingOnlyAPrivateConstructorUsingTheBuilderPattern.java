package org.mutabilitydetector.benchmarks;

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
 
public class ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern {
  private final String field;
	
	// usual method of making a class immutable 
	// - make its constructor private: ref EffectiveJava
	private ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern (String field) {
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	
	// inner Builder class
	public static class Builder {
		public ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern build() {
			// this new OnlyPrivateConstructors() is fooling mutability detector
			// it thinks OnlyPrivateConstructors() is no longer immutable due to the
			// ability to call new to create an instance of OnlyPrivateConstructors.
			return new ImmutableByHavingOnlyAPrivateConstructorUsingTheBuilderPattern("hi");
		}
	}
}