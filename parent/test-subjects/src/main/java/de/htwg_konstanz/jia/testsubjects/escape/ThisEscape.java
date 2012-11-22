/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.escape;

import java.awt.Event;
import java.util.EventListener;

/**
 * This class is taken from Brian Goetz' <em>Java Concurrency in Practice</em>,
 * listing 3.7 on page 41.
 * 
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class ThisEscape {

    private static final class EventSource {
        public void registerListener(final EventListener listener) {
            return;
        }
    }

    public ThisEscape(final EventSource source) {
        source.registerListener(
            new EventListener() {
                public void onEvent(final Event e) {
                    doSomething(e);
                }
            }
        );
    }

    private void doSomething(final Event e) {
        return;
    }

}
