## What is Mutability Detector?
Mutability Detector is designed to analyse Java classes and report on whether instances of a given class are immutable. It can be used:

  * In a unit test, with an assertion like `assertImmutable(MyClass.class)`. Is your class actually immutable? What about after that change you just made?
  * From the command line. Do you want to quickly run Mutability Detector over an entire code base?
  * At runtime. Does your API require being given immutable objects?
  * As a FindBugs plugin. Those classes you annotated with `@Immutable`, are they actually?
  
## Getting Started
To use Mutability Detector directly, either from the command line, at runtime in your application, or as part of your unit tests, grab the jar available [here](https://github.com/MutabilityDetector/MutabilityDetector/downloads). For the FindBugs plugin, grab the MutabilityDetector4FindBugs jar, and configure it to be picked up by FindBugs during a normal analysis, as described [here](http://code.google.com/p/findbugs/wiki/DetectorPluginTutorial#Loading_Our_Plugin). 

### Unit Testing
Just add MutabilityDetector to your unit testing classpath. Adding your first assertion is as simple as:

    import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
    
    @Test public void checkMyClassIsImmutable() {
        assertImmutable(MyClass.class); 
    }
    
Though it is possible (or likely) that you will have to configure the assertion to deal with any false positives that arise. See the JavaDoc on `MutabilityAssert` for further information. (TODO: link to as-yet unhosted JavaDoc).

### Static Analysis

An example of how to run it is probably the most useful. If you want to analyse MyCodebase.jar use:

`java -jar MutabilityDetector.jar -classpath path/to/MyCodebase.jar`

Mutability Detector can handle jars as seen above, or directories of class files (thanks go to the authors of [classpath-explorer](http://code.google.com/p/classpath-explorer/)). So if your codebase was in the filesystem as directories and .class files, and the directory MyCodebase was the root of that, you could run:

`java -jar MutabilityDetector.jar -classpath path/to/MyCodebase`

The output is a list of the analysed classes against the result of asking "Is immutable?", ie. `IMMUTABLE`, `NOT_IMMUTABLE`, `EFFECTIVELY_IMMUTABLE`.

The command line has the following usage:


    $ java -jar MutabilityDetector-0.8.jar --help
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
    -v,--verbose                 Print details of analysis and reasons for
                                  results.





### Runtime Analysis

It is possible to use Mutability Detector at runtime. For example, consider if you have a library which requires that objects passed to it are immutable. On receiving such an object, you can ask Mutability Detector if it is actually immutable.

Check out the code snippet in [this example](https://github.com/MutabilityDetector/ClientOfMutabilityDetector/blob/master/src/main/java/org/mutabilitydetector/runtime/RuntimeAnalysisExample.java), which shows correct usage against trunk code.

#### Contact
Contact can be made either by leaving comments or by [email](mailto:Grundlefleck+md@gmail.com).