package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;

public final class IntegerWithSemanticDefault {

    private final String message;
    private int cachedValue;

    public IntegerWithSemanticDefault(final String aMessage) {
        message = aMessage;
        cachedValue = -2;
    }

    public String getMessage() {
        return message;
    }

    public int getMessageLength() {
        int result = cachedValue;
        if (-2 == result) {
            result = message.length();
            cachedValue = result;
        }
        return result;
    }

}
