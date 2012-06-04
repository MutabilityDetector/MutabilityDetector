package org.mutabilitydetector.issues;

import static java.util.Arrays.asList;
import static org.mutabilitydetector.MutabilityReason.ABSTRACT_TYPE_TO_FIELD;
import static org.mutabilitydetector.issues.ImmutableConfiguration.MutabilityTest.AssumeCopiedIntoUnmodifiable.assuming;
import static org.mutabilitydetector.locations.Dotted.dotted;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ImmutableConfiguration {

    private final Map<String, Object> propMap;

    public ImmutableConfiguration(AbstractConfiguration origConfiguration) {

        Map store = new HashMap();
        // we need the variables interpolated, which doesn't come at the getProperty level
        Configuration interpolated = origConfiguration.interpolatedConfiguration();
        final Iterator keyIter = interpolated.getKeys();
        while (keyIter.hasNext()) {
            String key = (String) keyIter.next();
            store.put(key, interpolated.getProperty(key));
        }
        this.propMap = Collections.unmodifiableMap(store);
    }
    
    public Object getSomeProperty(String key) {
        return propMap.get(key);
    }

    public static interface Configuration {
        Iterator getKeys();
        Object getProperty(String key);
    }

    public static abstract class AbstractConfiguration {
        public abstract Configuration interpolatedConfiguration();
    }
    
    public static class MutabilityTest {
        @Test
        public void immutableConfiguration_assumeAnyMapIsImmutable() throws Exception {
            assertInstancesOf(ImmutableConfiguration.class, 
                              areImmutable(),
                              provided(Map.class).isAlsoImmutable());
        }
        
        @Test
        public void immutableConfiguration_assumeACopyFromUnmodifiableCollection() throws Exception {
            assertInstancesOf(ImmutableConfiguration.class, 
                              areImmutable(),
                              assuming("propMap").hasCollectionsUnmodifiableTypeAssignedToIt());
        }
        
        public static class AssumeCopiedIntoUnmodifiable extends BaseMatcher<MutableReasonDetail> {

            private static class Assuming {
                private final String fieldName;
                public Assuming(String fieldName) {
                    this.fieldName = fieldName;
                }
                public Matcher<MutableReasonDetail> hasCollectionsUnmodifiableTypeAssignedToIt() {
                    return new AssumeCopiedIntoUnmodifiable(fieldName);
                }
                
            }

            public static Assuming assuming(String fieldName) {
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

    }
}
