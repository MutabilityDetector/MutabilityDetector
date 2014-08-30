package org.mutabilitydetector.unittesting.internal;

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



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "serial" })
public class CloneList<T> extends ArrayList<T> {
	final List<T> myCopy;
	public CloneList(List<T> other) {
		super();
		this.myCopy = Collections.unmodifiableList(new ArrayList<T>(other));
	}
}
