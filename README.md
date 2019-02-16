<a href="http://www.javadoc.io/doc/org.mutabilitydetector/MutabilityDetector"><img src="http://www.javadoc.io/badge/org.mutabilitydetector/MutabilityDetector.svg?label=latest_version" alt="Latest Version Javadocs"></a>

## What is Mutability Detector?
Mutability Detector is designed to analyse Java classes and report on whether instances of a given class are immutable. It can be used:

  * In a unit test, with an assertion like `assertImmutable(MyClass.class)`. Is your class actually immutable? What about after that change you just made?
  * As a FindBugs plugin. Those classes you annotated with `@Immutable`, are they actually?
  * At runtime. Does your API require being given immutable objects?
  * From the command line. Do you want to quickly run Mutability Detector over an entire code base?
  
  
  
  
## Why Try To Detect Mutability?

Developing classes to be immutable has several benefits. An immutable object is one which cannot be changed once it is constructed. While writing concurrent programs, using immutable objects can greatly simplify complex systems, as sharing an object across threads is much safer. There are a few rules for what makes an object immutable, and it is easy to break the rules and render the object unsafe. This could lead to subtle, hard-to-detect bugs which could lower the integrity of the system. Using an automated tool to recognise mutability where it's not intended can reduce the complexity of writing immutable classes.

Mutability Detector is in the very early stages of development. To give an idea of the performance, on a Thinkpad T61 the tool runs over the JVM's rt.jar (48MB, 17,000 classes) in under 2 minutes. The tool analyses on the strict side, very few classes are found to be perfectly immutable, for instance, java.lang.String and java.lang.Integer are not immutable because of a non-final field, and primitive array field, respectively. Mutability Detector will not be released under a 1.0 version until these cases can be correctly analysed.

If this sounds like it may be interesting or useful to you, continue reading for more information on getting started. You may also want to take a look at the [Mutability Detector Blog](http://mutability-detector.blogspot.co.uk/). 
  
  
## Getting Started
To use Mutability Detector directly, either from the command line, at runtime in your application, or as part of your unit tests, grab the jar available [from Maven Central](https://search.maven.org/remote_content?g=org.mutabilitydetector&a=MutabilityDetector&v=LATEST). Or you can declare it in your Maven-compatible build tool, with the following coordinates:

    <dependency>
        <groupId>org.mutabilitydetector</groupId>
        <artifactId>MutabilityDetector</artifactId>
        <version>[latest version here]</version>
        <scope>test</scope>
    </dependency>


### Using Mutability Detector in Unit Testing
Just add MutabilityDetector to your unit testing classpath. Adding your first assertion is as simple as:

    import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
    
    @Test public void checkMyClassIsImmutable() {
        assertImmutable(MyClass.class); 
    }
    
Though it is possible (or likely) that you will have to configure the assertion to deal with any false positives that arise. See the [JavaDoc on `MutabilityAssert`](http://mutabilitydetector.github.io/MutabilityDetector/mvn-site/apidocs/org/mutabilitydetector/unittesting/MutabilityAssert.html) for further information. 

### Using Mutability Detector from the Command Line

An example of how to run it is probably the most useful. If you want to analyse MyCodebase.jar use:

`java -jar MutabilityDetector.jar -classpath path/to/MyCodebase.jar`

Mutability Detector can handle jars as seen above, or directories of class files (thanks go to the authors of [classpath-explorer](http://code.google.com/p/classpath-explorer/)). So if your codebase was in the filesystem as directories and .class files, and the directory MyCodebase was the root of that, you could run:

`java -jar MutabilityDetector.jar -classpath path/to/MyCodebase`

The output is a list of the analysed classes against the result of asking "Is immutable?", ie. `IMMUTABLE`, `NOT_IMMUTABLE`, `EFFECTIVELY_IMMUTABLE`.

The command line has the following usage:


    $ java -jar MutabilityDetector-0.9.jar --help
    usage: MutabilityDetector
    -cl,--classlist <filename>   Only report results on the classes listed
                                  within <filename>. Currently this option
                                  only supports plain text files with one
                                  class per line. It is also rather limited in
                                  the format it accepts: each line must
                                  contain the equivalent of
                                  someClass.getName(), e.g. it must be
                                  java.lang.Integer, with dot delimiters and
                                  no suffixes such as .java or .class. Can be
                                  used in conjunction with -match to reduce
                                  the time taken to perform analysis.
    -cp,--classpath <path>       The classpath to be analysed by Mutability
                                  Detector
    -e,--reportErrors            Reports on errors in the analysis. Defaults
                                  to false.
    -f,--failFast                When true, encountering an unhandled
                                  exception will cause analysis to abort
                                  immediately. When false, exceptions during
                                  analysis of a particular class will be
                                  reflected in the result assigned to that
                                  class. Defaults to false.
    -h,--help                    print this message
    -m,--match <regex>           A regular expression used to match class
                                  names to analyse. This is matched against
                                  the fully qualified class name, minus the
                                  .class suffix (i.e. it matches against
                                  'java.lang.Object', not
                                  'java/lang/Object.class'). The default is
                                  '.*', meaning all classes will be analysed.
    -r,--report <arg>            Choose what is reported from the analysis.
                                  Valid options are [ALL|IMMUTABLE|MUTABLE].
                                  If not specified, or doesn't match an
                                  available mode, defaults to 'ALL'
    -s,--summary                 Show summary of analysis result.
    -v,--verbose                 Print details of analysis and reasons for
                                  results.





### Using Mutability Detector within Your Application

It is possible to use Mutability Detector at runtime. For example, consider if you have a library which requires that objects passed to it are immutable. On receiving such an object, you can ask Mutability Detector if it is actually immutable.

Check out the code snippet in [this example](https://github.com/MutabilityDetector/ClientOfMutabilityDetector/blob/master/src/main/java/org/mutabilitydetector/runtime/RuntimeAnalysisExample.java), which shows correct usage against trunk code.

### FindBugs Plugin

To have Mutability Detector inspect your classes during a FindBugs analysis, grab the MutabilityDetector4FindBugs jar, and configure it to be picked up by FindBugs during a normal analysis, as described [here](http://code.google.com/p/findbugs/wiki/DetectorPluginTutorial#Loading_Our_Plugin). 

Mutability Detector will perform it's analysis on any classes annotated with `@Immutable`.

MutabilityDetector4FindBugs is also available from Maven Central, with the following coordinates:


    <dependency>
        <groupId>org.mutabilitydetector</groupId>
        <artifactId>MutabilityDetector4FindBugs</artifactId>
        <version>0.9.3</version>
        <scope>test</scope>
    </dependency>

### Java 8 Compatibility
![Compatibility Badge](https://java.net/downloads/adoptopenjdk/compat.svg)

### Build Status
[![Build Status](https://api.travis-ci.org/MutabilityDetector/MutabilityDetector.png?branch=master)](https://travis-ci.org/MutabilityDetector/MutabilityDetector)

#### Contact
Contact can be made either by leaving comments, by [email](mailto:Grundlefleck+md@gmail.com), or through Gitter.

Gitter is available for OSX, Windows, Linux, iPhone, and Android. It can also be mirror over and IRC Bridge. To install Gitter head over to https://gitter.im/apps for your system. 
[![Join the chat at https://gitter.im/MutabilityDetector/main](https://badges.gitter.im/MutabilityDetector/main.svg)](https://gitter.im/MutabilityDetector/main?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


