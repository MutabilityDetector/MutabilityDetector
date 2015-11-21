package org.mutabilitydetector.checkers;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.collect.ImmutableList;
import org.mutabilitydetector.AnalysisError;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;

import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Collections;

@Immutable
public final class CheckerResult {

    public static final CheckerResult IMMUTABLE_CHECKER_RESULT = new CheckerResult(
            IsImmutable.IMMUTABLE,
            Collections.<MutableReasonDetail>emptyList(),
            Collections.<AnalysisError>emptyList());

    public final IsImmutable isImmutable;
    public final Collection<MutableReasonDetail> reasons;
    public final Collection<AnalysisError> errors;
    
    public CheckerResult(IsImmutable isImmutable, Iterable<MutableReasonDetail> reasons, Iterable<AnalysisError> errors) {
        this.isImmutable = isImmutable;
        this.reasons = ImmutableList.copyOf(reasons);
        this.errors = ImmutableList.copyOf(errors);
    }

    public static CheckerResult withNoErrors(IsImmutable isImmutable, Iterable<MutableReasonDetail> reasons) {
        return new CheckerResult(isImmutable, reasons, Collections.<AnalysisError>emptyList());
    }

}