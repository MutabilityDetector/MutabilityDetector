package org.mutabilitydetector.checkers;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;

@Immutable
public final class CheckerResult {
    public final IsImmutable isImmutable;
    public final Collection<MutableReasonDetail> reasons;
    
    public CheckerResult(IsImmutable isImmutable, Iterable<MutableReasonDetail> reasons) {
        this.isImmutable = isImmutable;
        this.reasons = Collections.unmodifiableCollection(newArrayList(reasons));
    }
}