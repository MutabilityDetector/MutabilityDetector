package org.mutabilitydetector.checkers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;

import java.util.Collection;

/**
 * @author: Marc Gomez / marc.gomez82 (at) gmail.com
 */
public class SupersededReasonsFilter {

    public Collection<MutableReasonDetail> filterSupersededReasons(Collection<MutableReasonDetail> reasons) {
        return Lists.newArrayList(Iterables.filter(reasons, supersededReasonsFilter(reasons)));
    }

    private static Predicate<MutableReasonDetail> supersededReasonsFilter(Collection<MutableReasonDetail> reasons) {
        final ImmutableSet<Reason> allReasons = ImmutableSet.copyOf(Iterables.transform(reasons, mutableReasonDetailToReasonTransformer()));
        return new Predicate<MutableReasonDetail>() {
            public boolean apply(MutableReasonDetail input) {
                return !input.reason().supersededByOneOf(Iterables.toArray(allReasons, Reason.class));
            }
        };
    }

    private static Function<MutableReasonDetail, Reason> mutableReasonDetailToReasonTransformer() {
        return new Function<MutableReasonDetail, Reason>() {
            public Reason apply(MutableReasonDetail input) {
                return input.reason();
            }
        };
    }

}
