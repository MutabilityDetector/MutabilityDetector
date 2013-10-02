package org.mutabilitydetector.unittesting.playground;

import java.util.ArrayList;
import java.util.List;

public class SafeCopier<T> {

	public List<T> copy(List<T> list) {
		return new ArrayList<T>(list);
	}

}
