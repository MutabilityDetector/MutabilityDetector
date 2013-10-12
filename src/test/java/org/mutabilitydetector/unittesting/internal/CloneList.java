package org.mutabilitydetector.unittesting.internal;

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
