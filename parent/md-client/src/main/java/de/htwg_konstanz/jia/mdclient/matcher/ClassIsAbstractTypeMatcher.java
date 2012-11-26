package de.htwg_konstanz.jia.mdclient.matcher;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
final class ClassIsAbstractTypeMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private static final Reason REASON = MutabilityReason.ABSTRACT_TYPE_INHERENTLY_MUTABLE;

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with reason ");
        desc.appendValue(REASON);
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        return isExpectedReason(mutableReasonDetail.reason());
    }

    private boolean isExpectedReason(final Reason reason) {
        return REASON == reason;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        return builder.toString();
    }

}
