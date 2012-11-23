package de.htwg_konstanz.jia.mdclient.matcher;

import static java.lang.String.format;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
final class MethodCausesSideEffectMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private final String nameOfAffectedField;
    private final String nameOfCausingMethod;

    /**
     * Creates a new instance of this class.
     * 
     * @param nameOfAffectedField
     *            name of the field which is affected by side effect.
     * @param nameOfCausingMethod
     *            name of the method which causes the side effect.
     */
    public MethodCausesSideEffectMatcher(final String nameOfAffectedField, final String nameOfCausingMethod) {
        super();
        Validate.notEmpty(nameOfAffectedField);
        Validate.notEmpty(nameOfCausingMethod);
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

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("nameOfAffectedField", nameOfAffectedField).append("nameOfCausingMethod", nameOfCausingMethod);
        return builder.toString();
    }

}