package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
public final class CallingConstructorWithThis {

    private static final class Nonsense {
        @SuppressWarnings("unused")
        private final CallingConstructorWithThis value;
        
        public Nonsense(final CallingConstructorWithThis aValue) {
            value = aValue;
        }
    }

    public CallingConstructorWithThis() {
        @SuppressWarnings("unused")
        final Nonsense nonsense = new Nonsense(this);
    }

}