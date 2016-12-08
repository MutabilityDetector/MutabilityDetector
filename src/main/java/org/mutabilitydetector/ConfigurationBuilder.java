package org.mutabilitydetector;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy;
import org.mutabilitydetector.checkers.MethodIs;
import org.mutabilitydetector.checkers.MutabilityAnalysisException;
import org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice;
import org.mutabilitydetector.checkers.info.CopyMethod;
import org.mutabilitydetector.config.HardcodedResultsUsage;
import org.mutabilitydetector.locations.ClassNameConverter;
import org.mutabilitydetector.locations.Dotted;
import org.mutabilitydetector.unittesting.MutabilityAssert;
import org.objectweb.asm.Type;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.checkers.MutabilityCheckerFactory.ReassignedFieldAnalysisChoice.NAIVE_PUT_FIELD_ANALYSIS;
import static org.mutabilitydetector.config.HardcodedResultsUsage.LOOKUP_WHEN_REFERENCED;
import static org.mutabilitydetector.locations.ClassNameConverter.CONVERTER;
import static org.mutabilitydetector.locations.Dotted.dotted;

/**
 * Builds a {@link Configuration} for customising Mutability Detector's analysis.
 * 
 * The most significant feature of {@link ConfigurationBuilder} is to allow
 * defining hardcoded results for particular classes, which should be respected
 * during analysis. For more details, see {@link MutabilityAssert}.
 * <p>
 * 
 * Users should subclass {@link ConfigurationBuilder} and override the
 * {@link #configure()} method, which should then be used to construct a
 * MutabilityAsserter instance.
 * 
 * For example:
 * 
 * <pre>
 * <code>
 * MutabilityAsserter myAsserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
 *   &#064;Override public void configure() {
 *     
 *     hardcodeAsDefinitelyImmutable(SomeClass.class);
 *     setExceptionPolicy(ExceptionPolicy.CARRY_ON);
 *     
 *     mergeHardcodedResultsFrom(ConfigurationBuilder.DEFAULT_CONFIGURATION);
 *   }
 * });
 * </code>
 * </pre>
 * 
 * This class also provides an out-of-the-box configuration with hardcoded
 * results for common JDK classes. This includes classes where Mutability
 * Detector's analysis is incorrect, for example, java.lang.String,
 * java.lang.Integer (and other primitive wrapper types) and
 * java.math.BigDecimal.
 * 
 * @see Configurations#JDK_CONFIGURATION
 * 
 */
@NotThreadSafe
public abstract class ConfigurationBuilder {
    
    /**
     * Subclasses should override this method to configure analysis.
     * <p>
     * It is recommended that any custom {@link Configuration}'s merge with the
     * {@link Configurations#OUT_OF_THE_BOX_CONFIGURATION} in order to remain consistent with
     * {@link MutabilityAssert}, and the command line settings. For example:
     * 
     * <pre>
     * <code>
     * MutabilityAsserter myAsserter = MutabilityAsserter.configured(new ConfigurationBuilder() {
     *   &#064;Override public void configure() {
     *     mergeHardcodedResultsFrom(ConfigurationBuilder.OUT_OF_THE_BOX_CONFIGURATION);
     *   }
     * });
     * </code>
     * </pre>
     * Similarly for {@link DefaultCachingAnalysisSession#createWithCurrentClassPath(Configuration)}
     * <p>
     * The available configuration methods are listed below.
     * 
     * @see #hardcodeResult(AnalysisResult)
     * @see #hardcodeResults(AnalysisResult...)
     * @see #hardcodeResults(Iterable)
     * @see #hardcodeAsDefinitelyImmutable(Class)
     * @see #hardcodeAsDefinitelyImmutable(String)
     *
     * @see #setHowToUseHardcodedResults(HardcodedResultsUsage)
     *
     * @see #mergeHardcodedResultsFrom(Configuration)
     * @see #setExceptionPolicy(ExceptionPolicy)
     * 
     * @see Configurations#OUT_OF_THE_BOX_CONFIGURATION
     */
    public abstract void configure();
    
    private static final Equivalence<AnalysisResult> CLASSNAME_EQUIVALENCE = Equivalence.equals().onResultOf(AnalysisResult.TO_CLASSNAME);

