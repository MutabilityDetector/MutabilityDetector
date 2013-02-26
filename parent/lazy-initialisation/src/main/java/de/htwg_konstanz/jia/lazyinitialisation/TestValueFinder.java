/**
 * 
 */
package de.htwg_konstanz.jia.lazyinitialisation;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.Collections;
import java.util.List;

import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;


/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 15.02.2013
 */
final class TestValueFinder implements Runnable {

    private final FieldNode variable;
    private final List<UnknownTypeValue> possibleInitialValues;
    private final MethodNode setterMethod;

    private TestValueFinder(final FieldNode theVariable,
            final List<UnknownTypeValue> thePossibleInitialValues,
            final MethodNode theSetterMethod) {
        variable = theVariable;
        possibleInitialValues = thePossibleInitialValues;
        setterMethod = theSetterMethod;
    }

    public static TestValueFinder getInstance(final FieldNode variable,
            final List<UnknownTypeValue> possibleInitialValues,
            final MethodNode setterMethod) {
        final List<UnknownTypeValue> piv = Collections.unmodifiableList(notEmpty(possibleInitialValues));
        return new TestValueFinder(notNull(variable), piv, notNull(setterMethod));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        /*
         * Label der putfield-Anweisung suchen.
         * Label des Sprungziels suchen.
         * Nur fuer putfield-Anweisung mit hoechstem Index fortfahren.
         * 
         * Sprunganweisung (JumpInsnNode) suchen,
         * ifeq, ifne, iflt, ifge, ifgt, ifle,
         * ifnull, ifnonnull,
         * 
         * 
         * pruefen, ob Wert einer lokalen int-Variable auf den Stack gelegt wird.
         * iload, iload_0, iload_1, iload_2, iload_3,
         * arraylength
         * 
         * falls nicht, pruefen, ob getfield vor der Sprunganweisung kommt,
         * 
         * wenn nicht, pruefen, ob Vergleichsanweisung (lcmp, fcmpl, fcmpg, dcmpl, dcmpg - sind alle InsnNode)
         * vor der Sprunganweisung kommt,
         * 
         * falls ja, pruefen, welcher Wert davor auf den Stack gelegt wird
         * lload, fload, dload,
         * lload_0, lload_1, lload_2, lload_3,
         * fload_0, fload_1, fload_2, fload_3,
         * dload_0 dload_1, dload_2, dload_3,
         * 
         * fall kein Wert von lokaler Variable, pruefen, ob getfield vor der Vergleichsanweisung kommt,
         * 
         * Bei mehreren Bedingungen: Einzelne Bedingungen finden und später miteinander verrechnen.
         */
    }

}
