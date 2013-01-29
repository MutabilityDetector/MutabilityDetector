package de.htwg_konstanz.jia.lazyinitialisation;

public final class LazyInitialisationWithObjectSemanticDefaultValue {

    private final String message;
    private HelloWorldProvider expensive;

    public LazyInitialisationWithObjectSemanticDefaultValue(final String aMessage) {
        message = aMessage;
        expensive = NullHelloWorldProvider.getInstance();
    }

    public String getExtendedMessage() {
        if (expensive.equals(NullHelloWorldProvider.getInstance())) {
            expensive = new DefaultHelloWorldProvider();
        }
        return message + " " + expensive.getHelloWorld();
    }

}
