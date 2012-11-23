package de.htwg_konstanz.jia.mdclient.matcher;

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
final class ClassAllowsInheritanceMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with").appendText(" reason 'CAN_BE_SUBCLASSED'");
        desc.appendText(" because the class allows inheritance (is not final).");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        return isExpectedReason(mutableReasonDetail.reason())
                && isExpectedMessage(mutableReasonDetail.message());
    }

    private boolean isExpectedReason(final Reason reason) {
        return MutabilityReason.CAN_BE_SUBCLASSED == reason;
    }

    private boolean isExpectedMessage(final String actualMessage) {
        final StringBuilder expectedMessage = new StringBuilder();
        expectedMessage.append("Can be subclassed, therefore parameters declared to be this type ");
        expectedMessage.append("could be mutable subclasses at runtime.");
        return expectedMessage.toString().equals(actualMessage);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        return builder.toString();
    }

}
