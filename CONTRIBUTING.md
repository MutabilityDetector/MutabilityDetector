# Introduction

If you're looking to contribute to Mutability Detector in some way, this guide will let you know the best way to go about it.

## For users of Mutability Detector
If you use Mutability Detector in your own unit tests, you can help out in the following ways:
  * feedback - any and all feedback is welcome, and should be sent to the [mailing list](http://groups.google.com/group/mutability-detector). Whether you think Mutability Detector sucks, or is the bee's knees, I want to hear about it!
  * reporting issues - if you find a bug, from getting false positives or false negatives, or the API not letting you do what you want to do, file an issue on the open [bug tracker](https://github.com/MutabilityDetector/MutabilityDetector/issues)

## For potential developers of Mutability Detector
If you want to hack on the code, to fix a bug that's holding you back, or just to scratch an itch, you can get involved by sending a patch. If it's a feature or a bug fix, it should be accompanied by an issue in the bug tracker.

### Checkout code from version control
You can check out the code from GitHub. For the core analysis, you should clone both [Mutability Detector](https://github.com/MutabilityDetector/MutabilityDetector) and [ClientOfMutabilityDetector](https://github.com/MutabilityDetector/ClientOfMutabilityDetector). The former contains all the main code and test cases, the client project is used to help alert of breaking API changes, or any other regressions. 

To work on the FindBugs plugin, you should grab the source for [MutabilityDetector4FindBugs](https://github.com/MutabilityDetector/MutabilityDetector4FindBugs).

Patches are preferred in the form of GitHub pull requests. However, if you want to just throw together a patch and drop it on the mailing list, I'm happy with that too.

### Building code
With both projects checked out, after you've made your change, and with the Maven command (`mvn`) available, you should be able do the following:

    cd MutabilityDetector
    ./build/install-simpleverifier.sh
    mvn install  
    cd ../ClientOfMutabilityDetector  
    mvn test
 

And have both run successfully.

Beyond the shell script which just installs a library not available from Maven Central, everything else should just work as a standard Maven project. Including building the jar, generating IDE files, and running tests.

### Coding standards

Currently Mutability Detector has a high level of unit test coverage, and patches should come with unit tests. For things like formatting, and naming, please try to be consistent with what you see around the rest of the code base - I believe it's important for readability to keep the code consistent. I'll most likely apply my own formatting settings to your patch anyway, so you shouldn't have to worry too much about that. I'm quite willing to just receive any kind of patch you throw at me and take the time to fix it up in whatever way it needs.

Any questions or comments, or if this help guide is woefully insufficient, please get in touch, preferably on the [mailing list](http://groups.google.com/group/mutability-detector).

## Licensing
Mutability Detector is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html). Any patches which are incorporated will be included under this license.


