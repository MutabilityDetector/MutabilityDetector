package org.mutabilitydetector.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mutabilitydetector.cli.ClassNamesToAnalyseTest.DottedClassNameMatcher.aDottedClassNameOf;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.mutabilitydetector.cli.RunMutabilityDetector.ClassNamesToAnalyse;
import org.mutabilitydetector.locations.Dotted;

public class ClassNamesToAnalyseTest {

    @Test
    public void convertsSlashedClassResourceNamesToDotted() throws Exception {
        ClassNamesToAnalyse toAnalyse = new ClassNamesToAnalyse();
        BatchAnalysisOptions options = new CommandLineOptions(System.err, "-cp", "whatever");
        String[] resources = { "com/some/classfile/Whatever.class", };
        
        assertThat(toAnalyse.asDotted(options, resources), contains(aDottedClassNameOf("com.some.classfile.Whatever")));
    }
    
    public static class DottedClassNameMatcher extends TypeSafeDiagnosingMatcher<Dotted> {

        private final String expectedName;

        private DottedClassNameMatcher(String expectedName) {
            this.expectedName = expectedName;
        }
        
        public static DottedClassNameMatcher aDottedClassNameOf(String expected) {
            return new DottedClassNameMatcher(expected);
        }
        
        @Override
        public void describeTo(Description description) {
            description.appendText("a Dotted class name of ").appendValue(expectedName);
        }

        @Override
        protected boolean matchesSafely(Dotted dotted, Description mismatchDescription) {
            if (!dotted.asString().equals(expectedName)) {
                mismatchDescription.appendText("a Dotted class name of ").appendValue(dotted.asString());
                return false;
            } else {
                return true;
            }
        }
        
    }
    
}
