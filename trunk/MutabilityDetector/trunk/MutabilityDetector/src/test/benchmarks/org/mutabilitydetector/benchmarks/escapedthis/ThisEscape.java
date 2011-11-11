package org.mutabilitydetector.benchmarks.escapedthis;

import java.awt.Event;
import java.util.EventListener;

/**
 * This is (roughly) the example from JCIP.
 *
 */
@SuppressWarnings("unused")
public class ThisEscape {

    public ThisEscape(EventSource source) {
        source.registerListener(new EventListener() {
            public void onEvent(Event e) {
                // doSomething(e);
            }
        });
    }
}

@SuppressWarnings("unused")
class EventSource {
    public void registerListener(EventListener listener) {
        // do something
    }
}