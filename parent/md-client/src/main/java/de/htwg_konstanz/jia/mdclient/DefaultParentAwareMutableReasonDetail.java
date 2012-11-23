/**
 * 
 */
package de.htwg_konstanz.jia.mdclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
@Immutable
public final class DefaultParentAwareMutableReasonDetail implements ParentAwareMutableReasonDetail {

    private final AnalysisResult parent;
    private final MutableReasonDetail mutableReasonDetail;

    private DefaultParentAwareMutableReasonDetail(final AnalysisResult parent,
            final MutableReasonDetail mutableReasonDetail) {
        super();
        this.parent = parent;
        this.mutableReasonDetail = mutableReasonDetail;
    }

    public static DefaultParentAwareMutableReasonDetail getInstance(final AnalysisResult parent,
            final MutableReasonDetail mutableReasonDetail) {
        return new DefaultParentAwareMutableReasonDetail(parent, mutableReasonDetail);
    }

    public static Collection<ParentAwareMutableReasonDetail> getInstancesFor(final AnalysisResult parent) {
        final Collection<MutableReasonDetail> unwrappedReasons = parent.reasons;
        final List<ParentAwareMutableReasonDetail> result = new ArrayList<ParentAwareMutableReasonDetail>(
                unwrappedReasons.size());
        for (final MutableReasonDetail mutableReasonDetail : unwrappedReasons) {
            result.add(getInstance(parent, mutableReasonDetail));
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public IsImmutable isImmutable() {
        return parent.isImmutable;
    }

    @Override
    public String dottedClassName() {
        return parent.dottedClassName;
    }

    @Override
    public Reason reason() {
        return mutableReasonDetail.reason();
    }

    @Override
    public CodeLocation<?> codeLocation() {
        return mutableReasonDetail.codeLocation();
    }

    @Override
    public String message() {
        return mutableReasonDetail.message();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mutableReasonDetail == null) ? 0 : mutableReasonDetail.hashCode());
        result = prime * result + ((parent == null) ? 0 : parent.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultParentAwareMutableReasonDetail)) {
            return false;
        }
        final DefaultParentAwareMutableReasonDetail other = (DefaultParentAwareMutableReasonDetail) obj;
        if (mutableReasonDetail == null) {
            if (other.mutableReasonDetail != null) {
                return false;
            }
        } else if (!mutableReasonDetail.equals(other.mutableReasonDetail)) {
            return false;
        }
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("parent", parent).append("mutableReasonDetail", mutableReasonDetail);
        return builder.toString();
    }

}
