package org.mutabilitydetector.checkers;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.mutabletofield.array.MutableByHavingArrayTypeAsField;
import org.mutabilitydetector.benchmarks.types.AbstractType;
import org.mutabilitydetector.benchmarks.visibility.AlmostEffectivelyImmutable;
import org.mutabilitydetector.locations.ClassLocation;
import org.mutabilitydetector.locations.CodeLocation;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.locations.FieldLocation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Marc Gomez
 *
 */
public class CheckerReasonLocationTest {

    @Test
    public void locationForArrayFieldMutabilityCheckerDisplaysFieldAndClass() {
        ArrayFieldMutabilityChecker checker = new ArrayFieldMutabilityChecker();
        Class<MutableByHavingArrayTypeAsField> classToAnalyse = MutableByHavingArrayTypeAsField.class;
        String arrayFieldName = "names";

        AnalysisResult analysisResult = TestUtil.runChecker(checker, classToAnalyse);

        assertCodeLocationDisplaysFieldAndClass(classToAnalyse, arrayFieldName, analysisResult);
    }


    @Test
    public void locationForCanSubclassCheckerDisplaysClass() {
        CanSubclassChecker checker = new CanSubclassChecker();
        Class<AlmostEffectivelyImmutable> classToAnalyse = AlmostEffectivelyImmutable.class;

        AnalysisResult analysisResult = TestUtil.runChecker(checker, classToAnalyse);

        assertCodeLocationDisplaysClass(classToAnalyse, analysisResult);
    }


    @Test
    public void locationForInherentTypeMutabilityCheckerDisplaysClass() {
        InherentTypeMutabilityChecker checker = new InherentTypeMutabilityChecker();
        Class<AbstractType> classToAnalyse = AbstractType.class;

        AnalysisResult analysisResult = TestUtil.runChecker(checker, classToAnalyse);

        assertCodeLocationDisplaysClass(classToAnalyse, analysisResult);
    }


    private void assertCodeLocationDisplaysClass(Class<?> classToAnalyse, AnalysisResult analysisResult) {
        assertThat(analysisResult.reasons.size(), is(1));
        CodeLocation actualCodeLocation = Iterables.getOnlyElement(analysisResult.reasons).codeLocation();
        CodeLocation expectedCodeLocation = ClassLocation.from(Dotted.fromClass(classToAnalyse));
        assertThat(actualCodeLocation, equalTo(expectedCodeLocation));
    }

    private void assertCodeLocationDisplaysFieldAndClass(Class<MutableByHavingArrayTypeAsField> classToAnalyse, String arrayFieldName, AnalysisResult analysisResult) {
        assertThat(analysisResult.reasons.size(), is(1));
        CodeLocation actualCodeLocation = Iterables.getOnlyElement(analysisResult.reasons).codeLocation();
        CodeLocation expectedCodeLocation = new FieldLocation(arrayFieldName, ClassLocation.from(Dotted.fromClass(classToAnalyse)));
        assertThat(actualCodeLocation, equalTo(expectedCodeLocation));
    }
}


