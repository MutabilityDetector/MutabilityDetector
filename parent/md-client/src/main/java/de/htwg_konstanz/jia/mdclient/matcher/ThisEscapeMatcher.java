package de.htwg_konstanz.jia.mdclient.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.ClassLocation;

import de.htwg_konstanz.jia.mdclient.NullParentAwareMutableReasonDetail;
import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
final class ThisEscapeMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private ParentAwareMutableReasonDetail reason = NullParentAwareMutableReasonDetail.INSTANCE;

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with");
        desc.appendText(" reason 'ESCAPED_THIS_REFERENCE'");
        desc.appendText(" because the 'this'-reference escapes the constructor of class '");
        desc.appendValue(reason.dottedClassName()).appendText("'.");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        reason = mutableReasonDetail;
        return isExpectedReason(mutableReasonDetail.reason())
                && isExpectedMessage(mutableReasonDetail.message())
                && isExpectedClassName(mutableReasonDetail);
    }

    private boolean isExpectedReason(final Reason actualReason) {
        return MutabilityReason.ESCAPED_THIS_REFERENCE == actualReason;
    }

    private boolean isExpectedMessage(final String actualMessage) {
        return "The 'this' reference is passed outwith the constructor.".equals(actualMessage);
    }

    private boolean isExpectedClassName(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        final String expectedTypeName = mutableReasonDetail.dottedClassName();
        final ClassLocation classLocation = (ClassLocation) mutableReasonDetail.codeLocation();
        return expectedTypeName.equals(classLocation.typeName());
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("reason", reason);
        return builder.toString();
    }

}