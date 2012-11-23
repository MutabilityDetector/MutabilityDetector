package de.htwg_konstanz.jia.mdclient.matcher;

import static java.lang.String.format;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.FieldLocation;

import de.htwg_konstanz.jia.mdclient.ParentAwareMutableReasonDetail;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz.de)
 * @version 22.11.2012
 */
final class FieldHasMutableTypeMatcher extends TypeSafeMatcher<ParentAwareMutableReasonDetail> {

    private final String fieldName;
    private final Class<?> mutableFieldType;

    /**
     * Creates a new instance of this class.
     * 
     * @param fieldName
     *            name of the field which is mutable.
     * @param mutableFieldType
     *            class which surrounds {@code fieldName}.
     */
    public FieldHasMutableTypeMatcher(final String fieldName, final Class<?> mutableFieldType) {
        super();
        Validate.notEmpty(fieldName);
        Validate.notNull(mutableFieldType);
        this.fieldName = fieldName;
        this.mutableFieldType = mutableFieldType;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText(" a 'MutableReasonDetail' with").appendText(" reason 'MUTABLE_TYPE_TO_FIELD'");
        desc.appendText(" because the field ").appendValue(fieldName).appendText(" is of type ");
        desc.appendValue(mutableFieldType.getName()).appendText(".");
    }

    @Override
    protected boolean matchesSafely(final ParentAwareMutableReasonDetail mutableReasonDetail) {
        return isExpectedReason(mutableReasonDetail.reason())
                && isExpectedFieldName(mutableReasonDetail.codeLocation())
                && isExpectedMessage(mutableReasonDetail.message());
    }

    private boolean isExpectedFieldName(final CodeLocation<?> codeLocation) {
        final FieldLocation fieldLocation = (FieldLocation) codeLocation;
        return fieldName.equals(fieldLocation.fieldName());
    }

    private boolean isExpectedReason(final Reason reason) {
        return MutabilityReason.MUTABLE_TYPE_TO_FIELD == reason;
    }

    private boolean isExpectedMessage(final String actualMessage) {
        final String messageTemplate = "Field can have a mutable type (%s) assigned to it.";
        final String expectedMessage = format(messageTemplate, mutableFieldType.getName());
        return expectedMessage.equals(actualMessage);
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("fieldName", fieldName).append("mutableFieldType", mutableFieldType);
        return builder.toString();
    }

}