/**
 * 
 */
package de.htwg_konstanz.jia.testsubjects.methods;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
public final class WithSideEffectOnHiddenObjectState {

    private final int value;
    @SuppressWarnings("unused") private volatile int invokationCounter;

    public WithSideEffectOnHiddenObjectState(final int value) {
        super();
        this.value = value;
        invokationCounter = 0;
    }

    public int getValue() {
        synchronized (this) {
            invokationCounter++;
        }
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
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
        if (!(obj instanceof WithSideEffectOnHiddenObjectState)) {
            return false;
        }
        final WithSideEffectOnHiddenObjectState other = (WithSideEffectOnHiddenObjectState) obj;
        if (value != other.value) {
            return false;
        }
        return true;
    }

}
