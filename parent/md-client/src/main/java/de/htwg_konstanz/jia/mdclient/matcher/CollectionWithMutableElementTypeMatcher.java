/**
 * 
 */
package de.htwg_konstanz.jia.mdclient.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 26.11.2012
 */
public final class CollectionWithMutableElementTypeMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private static final Reason EXPECTED_REASON = MutabilityReason.PUBLISHED_NON_FINAL_FIELD;

    @Override
    public void describeTo(final Description desc) {
        // TODO Auto-generated method stub
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail item) {
        return isExpectedReason(item.reason());
    }

    private boolean isExpectedReason(final Reason reason) {
        return EXPECTED_REASON == reason;
    }

    

}
