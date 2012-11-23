package de.htwg_konstanz.jia.mdclient.matcher;

import static java.lang.String.format;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
final class FieldCanBeReassignedMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private final String expectedMessage;
    
    /**
     * Creates a new instance of this class.
     * 
     * @param fieldName
     *            name of the field which can be reassigned.
     * @param methodName
     *            name of the method which enables reassigning of
     *            {@code fieldName}.
     */
    public FieldCanBeReassignedMatcher(final String fieldName, final String methodName) {
        Validate.notEmpty(fieldName);
        Validate.notEmpty(methodName);
        expectedMessage = format("Field [%s] can be reassigned within method [%s]", fieldName, methodName);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a 'MutableReasonDetail' with\n");
        description.appendText("  reason 'FIELD_CAN_BE_REASSIGNED'\n");
        description.appendText("  and message: '").appendText(expectedMessage).appendText("'.");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        return MutabilityReason.FIELD_CAN_BE_REASSIGNED == mutableReasonDetail.reason()
                && expectedMessage.equals(mutableReasonDetail.message());
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("expectedMessage", expectedMessage);
        return builder.toString();
    }

}
