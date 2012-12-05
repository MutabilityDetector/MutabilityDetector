/*
 *    Copyright (c) 2008-2011 Graham Allan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.mutabilitydetector;

import static java.lang.Integer.toHexString;
import static java.lang.String.format;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.mutabilitydetector.locations.CodeLocation;

import com.google.common.base.Objects;

@Immutable
public final class MutableReasonDetail {

    private final String message;
    private final CodeLocation<?> location;
    private final Reason reason;
    private final int hashCode;

    private MutableReasonDetail(String message, CodeLocation<?> location, Reason reason) {
        this.message = message;
        this.location = location;
        this.reason = reason;
        
        this.hashCode = Objects.hashCode(message, location, reason);
    }
    
    public static MutableReasonDetail newMutableReasonDetail(@Nonnull String message, 
            @Nonnull CodeLocation<?> location, @Nonnull Reason reason) {
        checkNotNull(message, location, reason);
        return new MutableReasonDetail(message, location, reason);
    }

    private static void checkNotNull(String message, CodeLocation<?> location, Reason reason) {
        if (message == null) throw new NullPointerException("message cannot be null");
        if (location == null) throw new NullPointerException("location cannot be null");
        if (reason == null) throw new NullPointerException("reason cannot be null");
    }

    public Reason reason() {
        return reason;
    }

    public CodeLocation<?> codeLocation() {
        return location;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return format("%s@%s[%s, %s, %s]", getClass().getSimpleName(), toHexString(hashCode()), message, reason, location);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        MutableReasonDetail other = (MutableReasonDetail) obj;
        if (!location.equals(other.location)) { return false; }
        if (!message.equals(other.message)) { return false; }
        if (!reason.equals(other.reason)) { return false; }
        
        return true;
    }


}
