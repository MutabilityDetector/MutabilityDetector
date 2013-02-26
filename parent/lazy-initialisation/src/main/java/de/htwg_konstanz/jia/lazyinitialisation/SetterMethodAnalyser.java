package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * 
 *
 * @author Juergen Fickel
 * @version 14.02.2013
 */
public final class SetterMethodAnalyser implements Runnable {

    private final FieldNode variable;
    private final List<UnknownTypeValue> possibleInitialValues;
    private final MethodNode setterMethod;

    private SetterMethodAnalyser(final FieldNode theVariable,
            final List<UnknownTypeValue> thePossibleInitialValues,
            final MethodNode theSetterMethod) {
        variable = theVariable;
        possibleInitialValues = thePossibleInitialValues;
        setterMethod = theSetterMethod;
    }

    public static SetterMethodAnalyser getInstance(final FieldNode variable,
            final List<UnknownTypeValue> possibleInitialValues,
            final MethodNode setterMethod) {
        final List<UnknownTypeValue> piv = Collections.unmodifiableList(notEmpty(possibleInitialValues));
        return new SetterMethodAnalyser(notNull(variable), piv, notNull(setterMethod));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

}
