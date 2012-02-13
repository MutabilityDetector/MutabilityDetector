package org.mutabilitydetector.findbugs.warningtypes;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EscapedThisReference {
    public EscapedThisReference() {
        PublishYourThisReference.publishUnsafely(this);
    }
}

class PublishYourThisReference {
    public static void publishUnsafely(Object o) {
        // whatever
    }
}
