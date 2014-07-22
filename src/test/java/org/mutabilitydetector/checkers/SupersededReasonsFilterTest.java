package org.mutabilitydetector.checkers;

import com.google.common.collect.Lists;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.Dotted;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author: Marc Gomez / marc.gomez82 (at) gmail.com
 */
public class SupersededReasonsFilterTest {


    private SupersededReasonsFilter filter;

    @Test
    public void supersededReasonsAreFiltered() {
        MutableReasonDetail mutableReasonDetail1 = createMutableReasonDetail(MutabilityReason.PUBLISHED_NON_FINAL_FIELD);
        MutableReasonDetail mutableReasonDetail2 = createMutableReasonDetail(MutabilityReason.ESCAPED_THIS_REFERENCE);
        MutableReasonDetail mutableReasonDetail3 = createMutableReasonDetail(MutabilityReason.NON_FINAL_FIELD);
        Collection<MutableReasonDetail> inputReasons = Lists.newArrayList(mutableReasonDetail1, mutableReasonDetail2, mutableReasonDetail3);

        Collection<MutableReasonDetail> actual = filter.filterSupersededReasons(inputReasons);
        Collection<MutableReasonDetail> expected = Lists.newArrayList(mutableReasonDetail1, mutableReasonDetail2);
        assertThat(actual, CoreMatchers.equalTo(expected));
    }

    private MutableReasonDetail createMutableReasonDetail(MutabilityReason reason) {
        return MutableReasonDetail.newMutableReasonDetail("message", ClassLocation.from(Dotted.dotted("DummyClass")), reason);
    }

    @Before
    public void setUp() throws Exception {
        filter = new SupersededReasonsFilter();
    }
}
