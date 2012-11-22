package de.htwg_konstanz.jia.mdclient.matcher;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class FieldCanBeReassignedMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private final String expectedMessage;
    
    public FieldCanBeReassignedMatcher(final String fieldName, final String methodName) {
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
}