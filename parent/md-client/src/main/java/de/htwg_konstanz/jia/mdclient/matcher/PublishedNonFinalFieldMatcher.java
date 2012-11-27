/**
 * 
 */
package de.htwg_konstanz.jia.mdclient.matcher;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.FieldLocation;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.11.2012
 */
@Immutable
public final class PublishedNonFinalFieldMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {
    
    private static final Reason EXPECTED_REASON = MutabilityReason.PUBLISHED_NON_FINAL_FIELD;

    private final String expectedFieldName;

    private PublishedNonFinalFieldMatcher(final String fieldName) {
        expectedFieldName = fieldName;
    }

    /**
     * @param fieldName
     *            expected name of the field which is not {@code final} but
     *            published. Must be neither {@code null} nor empty.
     * @return a new instance of this class.
     */
    public static PublishedNonFinalFieldMatcher getInstance(final String fieldName) {
        Validate.notEmpty(fieldName);
        return new PublishedNonFinalFieldMatcher(fieldName);
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with reason").appendValue(EXPECTED_REASON);
        desc.appendText(" because the non-final field ").appendValue(expectedFieldName);
        desc.appendText(" is published.");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail item) {
        return isExpectedReason(item.reason())
                && isExpectedFieldName(item.codeLocation());
    }

    private boolean isExpectedReason(final Reason reason) {
        return EXPECTED_REASON == reason;
    }

    private boolean isExpectedFieldName(final CodeLocation<?> codeLocation) {
        boolean result = false;
        if (isFieldLocation(codeLocation)) {
            final FieldLocation fieldLocation = (FieldLocation) codeLocation;
            result = expectedFieldName.equals(fieldLocation.fieldName());
        }
        return result;
    }

    private boolean isFieldLocation(final CodeLocation<?> codeLocation) {
        return codeLocation instanceof FieldLocation;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("EXPECTED_REASON", EXPECTED_REASON);
        builder.append("expectedFieldName", expectedFieldName);
        return builder.toString();
    }

}
