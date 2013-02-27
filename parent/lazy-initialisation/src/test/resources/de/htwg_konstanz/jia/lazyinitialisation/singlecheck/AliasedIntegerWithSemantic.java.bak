package de.htwg_konstanz.jia.lazyinitialisation.singlecheck;

public final class AliasedIntegerWithSemantic {

    private final String message;
    private int cachedValue;

    public AliasedIntegerWithSemantic(final String aMessage) {
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
