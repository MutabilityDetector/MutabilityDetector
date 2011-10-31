package org.mutabilitydetector.locations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.FieldLocation.fieldLocation;

import org.junit.Test;

public class FieldLocationTest {

	@Test
	public void hasFieldNameAndNameOfTypeContainingField() throws Exception {
		FieldLocation fieldLocation = FieldLocation.fieldLocation("myFieldName", ClassLocation.fromInternalName("a/b/MyClass"));
		assertThat(fieldLocation.fieldName(), is("myFieldName"));
		assertThat(fieldLocation.typeName(), is("a.b.MyClass"));
	}
	
	@Test
	public void comparesToOtherFieldLocationsSortingAlphabeticallyByOwningTypeNameThenFieldName() throws Exception {
		FieldLocation comparing = fieldLocation("myFieldName", fromInternalName("a/b/MyClass"));
		assertThat(comparing.compareTo(fieldLocation("myFieldNamd", fromInternalName("a/b/MyClass"))), is(greaterThan(0)));
		assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClass"))), is(equalTo(0)));
		assertThat(comparing.compareTo(fieldLocation("myFieldNamf", fromInternalName("a/b/MyClass"))), is(lessThan(0)));
		
		assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClasr"))), is(greaterThan(0)));
		assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClass"))), is(equalTo(0)));
		assertThat(comparing.compareTo(fieldLocation("myFieldName", fromInternalName("a/b/MyClast"))), is(lessThan(0)));
	}
}
