package de.htwg_konstanz.jia.mdclient.matcher;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class MethodCausesSideEffectMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private final String nameOfAffectedField;
    private final String nameOfCausingMethod;

    public MethodCausesSideEffectMatcher(String nameOfAffectedField, String nameOfCausingMethod) {
        super();
        this.nameOfAffectedField = nameOfAffectedField;
        this.nameOfCausingMethod = nameOfCausingMethod;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with").appendText(" reason 'FIELD_CAN_BE_REASSIGNED'");
        desc.appendText(" because the field ").appendValue(nameOfAffectedField);
        desc.appendText(" is altered by method ").appendValue(nameOfCausingMethod).appendText(".");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        return isExpectedReason(mutableReasonDetail.reason())
                && isExpectedMessage(mutableReasonDetail.message());
    }

    private boolean isExpectedReason(final Reason reason) {
        return MutabilityReason.FIELD_CAN_BE_REASSIGNED == reason;
    }

    private boolean isExpectedMessage(final String actualMessage) {
        final String messageTemplate = "Field [%s] can be reassigned within method [%s]";
        final String expectedMessage = format(messageTemplate, nameOfAffectedField, nameOfCausingMethod);
        return expectedMessage.equals(actualMessage);
    }

}