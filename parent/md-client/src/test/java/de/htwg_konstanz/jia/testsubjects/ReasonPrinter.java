package de.htwg_konstanz.jia.testsubjects;

import java.util.Collection;

import org.mutabilitydetector.MutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
interface ReasonPrinter {

    void printReasonsIfNotEmpty(String className, Collection<MutableReasonDetail> reasons);

}
