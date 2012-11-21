package de.htwg_konstanz.jia.testsubjects.escape;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class ReturningThisFromMethod {

    public static ReturningThisFromMethod instance;
    
    public ReturningThisFromMethod() {
        instance = this;
    }

    public static ReturningThisFromMethod getInstance() {
        return instance;
    }

}
