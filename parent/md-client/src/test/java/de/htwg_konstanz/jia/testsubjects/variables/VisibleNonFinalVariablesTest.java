package de.htwg_konstanz.jia.testsubjects.variables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.MutabilityReason;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.Reason;
import org.mutabilitydetector.locations.FieldLocation;
import org.mutabilitydetector.unittesting.internal.AnalysisSessionHolder;

import de.htwg_konstanz.jia.testsubjects.variables.VisibleNonFinalVariables;

/**
 * @author Juergen Fickel (jufickel@htwg-konstanz)
 * @version 15.11.2012
 */
public final class VisibleNonFinalVariablesTest {

    private interface PartialResult<T> {

        boolean doAllOfThisReasonsApply(T key, Reason mandatoryReason, Reason... furtherReasons);

    }

    private static final class PartialResults implements MultiMap, PartialResult<String> {

        private final MultiValueMap values = new MultiValueMap();

        public static PartialResults getFor(final AnalysisResult analysisResult) {
            final Collection<MutableReasonDetail> reasons = analysisResult.reasons;
            final PartialResults result = new PartialResults();
            for (final MutableReasonDetail reason : reasons) {
                final FieldLocation fieldLocation = (FieldLocation) reason.codeLocation();
                result.put(fieldLocation.fieldName(), reason.reason());
            }
            return result;
        }

        private PartialResults() {
            super();
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public Object get(final Object key) {
            return values.get(key);
        }

        @Override
        public boolean containsValue(final Object value) {
            return values.containsValue(value);
        }

        @Override
        public Object put(final Object key, final Object value) {
            return values.put(key, value);
        }

        @Override
        public boolean isEmpty() {
            return values.isEmpty();
        }

        @Override
        public boolean containsKey(final Object key) {
            return values.containsKey(key);
        }

        @Override
        public Object remove(final Object key) {
            return values.remove(key);
        }

        @Override
        public Collection<?> values() {
            return values.values();
        }

        @Override
        public void putAll(@SuppressWarnings("rawtypes") final Map m) {
            values.putAll(m);
        }

        @Override
        public void clear() {
            values.clear();
        }

        @Override
        public Set<?> keySet() {
            return values.keySet();
        }

        @Override
        public Set<?> entrySet() {
            return values.entrySet();
        }

        @Override
        public boolean equals(final Object o) {
            return values.equals(o);
        }

        @Override
        public int hashCode() {
            return values.hashCode();
        }

        @Override
        public Object remove(final Object key, final Object item) {
            return values.remove(key, item);
        }

        @Override
        public boolean doAllOfThisReasonsApply(final String key,
                final Reason mandatoryReason,
                final Reason... furtherReasons) {
            final Collection<?> valuesForKey = values.getCollection(key);
            if (null != valuesForKey && valuesForKey.contains(mandatoryReason)) {
                for (final Reason reason : furtherReasons) {
                    if (!valuesForKey.contains(reason)) {
                        return false;
                    }
                }
            }
            return true;
        }

    }

    private final byte numberOfVisibleVariables = 9;
    private final Set<String> expectedFieldNames = new HashSet<String>(numberOfVisibleVariables);

    public VisibleNonFinalVariablesTest() {
        super();
        expectedFieldNames.add("staticByteValue");
        expectedFieldNames.add("staticCharacter");
        expectedFieldNames.add("staticString");
        expectedFieldNames.add("number");
        expectedFieldNames.add("character");
        expectedFieldNames.add("longNumber");
        expectedFieldNames.add("someString");
        expectedFieldNames.add("mutableString");
        expectedFieldNames.add("numberObject");
    }

    @Test
    public void visibleVariablesMustBeFinal() {
        final AnalysisResult analysisResult = AnalysisSessionHolder.analysisResultFor(VisibleNonFinalVariables.class);
        assertMutable(analysisResult);
        assertAppropriateReasons(analysisResult);
    }

    private void assertMutable(final AnalysisResult analysisResult) {
        assertEquals(IsImmutable.NOT_IMMUTABLE, analysisResult.isImmutable);
    }

    private void assertAppropriateReasons(final AnalysisResult analysisResult) {
        final PartialResults fieldResults = PartialResults.getFor(analysisResult);
        assertEquals(numberOfVisibleVariables, fieldResults.size());
        for (final String key : expectedFieldNames) {
            assertTrue(fieldResults.doAllOfThisReasonsApply(key, MutabilityReason.NON_FINAL_FIELD,
                    MutabilityReason.PUBLISHED_NON_FINAL_FIELD));
        }
    }

}
