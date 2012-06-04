package org.mutabilitydetector.issues;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.locations.Dotted.dotted;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;

public class AssumeCopiedIntoUnmodifiable extends BaseMatcher<MutableReasonDetail> {

    public static class Assuming {
        private final String fieldName;
        public Assuming(String fieldName) {
            this.fieldName = fieldName;
        }
        public Matcher<MutableReasonDetail> hasCollectionsUnmodifiableTypeAssignedToIt() {
            return new AssumeCopiedIntoUnmodifiable(fieldName);
        }
        
    }

    public static AssumeCopiedIntoUnmodifiable.Assuming assuming(String fieldName) {
        return new Assuming(fieldName);
    }

    private static final List<Dotted> unmodifiableTypes = asList(dotted("java.util.List"), 
                                                                 dotted("java.util.Map"), 
                                                                 dotted("java.util.Set"),
                                                                 dotted("java.util.Collection"),
                                                                 dotted("java.util.SortedSet"),
                                                                 dotted("java.util.SortedMap"));

    private final String fieldName;
    
    public AssumeCopiedIntoUnmodifiable(String fieldName) {
        this.fieldName = fieldName;
    }
    
    @Override
    public void describeTo(Description description) { }


    private Dotted sniffOutAssignedTypeFromMessage(String message) {
        return dotted(message.substring(message.lastIndexOf("(") + 1, message.lastIndexOf(")")));
    }


    @Override
    public boolean matches(Object arg0) {
        MutableReasonDetail reasonDetail = (MutableReasonDetail) arg0;
        if (reasonDetail.reason().isOneOf(ABSTRACT_TYPE_TO_FIELD)) {
            String potentiallyAbstractField = ((FieldLocation) reasonDetail.codeLocation()).fieldName();
            Dotted assignedType = sniffOutAssignedTypeFromMessage(reasonDetail.message());
            if (potentiallyAbstractField.equals(fieldName) && unmodifiableTypes.contains(assignedType)) {
                return true;
            }
        }
        
        return false;
    }

}