    private static final Function<AnalysisResult, Wrapper<AnalysisResult>> TO_CLASSNAME_EQUIVALENCE_WRAPPER = 
        new Function<AnalysisResult, Wrapper<AnalysisResult>>() {
            @Override public Wrapper<AnalysisResult> apply(AnalysisResult input) {
                return CLASSNAME_EQUIVALENCE.wrap(input);
            }
    };

    private static final Function<Wrapper<AnalysisResult>, AnalysisResult> UNWRAP = new Function<Wrapper<AnalysisResult>, AnalysisResult>() {
        @Override public AnalysisResult apply(Wrapper<AnalysisResult> input) { return input.get(); }
    };


    public final Configuration build() {
        configure();
        return new DefaultConfiguration(
                hardcodedResults.build(),
                hardcodedImmutableContainerClasses.build(),
                exceptionPolicy,
                reassignedFieldAlgorithm,
                validCopyMethods.build(),
                howToUseHardcodedResults);
    }
    
    private ImmutableSet.Builder<AnalysisResult> hardcodedResults = ImmutableSet.builder();
    private ImmutableSet.Builder<Dotted> hardcodedImmutableContainerClasses = ImmutableSet.builder();
    private ExceptionPolicy exceptionPolicy = FAIL_FAST;
    private ReassignedFieldAnalysisChoice reassignedFieldAlgorithm = NAIVE_PUT_FIELD_ANALYSIS;
    private ImmutableSetMultimap.Builder<String,CopyMethod> validCopyMethods = ImmutableSetMultimap.builder();
    private HardcodedResultsUsage howToUseHardcodedResults = LOOKUP_WHEN_REFERENCED;

    
    /**
     * Configures how Mutability Detector's analysis should respond to
     * exceptions during analysis.
     * <p>
     * During analysis, an exception may occur which is recoverable. That is,
     * Mutability Detector is able to continue it's analysis, and <b>may</b>
     * produce valid results.
     * <p>
     * The default behaviour is to use {@link ExceptionPolicy#FAIL_FAST},
     * meaning any unhandled exceptions will propogate up past the assertion,
     * and cause a failing test.
     * <p>
     * Setting this configuration flag to {@link ExceptionPolicy#CARRY_ON} may
     * allow unit tests to function where exceptions don't necessarily preclude
     * a useful output. For example, consider a class which you wish to make
     * immutable; a test for that class fails with an unhandled exception. If
     * that test has, say 10 reasons for mutability, and 1 of those causes the
     * test to abort with an exception, you have just lost out on 90% of the
     * required information. {@link ExceptionPolicy#CARRY_ON} will allow the
     * test to report 9 out of 10 reasons. The test may be useful, although it
     * won't be comprehensive.
     * <p>
     * 
     * If you are unlucky enough to have a test which results in an exception,
     * please report it to the Mutability Detector project, at the <a
     * href="https
     * ://github.com/MutabilityDetector/MutabilityDetector/issues">project
     * homepage</a>.
     * 
     * 
     * @see Configuration#exceptionPolicy()
     * @param exceptionPolicy
     *            - how to respond to exceptions during analysis. Defaults to
     *            {@link ExceptionPolicy#FAIL_FAST}
     */
    protected final void setExceptionPolicy(ExceptionPolicy exceptionPolicy) {
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Add a predefined result used during analysis.
     * <p>
     * Hardcoding a result means that information queried about a class will
     * honour the result you have set. For example, if during analysis,
     * Mutability Detector has to discover whether a field type is mutable or
     * not. However, requesting the {@link AnalysisResult} of the class in
     * question directly will return the real result from the actual analysis.
     * This holds for unit tests, command line runs, and runtime analysis. As
     * such, calling this method will have no effect when querying an
     * AnalysisResult directly.
     * 
     * @see Configuration#hardcodedResults()
     * @see AnalysisResult
     * @see AnalysisSession#resultFor(org.mutabilitydetector.locations.Dotted)
     */
    protected final void hardcodeResult(AnalysisResult result) {
        hardcodedResults.add(result);
    }

    /**
     * Adds all the given AnalysisResults, as if hardcodeResult was called for
     * each element in the iterable.
     * 
     * @see AnalysisResult
     * @see #hardcodeResult(AnalysisResult)
     */
    protected final void hardcodeResults(Iterable<AnalysisResult> result) {
        hardcodedResults.addAll(result);
    }

    /**
     * Adds all the given AnalysisResults, as if hardcodeResult was called for
     * each element in the var args parameter.
     * 
     * @see AnalysisResult
     * @see #hardcodeResult(AnalysisResult)
     */
    protected final void hardcodeResults(AnalysisResult... result) {
        hardcodeResults(Arrays.asList(result));
    }
    
    /**
     * Hardcodes a result indicating the given class is Immutable.
     * 
     * @see AnalysisResult#definitelyImmutable(String)
     */
    protected final void hardcodeAsDefinitelyImmutable(Class<?> immutableClass) {
        hardcodeResult(AnalysisResult.definitelyImmutable(Dotted.fromClass(immutableClass)));
    }

    /**
     * Hardcodes a result indicating the given class is Immutable.
     * 
     * The most reliable format of class name would be the dotted version of the
     * fully qualified name, e.g. java.lang.String. However, there will be a
     * "best effort" attempt to accept other formats, e.g. java/lang/String, or
     * java.lang.String.class.
     * 
     * @see ClassNameConverter
     * @see AnalysisResult#definitelyImmutable(String)
     */
    protected final void hardcodeAsDefinitelyImmutable(String immutableClassName) {
        hardcodeResult(AnalysisResult.definitelyImmutable(dotted(immutableClassName)));
    }

    /**
     * Specifies how hardcoded results should be used.
     * <br>
     * The default is {@link HardcodedResultsUsage#LOOKUP_WHEN_REFERENCED} which was the implicit behaviour in
     * Mutability Detector prior to the introduction of this configuration setting.
     *
     * @see HardcodedResultsUsage#LOOKUP_WHEN_REFERENCED
     */
    protected final void setHowToUseHardcodedResults(HardcodedResultsUsage usage) {
        this.howToUseHardcodedResults = usage;
    }
    
    /**
     * Returns an immutable snapshot of the hardcoded results as at time of calling.
     * 
     * Note changes the returned Set will not allow modifications, and will not 
     * reflect changes to the underlying configuration.
     */
    protected final Set<AnalysisResult> getCurrentlyHardcodedResults() {
        return hardcodedResults.build();
    }

    /**
     * Configures a generic container type as immutable.
     *
     * Should be used for classes which, while immutable, contain potentially mutable elements. Mutability Detector's
     * analysis will warn on classes which use immutable container classes parameterised with mutable types, but allow
     * container classes parameterised with immutable types.
     *
     * For example:
     * <code>
     *     private final ImmutableContainer&lt;java.util.Date&gt; // considered mutable
     *     private final ImmutableContainer&lt;GENERIC_TYPE_VARIABLE&gt; // considered mutable
     *     private final ImmutableContainer&lt;?&gt; // considered mutable
     *     private final ImmutableContainer&lt;String&gt; // considered immutable
     * </code>
     *
     * A good example of an immutable container type is JDK 8's java.util.Optional. It has a single, final field, and
     * cannot be subclassed. However, the generic parameter allows an instance of Optional to contain a mutable type,
     * which can be modified by any code with a reference. However, if a class has an Optional field containing an
     * immutable type, e.g. <code>Optional&lt;String&gt;</code> then it should not cause the class to be considered
     * mutable.
     *
     * Be default java.util.Optional is considered an immutable container class.
     *
     * @param immutableContainerClass class that is immutable as long as contained elements are immutable
     * @see MutabilityReason#COLLECTION_FIELD_WITH_MUTABLE_ELEMENT_TYPE
     */
    protected final void hardcodeAsImmutableContainerType(Class<?> immutableContainerClass) {
        hardcodeAsImmutableContainerType(Dotted.fromClass(immutableContainerClass));
    }

    protected final void hardcodeAsImmutableContainerType(String immutableContainerClassName) {
        hardcodeAsImmutableContainerType(dotted(CONVERTER.dotted(immutableContainerClassName)));

    }

    private void hardcodeAsImmutableContainerType(Dotted immutableContainerClassName) {
        hardcodedImmutableContainerClasses.add(immutableContainerClassName);
    }

    /**
     * Merges the hardcoded results of this Configuration with the given
     * Configuration.
     * 
     * The resultant hardcoded results will be the union of the two sets of
     * hardcoded results. Where the AnalysisResult for a class is found in both
     * Configurations, the result from otherConfiguration will replace the
     * existing result in this Configuration. This replacement behaviour will
     * occur for subsequent calls to
     * {@link #mergeHardcodedResultsFrom(Configuration)}.
     * 
     * @param otherConfiguration - Configuration to merge hardcoded results with.
     */
    protected void mergeHardcodedResultsFrom(Configuration otherConfiguration) {
        Set<Wrapper<AnalysisResult>> union = 
                Sets.union(copyOf(transform(otherConfiguration.hardcodedResults().values(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)),
                           newHashSet(transform(hardcodedResults.build(), TO_CLASSNAME_EQUIVALENCE_WRAPPER)));
        
        Set<AnalysisResult> result = copyOf(transform(union, UNWRAP));
        
        hardcodedResults = ImmutableSet.<AnalysisResult>builder().addAll(result);
    }

    /**
     * Merges the immutable container types of this Configuration with the given
     * Configuration.
     *
     * The resultant immutable container types results will be the union of the two sets of
     * immutable container types. Where the type is found in both
     * Configurations, the result from otherConfiguration will replace the
     * existing result in this Configuration. This replacement behaviour will
     * occur for subsequent calls to
     * {@link #mergeImmutableContainerTypesFrom(Configuration)} .
     *
     * @param otherConfiguration - Configuration to merge immutable container types with.
     */
    protected void mergeImmutableContainerTypesFrom(Configuration otherConfiguration) {
        Set<Dotted> union =
                Sets.union(hardcodedImmutableContainerClasses.build(), otherConfiguration.immutableContainerClasses());

        hardcodedImmutableContainerClasses = ImmutableSet.<Dotted>builder().addAll(union);
    }
    
    /**
     * Merge valid copy methods from another configuration.
     * 
     * @param otherConfiguration - configuration to merge harcoded copy methods from.
     */
    protected void mergeValidCopyMethodsFrom(Configuration otherConfiguration) {
        validCopyMethods.putAll(otherConfiguration.hardcodedCopyMethods());
    }

    /**
     * Merges configuration from another configuration with this one.
     *
     * As with other merge methods, where the same setting is present in both configurations, the settings from
     * <code>other</code> will be applied.
     *
     * Merging applies only to the configuration applied to certain classes, such as hardcoded results. It does not
     * change <code>this<code> <code>Configuration</code>'s setting for e.g. @{link #setExceptionPolicy}.
     *
     *
     * @param otherConfiguration configuration to merge from
     * @see #mergeHardcodedResultsFrom(Configuration)
     * @see #mergeImmutableContainerTypesFrom(Configuration)
     * @see #mergeValidCopyMethodsFrom(Configuration)
     */
    protected void merge(Configuration otherConfiguration) {
        mergeHardcodedResultsFrom(otherConfiguration);
        mergeImmutableContainerTypesFrom(otherConfiguration);
        mergeValidCopyMethodsFrom(otherConfiguration);
    }

    protected void useAdvancedReassignedFieldAlgorithm() {
        this.reassignedFieldAlgorithm = ReassignedFieldAnalysisChoice.LAZY_INITIALISATION_ANALYSIS;
    }
    
    /**
     * Hardcode a copy method as being valid. This should be used to tell Mutability Detector about
     * a method which copies a collection, and when the copy can be wrapped in an immutable wrapper
     * we can consider the assignment immutable. Useful for allowing Mutability Detector to correctly
     * work with other collections frameworks such as Google Guava. Reflection is used to obtain the
     * method's descriptor and to verify the method's existence.
     * 
     * @param fieldType - the type of the field to which the result of the copy is assigned
     * @param fullyQualifiedMethodName - the fully qualified method name
     * @param argType - the type of the argument passed to the copy method
     * 
     * @throws MutabilityAnalysisException - if the specified class or method does not exist
     * @throws IllegalArgumentException - if any of the arguments are null
     */
    protected final void hardcodeValidCopyMethod(Class<?> fieldType, String fullyQualifiedMethodName, Class<?> argType) {
        if (argType==null || fieldType==null || fullyQualifiedMethodName==null) {
            throw new IllegalArgumentException("All parameters must be supplied - no nulls");
        }
        String className = fullyQualifiedMethodName.substring(0, fullyQualifiedMethodName.lastIndexOf("."));
        String methodName = fullyQualifiedMethodName.substring(fullyQualifiedMethodName.lastIndexOf(".")+1);

        
        String desc = null;
        try {
            if (MethodIs.aConstructor(methodName)) {
                Constructor<?> ctor = Class.forName(className).getDeclaredConstructor(argType);
                desc = Type.getConstructorDescriptor(ctor);
            } else {
                Method method = Class.forName(className).getMethod(methodName, argType);
                desc = Type.getMethodDescriptor(method);
            }
        } catch (NoSuchMethodException e) {
            rethrow("No such method", e);
        } catch (SecurityException e) {
            rethrow("Security error", e);
        } catch (ClassNotFoundException e) {
            rethrow("Class not  found", e);
        }
        CopyMethod copyMethod = new CopyMethod(dotted(className), methodName, desc);
        hardcodeValidCopyMethod(fieldType, copyMethod);
    }
    
    protected void hardcodeValidCopyMethod(Class<?> fieldType, CopyMethod copyMethod) {
        validCopyMethods.put(fieldType.getCanonicalName(), copyMethod);
    }

    public Multimap<String,CopyMethod> getCopyMethodsAllowed() {
        return validCopyMethods.build();
    }
    
    private void rethrow(String message, Throwable e) {
        throw new MutabilityAnalysisException("Error in configuration: "+message+": "+e.getMessage(), e);
    }
    
    @Immutable
    private static final class DefaultConfiguration implements Configuration {

        private final ImmutableSet<AnalysisResult> hardcodedResults;
        private final HardcodedResultsUsage howToUseHardcodedResults;
        private final ImmutableMap<Dotted, AnalysisResult> resultsByClassname;
        private final ImmutableSet<Dotted> immutableContainerClasses;
        private final ImmutableSetMultimap<String, CopyMethod> validCopyMethods;

        private final ExceptionPolicy exceptionPolicy;
        private final ReassignedFieldAnalysisChoice reassignedFieldAlgorithm;

        private DefaultConfiguration(ImmutableSet<AnalysisResult> predefinedResults,
                                     ImmutableSet<Dotted> immutableContainerClasses,
                                     ExceptionPolicy exceptionPolicy,
                                     ReassignedFieldAnalysisChoice reassignedFieldAlgorithm,
                                     ImmutableSetMultimap<String, CopyMethod> validCopyMethods,
                                     HardcodedResultsUsage howToUseHardcodedResults) {
            this.immutableContainerClasses = immutableContainerClasses;
            this.exceptionPolicy = exceptionPolicy;
            this.hardcodedResults = predefinedResults;
            this.howToUseHardcodedResults = howToUseHardcodedResults;
            this.resultsByClassname = Maps.uniqueIndex(hardcodedResults, AnalysisResult.TO_DOTTED_CLASSNAME);
            this.reassignedFieldAlgorithm = reassignedFieldAlgorithm;
            this.validCopyMethods = validCopyMethods;
        }

        @Override
        public Map<Dotted, AnalysisResult> hardcodedResults() {
            return resultsByClassname;
        }

        @Override
        public HardcodedResultsUsage howToUseHardcodedResults() {
            return howToUseHardcodedResults;
        }

        @Override
        public Set<Dotted> immutableContainerClasses() { return immutableContainerClasses; }

        @Override
        public ExceptionPolicy exceptionPolicy() {
            return exceptionPolicy;
        }

        @Override
        public ReassignedFieldAnalysisChoice reassignedFieldAlgorithm() {
            return reassignedFieldAlgorithm;
        }
        
        @Override
        public ImmutableSetMultimap<String, CopyMethod> hardcodedCopyMethods() {
            return validCopyMethods;
        }
        
    }
    
    

}
