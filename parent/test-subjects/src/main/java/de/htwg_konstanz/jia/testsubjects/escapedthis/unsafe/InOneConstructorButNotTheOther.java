package de.htwg_konstanz.jia.testsubjects.escapedthis.unsafe;

import java.util.HashMap;
import java.util.Map;

import de.htwg_konstanz.jia.testsubjects.escapedthis.GiveMeYourThisReference;

public final class InOneConstructorButNotTheOther {
    public static final Object staticField = null;

    public InOneConstructorButNotTheOther(final boolean param) {
        new GiveMeYourThisReference(staticField, param ? "hi there" : "bye there", Long.valueOf("42"), this,
                newMap(), 1.0d);
    }

    public InOneConstructorButNotTheOther() {
        // This constructor is good. But the class is still mutable.
    }

    public static Map<String, String> newMap() {
        return new HashMap<String, String>();
    }
}