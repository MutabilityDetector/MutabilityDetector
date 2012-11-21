package de.htwg_konstanz.jia.testsubjects;

import static java.lang.String.format;

import java.util.Collection;

import org.mutabilitydetector.MutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 21.11.2012
 */
final class DefaultReasonPrinter {

    private static enum Implementation implements ReasonPrinter {
        INSTANCE;

        @Override
        public void printReasonsIfNotEmpty(final String className, final Collection<MutableReasonDetail> reasons) {
            if (!reasons.isEmpty()) {
                final String classMessage = format("Analysed class: '%s' is mutable because of", className);
                System.out.println(classMessage);
                for (final MutableReasonDetail mutableReasonDetail : reasons) {
                    final String reasonMessage = format("    * reason: %s", mutableReasonDetail.reason());
                    System.out.println(reasonMessage);
                }
            }
        }
        
    }

    private DefaultReasonPrinter() {
        throw new AssertionError();
    }

    public static Implementation getInstance() {
        return Implementation.INSTANCE;
    }

}
