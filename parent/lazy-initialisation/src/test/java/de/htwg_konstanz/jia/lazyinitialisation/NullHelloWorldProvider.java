package de.htwg_konstanz.jia.lazyinitialisation;

/**
 * @author Juergen Fickel
 * @version 20.12.2012
 */
public final class NullHelloWorldProvider implements HelloWorldProvider {

    private static final NullHelloWorldProvider INSTANCE = new NullHelloWorldProvider();

    private NullHelloWorldProvider() {
        super();
    }

    public static final NullHelloWorldProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public String getHelloWorld() {
        return "";
    }

}
