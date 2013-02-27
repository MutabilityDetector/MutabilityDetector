package de.htwg_konstanz.jia.lazyinitialisation;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 27.02.2013
 */
@NotThreadSafe
final class IntegerSetBuilder {
    private final Set<Integer> resultSet;

    private IntegerSetBuilder() {
        resultSet = new HashSet<Integer>();
    }

    public static IntegerSetBuilder getInstance() {
        return new IntegerSetBuilder();
    }

    public IntegerSetBuilder add(final int integer) {
        resultSet.add(Integer.valueOf(integer));
        return this;
    }

    public Set<Integer> build() {
        return resultSet;
    }
}
